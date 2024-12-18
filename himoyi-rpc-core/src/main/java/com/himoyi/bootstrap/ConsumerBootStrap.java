package com.himoyi.bootstrap;

import com.himoyi.RpcApplication;
import com.himoyi.interceptor.InterceptorChain;
import com.himoyi.interceptor.consumer.ConsumerHandlerInterceptor;
import com.himoyi.utils.SPILoader;

import java.util.Map;

/**
 * 消费者服务启动类
 */
public class ConsumerBootStrap {

    public static void init() {
        // RPC 框架初始化，主要是配置和注册中心
        RpcApplication.init();

        // 构造拦截器链
        createInterceptorChain();
    }


    /**
     * 构造拦截器链
     */
    private static void createInterceptorChain() {
        Map<String, Class<?>> loadResult = SPILoader.load(ConsumerHandlerInterceptor.class);

        InterceptorChain interceptorChain = new InterceptorChain();
        loadResult.forEach((k, v) -> {
            try {
                ConsumerHandlerInterceptor consumerHandlerInterceptor = (ConsumerHandlerInterceptor) v.getDeclaredConstructor().newInstance();
                interceptorChain.addHandlerInterceptor(consumerHandlerInterceptor);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        RpcApplication.setConsumerInterceptorChain(interceptorChain);

    }
}
