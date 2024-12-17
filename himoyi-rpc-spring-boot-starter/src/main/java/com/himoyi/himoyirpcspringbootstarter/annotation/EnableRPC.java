package com.himoyi.himoyirpcspringbootstarter.annotation;

import com.himoyi.himoyirpcspringbootstarter.bootstrap.RPCConsumerBootStrap;
import com.himoyi.himoyirpcspringbootstarter.bootstrap.RPCInitBootStrap;
import com.himoyi.himoyirpcspringbootstarter.bootstrap.RPCProviderBootStrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
// 当一个类或接口被标注为@EnableRPC时，Spring Boot应用程序会自动导入并启用RPCInitBootStrap、RPCConsumerBootStrap和RPCProviderBootStrap这三个配置类
// 这些配置类包含了RPC框架的初始化逻辑、服务消费者和服务提供者的配置
@Import({RPCInitBootStrap.class, RPCConsumerBootStrap.class, RPCProviderBootStrap.class})
public @interface EnableRPC {

    /**
     * 是否需要启动web服务器
     * 默认需要（服务提供者）
     *
     * @return
     */
    boolean startServer() default true;
}
