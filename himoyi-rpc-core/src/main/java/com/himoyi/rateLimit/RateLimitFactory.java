package com.himoyi.rateLimit;

import cn.hutool.core.util.ObjectUtil;
import com.himoyi.RpcApplication;
import com.himoyi.utils.SPILoader;

/**
 * 限流器工厂
 */
public class RateLimitFactory {

    static {
        SPILoader.load(RateLimit.class);
    }

    public static RateLimit getRateLimit(String key) {

        // 获取一个新的限流器实例
        return SPILoader.getNewInstance(RateLimit.class,
                key, new Class[]{int.class, int.class},
                new Object[]{RpcApplication.getRpcConfig().getGUAVA_TOKEN_CREATE_ONE_SECOND(), RpcApplication.getRpcConfig().getGUAVA_PRE_HOT_TIME()});

    }
}
