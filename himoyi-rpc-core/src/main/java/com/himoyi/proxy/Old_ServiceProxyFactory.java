package com.himoyi.proxy;

import java.lang.reflect.Proxy;

/**
 * 服务代理工厂（用于创建代理对象）
 */
public class Old_ServiceProxyFactory {

    /**
     * 根据服务类获取代理对象
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new ServiceProxy());
    }
}
