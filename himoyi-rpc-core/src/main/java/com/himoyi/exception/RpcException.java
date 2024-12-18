package com.himoyi.exception;

/**
 * RPC框架错误基类
 */
public class RpcException extends RuntimeException {

    private String msg = "";

    public RpcException(String msg) {
        this.msg = msg;
    }

    public RpcException() {}
}
