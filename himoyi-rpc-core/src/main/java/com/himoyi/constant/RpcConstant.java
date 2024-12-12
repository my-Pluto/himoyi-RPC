package com.himoyi.constant;

/**
 * RPC框架常量类
 */
public interface RpcConstant {

    /**
     * 默认前缀
     */
    String DEFAULT_CONFIG_PREFIX = "rpc";

    /**
     * 系统SPI地址
     */
    String RPC_SYSTEM_SPI_DIR = "META-INF/RPC/system/";

    /**
     * 用户自定义SPI地址
     */
    String RPC_CUSTOM_SPI_DIR = "META-INF/RPC/custom/";

    /**
     * 默认服务版本
     */
    String DEFAULT_SERVICE_VERSION = "1.0";

    /**
     * 默认服务注册中心根节点
     */
    String ETCD_ROOT_PATH = "/RPC/";
}
