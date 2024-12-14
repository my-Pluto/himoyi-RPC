package com.himoyi.fault.retry;

import com.himoyi.utils.SPILoader;

/**
 * 重试策略工厂
 */
public class RetryStrategyFactory {

    // 使用SPI机制加载重试策略
    static {
        SPILoader.load(RetryStrategy.class);
    }

    /**
     * 获取重试策略实例
     *
     * @param key 重试策略名称
     * @return
     */
    public static RetryStrategy getRetryStrategy(String key) {
        return SPILoader.getInstance(RetryStrategy.class, key);
    }
}
