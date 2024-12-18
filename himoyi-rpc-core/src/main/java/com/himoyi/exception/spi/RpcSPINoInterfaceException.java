package com.himoyi.exception.spi;

import com.himoyi.exception.RpcException;

public class RpcSPINoInterfaceException extends RpcException {

    public RpcSPINoInterfaceException(String msg) {
        super(msg);
    }
}
