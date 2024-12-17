package com.himoyi.himoyirpcspringbootstarter.bootstrap;

import com.himoyi.Config.RegistryConfig;
import com.himoyi.Config.RpcConfig;
import com.himoyi.RpcApplication;
import com.himoyi.himoyirpcspringbootstarter.annotation.RPCService;
import com.himoyi.himoyirpcspringbootstarter.annotation.RateLimiter;
import com.himoyi.model.ServiceMetaInfo;
import com.himoyi.rateLimit.RateLimitCenter;
import com.himoyi.registry.LocalRegistry;
import com.himoyi.registry.Registry;
import com.himoyi.registry.RegistryFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;

public class RPCProviderBootStrap implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        Class<?> beanClass = bean.getClass();
        RPCService rpcService_annotation = beanClass.getAnnotation(RPCService.class);


        if (rpcService_annotation != null) {

            Class<?> interfaceClass = rpcService_annotation.interfaceClass();

            if (interfaceClass == void.class) {
                interfaceClass = beanClass.getInterfaces()[0];
            }

            String serviceName = interfaceClass.getName();
            String serviceVersion = rpcService_annotation.serviceVersion();

            LocalRegistry.register(serviceName, beanClass);

            final RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            Registry registry = RegistryFactory.getRegistry(registryConfig.getRegistry());

            ServiceMetaInfo metaInfo = new ServiceMetaInfo();
            metaInfo.setServiceName(serviceName);
            metaInfo.setServiceVersion(serviceVersion);
            metaInfo.setServiceHost(rpcConfig.getServerHost());
            metaInfo.setServicePort(rpcConfig.getServerPort());

            try {
                registry.register(metaInfo);
            } catch (Exception e) {
                throw new RuntimeException(serviceName + " 服务注册失败！", e);
            }

            // 如果使用了限流策略，创建对应的限流器
            Method[] declaredMethods = beanClass.getDeclaredMethods();
            for (Method method : declaredMethods) {
                RateLimiter rateLimiter = method.getAnnotation(RateLimiter.class);
                if (rateLimiter != null) {
                    // todo 之后需要处理方法重载的情况
                    RateLimitCenter.getRateLimiter(beanClass.getName() + ":" + method.getName(), rateLimiter.rateLimiter());
                }
            }

        }


        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
