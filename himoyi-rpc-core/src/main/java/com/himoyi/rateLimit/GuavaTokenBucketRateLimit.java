package com.himoyi.rateLimit;

import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.TimeUnit;

/**
 * 使用Guava实现令牌桶策略
 */
public class GuavaTokenBucketRateLimit implements RateLimit {

    private RateLimiter rateLimiter;

    public GuavaTokenBucketRateLimit(int token_create_number_one_second, int pre_hot_time) {
        // 1s 放 5 个令牌到桶里也就是 0.2s 放 1个令牌到桶里
        // 预热时间为pre_hot_time s,也就说刚开始的 pre_hot_time s 内发牌速率会逐渐提升到预定的生成速率
        rateLimiter = RateLimiter.create(token_create_number_one_second, pre_hot_time, TimeUnit.SECONDS);
    }

    @Override
    public boolean getToken() {
        return rateLimiter.tryAcquire();
    }
}
