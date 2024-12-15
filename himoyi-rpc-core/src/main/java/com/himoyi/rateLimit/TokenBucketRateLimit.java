package com.himoyi.rateLimit;

/**
 * 令牌桶限流策略，手动实现
 */
public class TokenBucketRateLimit implements RateLimit {

    /**
     * 令牌生成的速率
     */
    private static int RATE;

    /**
     * 桶的容量
     */
    private static int CAPACITY;

    /**
     * 当前桶容量
     */
    private volatile int curCapacity;

    /**
     * 上次请求的时间戳
     */
    private volatile long timeStamp = System.currentTimeMillis();

    /**
     * 创建一个令牌桶
     *
     * @param capacity
     * @param rate
     */
    public TokenBucketRateLimit(int capacity, int rate) {
        RATE = rate;
        CAPACITY = capacity;
        curCapacity = capacity;
    }


    @Override
    public boolean getToken() {

        // 如果桶里有令牌，则直接消费一个，返回true
        if (curCapacity > 0) {
            curCapacity--;
            return true;
        }

        // 出现了令牌不够的情况，开始生成
        long currentTime = System.currentTimeMillis();

        // 判断自从上次以来，是否已经过去了足够的时间，如果达到时间，则应该生成令牌
        if (currentTime - timeStamp >= RATE) {

            // 生成足够的令牌
            if ((currentTime - timeStamp)/RATE >= 2) {
                curCapacity += (int) ((currentTime - timeStamp)/RATE)-1;
            }


            // 如果令牌桶超过了最大值，则控制容量
            if (curCapacity > CAPACITY) {
                curCapacity = CAPACITY;
            }

            // 更新时间戳
            timeStamp = currentTime;
            return true;
        }

        // 不能获取新的令牌
        return false;
    }
}
