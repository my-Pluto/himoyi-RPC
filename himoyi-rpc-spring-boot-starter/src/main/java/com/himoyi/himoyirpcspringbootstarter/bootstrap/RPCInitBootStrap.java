package com.himoyi.himoyirpcspringbootstarter.bootstrap;

import com.himoyi.Config.RpcConfig;
import com.himoyi.RpcApplication;
import com.himoyi.himoyirpcspringbootstarter.annotation.EnableRPC;
import com.himoyi.server.tcp.VertxTcpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.List;

/**
 * RPC框架的全局启动类
 */
//@Slf4j
public class RPCInitBootStrap implements ImportBeanDefinitionRegistrar {
    private static final Logger log = LoggerFactory.getLogger(RPCInitBootStrap.class);

    /**
     * 通过Spring中的接口ImportBeanDefinitionRegistrar，继承其registerBeanDefinitions可以获得EnableRPC注解的属性，以查看其是否需要启动服务器
     *
     * 在spring应用启动过程，一些被@Import注解的类（这些类都实现了ImportBeanDefinitionRegistrar接口）会执行ImportBeanDefinitionRegistrar的registerBeanDefinitions方法
     * 然后生成BeanDefinition对象，并最终注册到BeanDefinitionRegistry中，为后续实例化bean做准备的
     * @param importingClassMetadata
     * @param registry
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        boolean startServer = (boolean) importingClassMetadata.getAnnotationAttributes(EnableRPC.class.getName()).get("startServer");

        RpcApplication.init();

        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        if (startServer) {
            new VertxTcpServer().startServer(rpcConfig.getServerPort());
            log.info("Web服务器，成功监听端口：{}", rpcConfig.getServerPort());
        } else {
            log.info("web服务器不需要启动");
        }


    }
}
