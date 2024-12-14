package com.himoyi.fault.retry;

/**
 * 系统中定义的重试策略key
 */
public interface RetryStrategyKeys {

    /**
     * 不重试
     */
    String NO_RETRY = "noRetry";

    /**
     * 固定时间间隔的重试
     */
    String FIXED_RETRY = "fixedRetry";
}
