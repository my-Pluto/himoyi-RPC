package com.himoyi.interceptor.provider;

import com.himoyi.interceptor.common.HandlerInterceptor;
import com.himoyi.model.RpcRequest;
import com.himoyi.model.RpcResponse;
import lombok.NoArgsConstructor;

/**
 * 服务提供者过滤器链
 */
@NoArgsConstructor
public class ProviderHandlerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(RpcRequest request, RpcResponse response, Object handler) {
        return true;
    }

    @Override
    public void postHandle(RpcRequest request, RpcResponse response, Object handler) {
    }
}
