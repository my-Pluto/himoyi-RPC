package com.himoyi.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.himoyi.Config.RegistryConfig;
import com.himoyi.RpcApplication;
import com.himoyi.constant.RpcConstant;
import com.himoyi.exception.registry.RpcHeartBeatException;
import com.himoyi.exception.registry.RpcOfflineException;
import com.himoyi.exception.registry.RpcServiceDiscoveryException;
import com.himoyi.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.lease.LeaseGrantResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class EtcdRegistry implements Registry {

    private Client client;

    private KV kvClient;

    private Lease leaseClient;

    // 本地已注册服务key的缓存
    private final Set<String> localRegistryNodeKeySet = new HashSet<>();

    /**
     * 租约以及对应的信息
     */
    private final Map<String, Long> leaseGrantResponseMap = new ConcurrentHashMap<>();

    // 元数据缓存对象
    private final RegistryServiceCache cache = new RegistryServiceCache();

    // 已经被监听的服务
    private final Set<String> watchingServiceKeySet = new ConcurrentHashSet<>();

    /**
     * 缓存每个服务的token
     */
    private final Map<String, String> tokenMap = new ConcurrentHashMap<>();


    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder()
                .endpoints(registryConfig.getHost() + ":" + registryConfig.getPort())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient = client.getKVClient();
        leaseClient = client.getLeaseClient();

        // 开启心跳检测
        heartbeat();
    }

    @Override
    public void register(ServiceMetaInfo metaInfo) throws Exception {

        // 生成一个30s的租约
        long id = leaseClient.grant(30).get().getID();

        // 构造服务信息
        String registerKey = RpcConstant.ETCD_ROOT_PATH + metaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(metaInfo), StandardCharsets.UTF_8);

        // 将键值对与租约关联起来，并设置过期时间
        // 这个代码片段的主要作用是为Put操作设置一个租约（Lease）。在ETCD中，租约是一种机制，用于控制键值对的生命周期。通过设置租约ID，Put操作会将键值对与这个租约关联起来
        // 当租约到期时，与该租约关联的所有键值对将被自动删除。这对于管理临时数据或需要自动清理的键值对非常有用
        PutOption build = PutOption.builder().withLeaseId(id).build();

