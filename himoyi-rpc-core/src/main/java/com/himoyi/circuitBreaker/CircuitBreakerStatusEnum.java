package com.himoyi.circuitBreaker;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 熔断器状态枚举类
 */

@Getter
@AllArgsConstructor
public enum CircuitBreakerStatusEnum {

    /**
     * 熔断器开启
     */
    OPEN("open"),

    /**
     * 熔断器半开
     */
    HALF_OPEN("half-open"),

    /**
     * 熔断器关闭
     */
    CLOSED("closed");

    private final String status;
}
