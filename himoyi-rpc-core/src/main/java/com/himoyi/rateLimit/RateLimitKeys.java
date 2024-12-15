package com.himoyi.rateLimit;

/**
 * 系统实现的限流策略
 */
public interface RateLimitKeys {

    /**
     * 手动实现的限流策略
     */
    String TOKEN_BUCKET_RATE_LIMIT = "token_bucket_rate_limit";

    /**
     * 使用Guava实现的限流策略
     */
    String GUAVA_TOKEN_BUCKET_RATE_LIMIT = "guava_token_bucket_rate_limit";

    String NO_RATE_LIMIT = "no_rate_limit";
}
