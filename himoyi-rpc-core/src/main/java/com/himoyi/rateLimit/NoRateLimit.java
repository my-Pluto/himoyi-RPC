package com.himoyi.rateLimit;

/**
 * 不进行限流
 */
public class NoRateLimit implements RateLimit {

    /**
     * 占位的构造器，以能够统一使用反射创建对应的实例
     *
     * @param a
     * @param b
     */
    public NoRateLimit(int a, int b) {
    }

    @Override
    public boolean getToken() {
        return true;
    }
}
