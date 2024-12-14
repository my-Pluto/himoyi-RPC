package com.himoyi.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.himoyi.Config.RpcConfig;
import com.himoyi.RpcApplication;
import com.himoyi.constant.RpcConstant;
import com.himoyi.loadbalancer.LoadBalancerFactory;
import com.himoyi.model.RpcRequest;
import com.himoyi.model.RpcResponse;
import com.himoyi.model.ServiceMetaInfo;
import com.himoyi.protocol.*;
import com.himoyi.registry.Registry;
import com.himoyi.registry.RegistryFactory;
import com.himoyi.serializer.JdkSerializer;
import com.himoyi.serializer.Serializer;
import com.himoyi.serializer.SerializerFactory;
import com.himoyi.server.tcp.VertxTCPClient;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 服务代理类（JDK动态代理）
 */
@Slf4j
public class ServiceProxy implements InvocationHandler {


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        // 构造请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .parameters(args)
                .build();

        try {
            // 从注册中心获取服务信息
            ServiceMetaInfo serviceMetaInfo = getServiceMetaInfo(method.getDeclaringClass().getName());

            System.out.println(serviceMetaInfo.getServiceAddress());

            // 发送请求，返回结果
            return VertxTCPClient.doRequest(serviceMetaInfo, rpcRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 从服务中心根据服务名获取一个服务地址
     *
     * @param serviceName
     * @return
     */
    private ServiceMetaInfo getServiceMetaInfo(String serviceName) {
        Registry registry = RegistryFactory.getRegistry(RpcApplication.getRpcConfig().getRegistryConfig().getRegistry());

        List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(String.format("%s:%s", serviceName, RpcConstant.DEFAULT_SERVICE_VERSION));

        if (CollUtil.isEmpty(serviceMetaInfos)) {
            log.error("暂无服务地址");
            throw new RuntimeException("暂无服务地址");
        }

        // 构造参数map，使用负载均衡器选择一个服务地址
        // 如果调用同一个方法，请求的地址一定是同一个
        Map<String, Object> map = new HashMap<>();
        map.put("serviceName", serviceName);
        // 为了避免所有请求落到同一个服务器，添加一个随机数
        map.put("UUID", IdUtil.simpleUUID());
        return LoadBalancerFactory.getLoadBalancer(RpcApplication.getRpcConfig().getLoadBalancer()).select(map, serviceMetaInfos);
    }


}
