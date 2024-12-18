package com.himoyi.exception;

/**
 * 服务熔断异常类
 */
public class RpcHasRateLimitException extends RpcException {

    public RpcHasRateLimitException(String message) {
        super(message);
    }
}
