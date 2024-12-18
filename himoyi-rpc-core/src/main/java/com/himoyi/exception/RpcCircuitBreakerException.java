package com.himoyi.exception;

/**
 * 服务熔断异常类
 */
public class RpcCircuitBreakerException extends RpcException {

    public RpcCircuitBreakerException(String message) {
        super(message);
    }
}
