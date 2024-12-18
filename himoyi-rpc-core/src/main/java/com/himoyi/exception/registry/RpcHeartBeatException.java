package com.himoyi.exception.registry;

import com.himoyi.exception.RpcException;

public class RpcHeartBeatException extends RpcException {
    public RpcHeartBeatException(String message) {
        super(message);
    }
}
