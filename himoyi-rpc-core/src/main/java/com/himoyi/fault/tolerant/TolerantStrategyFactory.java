package com.himoyi.fault.tolerant;

import com.himoyi.utils.SPILoader;

/**
 * 容错机制工厂
 */
public class TolerantStrategyFactory {
    static {
        SPILoader.load(TolerantStrategy.class);
    }

    /**
     * 获取容错机制
     * @param strategyName
     * @return
     */
    public static TolerantStrategy getTolerantStrategy(String strategyName) {
        return SPILoader.getInstance(TolerantStrategy.class, strategyName);
    }
}
