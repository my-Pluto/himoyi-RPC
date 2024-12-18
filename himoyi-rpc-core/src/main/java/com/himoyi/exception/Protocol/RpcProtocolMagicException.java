package com.himoyi.exception.Protocol;

import com.himoyi.exception.RpcException;

/**
 * 控制消息异常类
 */
public class RpcProtocolMagicException extends RpcException {

    public RpcProtocolMagicException(String message) {
        super(message);
    }
}
