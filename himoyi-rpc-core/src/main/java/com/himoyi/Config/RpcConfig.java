package com.himoyi.Config;

import com.himoyi.fault.retry.RetryStrategyKeys;
import com.himoyi.fault.tolerant.TolerantStrategyKeys;
import com.himoyi.loadbalancer.LoadBalancerKeys;
import com.himoyi.rateLimit.RateLimitKeys;
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

    /**
     * 容错策略
     */
    private String failTolerantStrategy = TolerantStrategyKeys.FAIL_FAST;

    /**
     * 限流策略
     */
    private String RATE_LIMIT_STRATEGY = RateLimitKeys.NO_RATE_LIMIT;

    /**
     * Guava限流策略，每秒生成令牌的数量
     */
    private int GUAVA_TOKEN_CREATE_ONE_SECOND = 1;

    /**
     * 预热时间
     */
    private int GUAVA_PRE_HOT_TIME = 3;
}
