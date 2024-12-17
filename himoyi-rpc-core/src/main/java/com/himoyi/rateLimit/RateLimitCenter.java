package com.himoyi.rateLimit;

import com.himoyi.RpcApplication;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 限流注册中心，用于保存所有添加了限流的接口的限流器
 */
public class RateLimitCenter {

    /**
     * 用于保存所有需要限流的接口的限流器
     */
    private static Map<String, RateLimit> rateLimiters = new ConcurrentHashMap<String, RateLimit>();

    /**
     * 获取接口的限流器
     *
     * @param name
     * @return
     */
    public static RateLimit getRateLimiter(String name) {

        return getRateLimiter(name, RpcApplication.getRpcConfig().getRATE_LIMIT_STRATEGY());
    }

    /**
     * 获取接口的限流器
     *
     * @param name
     * @return
     */
    public static RateLimit getRateLimiter(String name, String rateLimitStrategy) {


        if (!rateLimiters.containsKey(name)) {
            synchronized (RateLimitCenter.class) {
                if (!rateLimiters.containsKey(name)) {
                    // todo 可以实现不同接口的限流速度不同，基于配置文件实现
                    // 如果不存在，则创建一个限流器，然后返回
                    RateLimit rateLimit = RateLimitFactory.getRateLimit(rateLimitStrategy);
                    rateLimiters.put(name, rateLimit);
                }
            }
        }

        return rateLimiters.get(name);
    }

    /**
     * 移除接口的限流器
     *
     * @param name
     */
    public static void removeRateLimiter(String name) {
        rateLimiters.remove(name);
    }
}
