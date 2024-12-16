package com.himoyi.circuitBreaker;

import com.himoyi.RpcApplication;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 熔断器注册中心
 */
public class CircuitBreakerCenter {

    private static Map<String, CircuitBreaker> circuitBreakerMap = new ConcurrentHashMap<>();

    /**
     * 获取一个服务的熔断器
     * @param serverName
     * @return
     */
    public static CircuitBreaker getCircuitBreaker(String serverName) {
        if (!circuitBreakerMap.containsKey(serverName)) {
            synchronized (CircuitBreakerCenter.class) {
                if (!circuitBreakerMap.containsKey(serverName)) {
                    CircuitBreaker circuitBreaker = new CircuitBreaker(RpcApplication.getRpcConfig().getFailureThreshold(), RpcApplication.getRpcConfig().getHalfOpenSuccessRate(), RpcApplication.getRpcConfig().getResetTimePeriod());
                    circuitBreakerMap.put(serverName, circuitBreaker);
                }
            }
        }

        return circuitBreakerMap.get(serverName);
    }


}
