package com.himoyi.exception;

/**
 * 通信错误异常类
 */
public class RpcSocketException extends RuntimeException {

    public RpcSocketException(String msg) {
        super(msg);
    }
}
