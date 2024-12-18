package com.himoyi.exception.Serializer;

import com.himoyi.exception.RpcException;

public class RpcSerializerFailException extends RpcException {

    public RpcSerializerFailException(String message) {
        super(message);
    }
}
