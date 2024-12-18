package com.himoyi.exception.Serializer;

import com.himoyi.exception.RpcException;

/**
 * 序列化器异常类
 */
public class RpcNotSerializerException extends RpcException {

    public RpcNotSerializerException(String message) {
        super(message);
    }
}
