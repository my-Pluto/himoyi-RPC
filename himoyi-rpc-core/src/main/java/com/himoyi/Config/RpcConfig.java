package com.himoyi.Config;

import com.himoyi.fault.retry.RetryStrategyKeys;
import com.himoyi.loadbalancer.LoadBalancerKeys;
import com.himoyi.serializer.SerializerKeys;
import lombok.Data;

/**
 * 配置类
 */
@Data
public class RpcConfig {

    /**
     * 项目名称
     */
    private String name = "himoyi_RPC";

    /**
     * 项目版本
     */
    private String version = "V1.0";

    /**
     * 服务器地址
     */
    private String serverHost = "127.0.0.1";

    /**
     * 服务器端口号
     */
    private int serverPort = 10100;

    /**
     * 是否开启模拟调用
     */
    private boolean mock = false;

    /**
     * 定义序列化器
     */
    private String serializer = SerializerKeys.KRYO;

    /**
     * 注册中心配置
     */
    private RegistryConfig registryConfig = new RegistryConfig();

    /**
     * 负载均衡器类型
     */
    private String loadBalancer = LoadBalancerKeys.CONSISTENT_HASH;

    /**
     * 重试策略
     */
    private String retryStrategy = RetryStrategyKeys.FIXED_RETRY;

    /**
     * 重试次数
     */
    private int retryAttemptNumber = 5;
}
