package com.himoyi;

import cn.hutool.core.util.ObjectUtil;
import com.himoyi.Config.RegistryConfig;
import com.himoyi.Config.RpcConfig;
import com.himoyi.constant.RpcConstant;
import com.himoyi.registry.Registry;
import com.himoyi.registry.RegistryFactory;
import com.himoyi.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * RPC框架应用
 * 存放了全局变量
 */

@Slf4j
public class RpcApplication {
    private static volatile RpcConfig rpcConfig;

    /**
     * 初始化配置文件，使用自定义配置
     *
     * @param newRpcConfig 自定义配置文件
     */
    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("RpcApplication init, config: {}", rpcConfig.toString());

        registryInit(rpcConfig.getRegistryConfig());
    }

    /**
     * 初始化注册中心
     *
     * @param registryConfig 注册中心配置文件
     */
    private static void registryInit(RegistryConfig registryConfig) {
        Registry registry = RegistryFactory.getRegistry(registryConfig.getRegistry());
        registry.init(registryConfig);

        // 注册Shutdown Hook，JVM退出时可以自动执行该方法
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));

        log.info("RpcApplication registry init, registry: {}", registryConfig.getRegistry());
    }

    /**
     * 初始化，使用配置文件
     */
    public static void init() {
        RpcConfig newrpcConfig;
        newrpcConfig = ConfigUtils.loadConfig(RpcConstant.DEFAULT_CONFIG_PREFIX, RpcConfig.class);
        if (ObjectUtil.isNotNull(newrpcConfig)) {
            init(newrpcConfig);
        } else {
            init(new RpcConfig());
        }
    }

    /**
     * 获取配置信息，使用双重检查锁保证单例模式
     *
     * @return 配置对象
     */
    public static RpcConfig getRpcConfig() {
        if (ObjectUtil.isNull(rpcConfig)) {
            synchronized (RpcApplication.class) {
                if (ObjectUtil.isNull(rpcConfig)) {
                    init();
                }
            }
        }

        return rpcConfig;
    }

}
