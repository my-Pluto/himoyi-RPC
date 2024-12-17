package com.himoyi.himoyirpcspringbootstarter.bootstrap;

import cn.hutool.core.util.ObjectUtil;
import com.himoyi.himoyirpcspringbootstarter.annotation.RPCReference;
import com.himoyi.proxy.MockServiceProxy;
import com.himoyi.proxy.ServiceProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

public class RPCConsumerBootStrap implements BeanPostProcessor {


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        Class<?> aClass = bean.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();

        for (Field field : declaredFields) {
            RPCReference rpcReference = field.getAnnotation(RPCReference.class);
            if (ObjectUtil.isNotNull(rpcReference)) {
                Class<?> interfaceClass = rpcReference.interfaceClass();

                // 如果字段有@RPCReference注解，首先获取注解中的interfaceClass属性
                // 如果interfaceClass是void.class，则使用字段的类型作为接口类
                if (interfaceClass == void.class) {
                    interfaceClass = field.getType();
                }

                // field.setAccessible(true); 使字段可访问，因为有些字段可能是私有的
                field.setAccessible(true);
                Object proxy;

                // 生成代理对象,检查是否开启了mock
                if (rpcReference.mock()) {
                    proxy = ServiceProxyFactory.getMock(interfaceClass);
                } else {
                    proxy = ServiceProxyFactory.getProxy(interfaceClass);
                }

                // todo 各种重试、容错策略等

                try {
                    // 通过反射将代理对象设置到Bean的字段上
                    field.set(bean, proxy);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("为字段注入代理对象失败！", e);
                }

                // 最后将字段的可访问性恢复
                field.setAccessible(false);

            }
        }


        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
