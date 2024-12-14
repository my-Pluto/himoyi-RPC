package com.himoyi.loadbalancer;

import lombok.Getter;

/**
 * 系统中定义的负载均衡器
 */

public interface LoadBalancerKeys {

    /**
     * 随机负载均衡
     */
    String RANDOM = "random";

    /**
     * 轮询负载均衡
     */
    String ROUND_ROBIN = "round-robin";

    /**
     * 一致性HASH负载均衡
     */
    String CONSISTENT_HASH = "consistent-hash";


}
