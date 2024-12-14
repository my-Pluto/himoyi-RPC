package com.himoyi.loadbalancer;

import cn.hutool.core.collection.CollUtil;
import com.himoyi.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 随机负载均衡
 */
public class RandomLoadBalancer implements LoadBalancer {

    // 随机数生成器
    private final Random random = new Random();

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

        // 获取索引
        int index = random.nextInt(serviceMetaInfoList.size());

        return serviceMetaInfoList.get(index);
    }
}