//        注册
        kvClient.put(key, value, build).get();

        // 添加到本地缓存
        localRegistryNodeKeySet.add(registerKey);

        // 注册租约信息
        leaseGrantResponseMap.put(registerKey, id);

        // 缓存本地token信息
        if (RpcApplication.getRpcConfig().isTokenAuth()) {
            tokenMap.put(metaInfo.getServiceName(), metaInfo.getToken());
        }

        log.info("register service: {} success", registerKey);
    }

    @Override
    public void unRegister(ServiceMetaInfo metaInfo) throws Exception {

        // 构造要删除的key
        String unregisterKey = RpcConstant.ETCD_ROOT_PATH + metaInfo.getServiceNodeKey();

        // 删除
        kvClient.delete(ByteSequence.from(unregisterKey, StandardCharsets.UTF_8)).get();

        // 从本地缓存中删除
        localRegistryNodeKeySet.remove(unregisterKey);
        // 撤销租约
        leaseClient.revoke(leaseGrantResponseMap.get(unregisterKey)).get();
        // 删除本地租约缓存
        leaseGrantResponseMap.remove(unregisterKey);

        if (RpcApplication.getRpcConfig().isTokenAuth()) {
            tokenMap.remove(metaInfo.getServiceName());
        }


        log.info("unregister service: {} success", metaInfo.getServiceNodeKey());
    }

    @Override
    public void destroy() {

        // 把所有节点从注册中心下线
        for (String key : localRegistryNodeKeySet) {
            try {
                kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get();
            } catch (Exception e) {
                log.error("{} 节点下线失败", key, e);
                throw new RpcOfflineException("节点下线失败！");
            }
        }

        // 关闭所有服务
        if (ObjectUtil.isNotNull(kvClient)) {
            kvClient.close();
        }

        if (ObjectUtil.isNotNull(client)) {
            client.close();
        }

        log.error("当前节点下线！");
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {

        // 先检查缓存中是否存在数据
        List<ServiceMetaInfo> list = cache.getCache(serviceKey);
        if (!CollUtil.isEmpty(list)) {
            return list;
        }

        // 构造要查询的服务前缀（key）
        String prefix = RpcConstant.ETCD_ROOT_PATH + serviceKey + "/";

        try {
            // 在ETCD中，GetOption是用于配置Get操作的选项类。
            // 主要作用是配置Get操作以使用前缀匹配。设置isPrefix(true)表示在获取键值对时，ETCD会返回所有以指定键为前缀的键值对。
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> serviceNodeList = kvClient.get(
                            ByteSequence.from(prefix, StandardCharsets.UTF_8),
                            getOption) // get方法返回的是一个CompletableFuture<GetResponse>，表示异步操作的结果
                    .get() // .get()方法是阻塞调用，等待异步操作完成并返回结果。这里会阻塞当前线程，直到get操作完成
                    .getKvs(); // getKvs()方法从GetResponse中获取所有匹配的键值对，返回一个List<KeyValue>

            list = serviceNodeList.stream().map(keyValue -> {
                // 添加监听机制
                String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                watch(key);

                String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                return JSONUtil.toBean(value, ServiceMetaInfo.class);
            }).collect(Collectors.toList());

            // 写入缓存
            cache.putCache(serviceKey, list);

            return list;
        } catch (Exception e) {
            log.error("获取服务列表失败, 前缀为 {}", prefix);
            throw new RpcServiceDiscoveryException("获取服务列表失败");
        }
    }

    @Override
    public void watch(String serviceKey) {
        Watch watchClient = client.getWatchClient();

        boolean newWatch = watchingServiceKeySet.add(serviceKey);

        if (newWatch) {
            watchClient.watch(ByteSequence.from(serviceKey, StandardCharsets.UTF_8), watchResponse -> {
                for (WatchEvent event : watchResponse.getEvents()) {
                    switch (event.getEventType()) {
                        case DELETE -> {
                            cache.clearCache(serviceKey);
                        }
                        case PUT -> {
//                            cache.clearCache(serviceKey);
//                            serviceDiscovery(serviceKey);
                        }
                        default -> {
                        }
                    }
                }
            });
        }
    }

    @Override
    public void heartbeat() {
        CronUtil.schedule("*/10 * * * * *", new Task() {
            @Override
            public void execute() {

                // 循环抽取本地缓存进行重新注册
                for (String registryNodeKey : localRegistryNodeKeySet) {
                    try {

                        // 检查是否存活
                        List<KeyValue> kvs = kvClient.get(ByteSequence.from(registryNodeKey, StandardCharsets.UTF_8)).get().getKvs();

                        // 构造元信息对象，重新注册以续约
                        KeyValue keyValue = kvs.get(0);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo metaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
//
//                        register(metaInfo);
                        Long id = leaseGrantResponseMap.get(RpcConstant.ETCD_ROOT_PATH + metaInfo.getServiceNodeKey());

                        // 如果节点已经失活，需要重启节点排除故障后才能重新注册
                        if (ObjectUtil.isNull(kvs) || CollUtil.isEmpty(kvs)) {
                            leaseGrantResponseMap.remove(RpcConstant.ETCD_ROOT_PATH + metaInfo.getServiceNodeKey());
                            continue;
                        }


                        if (ObjectUtil.isNotNull(id)) {
                            client.getLeaseClient().keepAliveOnce(id);
                        }
                    } catch (Exception e) {
                        log.error("{} 服务续签失败", registryNodeKey);
                        throw new RpcHeartBeatException("续签失败！");
                    }
                }
            }
        });

        // 开启秒级定时任务，第一位为秒
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    /**
     * 获取token
     *
     * @param key
     * @return
     */
    @Override
    public String getToken(String key) {
        if (RpcApplication.getRpcConfig().isTokenAuth()) {
            return tokenMap.get(key);
        }
        return "";
    }
}
