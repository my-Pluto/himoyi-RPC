package com.himoyi.bootstrap;

import com.himoyi.Config.RegistryConfig;
import com.himoyi.Config.RpcConfig;
import com.himoyi.RpcApplication;
import com.himoyi.constant.RpcConstant;
import com.himoyi.model.ServiceMetaInfo;
import com.himoyi.model.ServiceRegisterInfo;
import com.himoyi.registry.LocalRegistry;
import com.himoyi.registry.Registry;
import com.himoyi.registry.RegistryFactory;
import com.himoyi.server.tcp.VertxTcpServer;

import java.util.List;

/**
 * 服务提供者启动类
 */
public class ProviderBootStrap {

    /**
     * 服务初始化
     *
     * @param serviceRegisterInfos 需要注册的服务
     */
    public static void init(List<ServiceRegisterInfo<?>> serviceRegisterInfos) {

        // 初始化通用配置，加载配置文件
        RpcApplication.init();
        final RpcConfig rpcConfig = RpcApplication.getRpcConfig();

        // 循环注册所有需要注册的类
        for (ServiceRegisterInfo<?> serviceRegisterInfo : serviceRegisterInfos) {

            // 获取服务名称
            String serviceName = serviceRegisterInfo.getServiceName();

            // 注册到本地，用于保存当前服务器已经注册的服务
            LocalRegistry.register(serviceName, serviceRegisterInfo.getServiceClass());

            // 获取注册中心的配置
            RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
            // 获取注册中心
            Registry registry = RegistryFactory.getRegistry(registryConfig.getRegistry());

            // 创建服务元数据
            ServiceMetaInfo metaInfo = new ServiceMetaInfo();
            metaInfo.setServiceName(serviceName);
            metaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            metaInfo.setServiceHost(rpcConfig.getServerHost());
            metaInfo.setServicePort(rpcConfig.getServerPort());

            try {
                // 注册
                registry.register(metaInfo);
            } catch (Exception e) {
                throw new RuntimeException("service " + serviceName + " register error", e);
            }
        }

        // 启动Web服务器
        new VertxTcpServer().startServer(rpcConfig.getServerPort());
    }
}
