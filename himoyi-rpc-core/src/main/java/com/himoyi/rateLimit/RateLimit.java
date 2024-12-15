package com.himoyi.rateLimit;

/**
 * 限流策略通用接口
 */
public interface RateLimit {

    /**
     * 获取访问许可
     * @return 获取结果
     */
    boolean getToken();
}
