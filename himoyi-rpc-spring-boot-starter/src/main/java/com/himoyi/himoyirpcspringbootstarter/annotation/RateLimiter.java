package com.himoyi.himoyirpcspringbootstarter.annotation;

import com.himoyi.rateLimit.RateLimitKeys;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限流策略注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimiter {

    String rateLimiter() default RateLimitKeys.NO_RATE_LIMIT;

    /**
     * 每秒放行数
     * @return
     */
    int permitsPerSecond() default 1;

    /**
     * 预热时间
     * @return
     */
    int preHotTime() default 0;
}
