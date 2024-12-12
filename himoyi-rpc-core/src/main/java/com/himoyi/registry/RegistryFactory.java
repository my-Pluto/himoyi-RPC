package com.himoyi.registry;

import com.himoyi.utils.SPILoader;

/**
 * 注册中心工厂，用于获取注册中心对象，懒加载
 */
public class RegistryFactory {

    // 通过SPI机制加载注册中心
    static {
        SPILoader.load(Registry.class);
    }

    // 默认注册中心
    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();

    /**
     * 获取注册中心
     * @param key
     * @return
     */
    public static Registry getRegistry(String key) {
        return SPILoader.getInstance(Registry.class, key);
    }
}
