package com.himoyi.loadbalancer;

import com.himoyi.utils.SPILoader;

/**
 * 负载均衡器工厂
 */
public class LoadBalancerFactory {

    // 使用SPI加载负载均衡器
    static {
        SPILoader.load(LoadBalancer.class);
    }

    /**
     * 获取负载均衡器
     * @param loadBalancerName 负载均衡器类型
     * @return 负载均衡器
     */
    public static LoadBalancer getLoadBalancer(String loadBalancerName) {
        return SPILoader.getInstance(LoadBalancer.class, loadBalancerName);
    }
}
