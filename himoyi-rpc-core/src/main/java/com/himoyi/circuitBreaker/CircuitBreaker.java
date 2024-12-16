package com.himoyi.circuitBreaker;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义熔断器
 */
public class CircuitBreaker {

    /**
     * 目前熔断器的状态
     */
    private CircuitBreakerStatusEnum status = CircuitBreakerStatusEnum.CLOSED;

    /**
     * 失败计数器
     */
    private AtomicInteger failureCount = new AtomicInteger(0);
    /**
     * 成功计数器
     */
    private AtomicInteger successCount = new AtomicInteger(0);
    /**
     * 半开状态下成功计数器
     */
    private AtomicInteger halfOpenRequestCount = new AtomicInteger(0);

    /**
     * 失败阈值，当失败数量达到指定阈值，则开启熔断器
     */
    private final int failureThreshold;
    /**
     * 半开状态下，成功的比例
     * 当成功数量达到指定比例，则关闭熔断器
     */
    private final double halfOpenSuccessRate;
    /**
     * 重置时间周期
     * 在熔断器生效的情况下，如果经过了足够长的时间，则可以试探性进入半开状态
     */
    private final long resetTimePeriod;


    /**
     * 最后一次失败的时间戳
     */
    private long lastFailureTime = 0;

    /**
     * 初始化一个熔断器
     *
     * @param failureThreshold
     * @param halfOpenSuccessRate
     * @param resetTimePeriod
     */
    public CircuitBreaker(int failureThreshold, double halfOpenSuccessRate, long resetTimePeriod) {
        this.failureThreshold = failureThreshold;
        this.halfOpenSuccessRate = halfOpenSuccessRate;
        this.resetTimePeriod = resetTimePeriod;
    }


    public synchronized boolean allowRequest() {
        long currentTime = System.currentTimeMillis();

        switch (status) {
            case OPEN:
                // 检查是否经过了足够长的时间
                if (currentTime - lastFailureTime > resetTimePeriod) {
                    // 进入半开状态
                    status = CircuitBreakerStatusEnum.HALF_OPEN;
                    // 重置计数器
                    resetCount();
                    return true; // 允许请求
                }
                // 不能关闭熔断器，继续熔断
                return false;

            case HALF_OPEN:
                // 进行试探性请求
                halfOpenRequestCount.incrementAndGet();
                return true; // 允许请求
            case CLOSED:
            default:
                return true; // 服务正常，允许请求
        }
    }

    /**
     * 记录一次成功的请求
     */
    public synchronized void recordSuccessCount() {

        // 如果当前状态处于半开
        if (status == CircuitBreakerStatusEnum.HALF_OPEN) {
            halfOpenRequestCount.incrementAndGet(); // 记录一次半开成功的请求

            // 如果已经达到恢复阈值，则关闭熔断器
            if (successCount.get() >= halfOpenSuccessRate * halfOpenRequestCount.get()) {
                status = CircuitBreakerStatusEnum.CLOSED;
                resetCount(); // 重置计数器
            }
        } else {
            // 本熔断器的计算方式是当连续出现多次调用失败后熔断，只要成功一次即可重置计数器
            resetCount(); // 不是半开状态，重置计数器
        }
    }

    /**
     * 记录一次失败的请求
     */
    public synchronized void recordFailureCount() {
        // 记录失败次数与时间
        failureCount.incrementAndGet();
        lastFailureTime = System.currentTimeMillis();

        // 如果当前处于半开模式，则半开失败，进入开启状态
        if (status == CircuitBreakerStatusEnum.HALF_OPEN) {
            status = CircuitBreakerStatusEnum.OPEN;

            // 如果失败次数达到阈值，则直接开启熔断器
        } else if (failureCount.get() >= failureThreshold) {
            status = CircuitBreakerStatusEnum.OPEN;
        }
    }


    /**
     * 重置所有计数器
     */
    private void resetCount() {
        failureCount.set(0);
        successCount.set(0);
        halfOpenRequestCount.set(0);
    }
}
