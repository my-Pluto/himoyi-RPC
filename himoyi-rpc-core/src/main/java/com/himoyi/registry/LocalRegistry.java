package com.himoyi.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 本地服务注册器
 * <p>
 * 主要用于根据服务名称获取对应的实现类
 */
public class LocalRegistry {

    /**
     * 注册信息存储
     */
    private static final Map<String, Class<?>> map = new ConcurrentHashMap<String, Class<?>>();

    /**
     * 注册服务类
     *
     * @param className 服务名
     * @param clazz 实现类
     */
    public static void register(String className, Class<?> clazz) {
        map.put(className, clazz);
    }

    /**
     * 获取服务类
     *
     * @param className 服务名
     * @return 实现类
     */
    public static Class<?> get(String className) {
        return map.get(className);
    }

    /**
     * 删除服务类
     *
     * @param className 服务名
     */
    public static void remove(String className) {
        map.remove(className);
    }
}
