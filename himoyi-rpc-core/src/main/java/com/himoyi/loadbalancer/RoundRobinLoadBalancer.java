package com.himoyi.loadbalancer;

import cn.hutool.core.collection.CollUtil;
import com.himoyi.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询负载均衡
 */
public class RoundRobinLoadBalancer implements LoadBalancer {

    /**
     * 计数器，用于实现轮询
     */
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {

        // 如果不存在服务地址
        if (CollUtil.isEmpty(serviceMetaInfoList)) {
            return null;
        }

        // 如果只有一个服务地址，不需要轮询，直接返回
        if (serviceMetaInfoList.size() == 1) {
            return serviceMetaInfoList.get(0);
        }

        // 获取轮询索引
        int index = counter.getAndIncrement() % serviceMetaInfoList.size();
        return serviceMetaInfoList.get(index);
    }
}
