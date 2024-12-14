package com.himoyi.loadbalancer;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.HashUtil;
import cn.hutool.core.util.ObjectUtil;
import com.himoyi.model.ServiceMetaInfo;

import java.util.*;

/**
 * 一致性Hash负载均衡器
 */
public class ConsistentHashLoadBalancer implements LoadBalancer {

    // 一致性hash虚拟节点，用于构建一致性hash环
    private final TreeMap<Integer, ServiceMetaInfo> virtualNodes = new TreeMap<>();

    // 虚拟节点的数量
    private static final int VIRTUAL_NODE_COUNT = 100;

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {

        // 如果不存在服务节点
        if (CollUtil.isEmpty(serviceMetaInfoList)) {
            return null;
        }

        // 构造虚拟节点环
        for (ServiceMetaInfo serviceMetaInfo : serviceMetaInfoList) {

            // 每个服务节点构造100个虚拟节点放置在环上
            for (int i = 0; i < VIRTUAL_NODE_COUNT; i++) {
                // 计算hash
                int hash = getHash(serviceMetaInfo.getServiceAddress() + "#" + i);
                virtualNodes.put(hash, serviceMetaInfo);
            }
        }

        // 获取调用请求的hash
        int hash = getHash(requestParams.toString());

        // 获取最近的且大于等于调用请求的hash的虚拟节点
        Map.Entry<Integer, ServiceMetaInfo> serviceMetaInfoEntry = virtualNodes.ceilingEntry(hash);
        if (ObjectUtil.isNull(serviceMetaInfoEntry)) {

            // 如果没有对应的节点，则返回圆环首部的节点
            serviceMetaInfoEntry = virtualNodes.firstEntry();
        }

        return serviceMetaInfoEntry.getValue();
    }

    /**
     * hash算法
     *
     * @param key
     * @return
     */
    private int getHash(Object key) {
        return HashUtil.fnvHash(key.toString());
    }
}
