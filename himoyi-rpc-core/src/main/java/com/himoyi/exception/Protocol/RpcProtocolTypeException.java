package com.himoyi.exception.Protocol;

import com.himoyi.exception.RpcException;

/**
 * 控制消息异常类
 */
public class RpcProtocolTypeException extends RpcException {

    public RpcProtocolTypeException(String message) {
        super(message);
    }
}
