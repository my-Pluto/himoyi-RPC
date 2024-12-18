package com.himoyi.exception.spi;

import com.himoyi.exception.RpcException;

public class RpcSPIFailClassException extends RpcException {

    public RpcSPIFailClassException(String msg) {
        super(msg);
    }
}
