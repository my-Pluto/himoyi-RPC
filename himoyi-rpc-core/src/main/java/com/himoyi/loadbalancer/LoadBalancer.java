package com.himoyi.loadbalancer;

import com.himoyi.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

/**
 * 负载均衡器，消费端使用
 */
public interface LoadBalancer {

    ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList);
}
