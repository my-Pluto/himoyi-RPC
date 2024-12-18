package com.himoyi.exception.registry;

import com.himoyi.exception.RpcException;

public class RpcOfflineException extends RpcException {
    public RpcOfflineException(String message) {
        super(message);
    }
}
