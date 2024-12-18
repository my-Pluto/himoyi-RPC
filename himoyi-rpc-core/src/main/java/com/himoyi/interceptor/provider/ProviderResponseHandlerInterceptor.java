package com.himoyi.interceptor.provider;

import com.himoyi.model.RpcRequest;
import com.himoyi.model.RpcResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@Slf4j
public class ProviderResponseHandlerInterceptor extends ProviderHandlerInterceptor {
    @Override
    public boolean preHandle(RpcRequest request, RpcResponse response, Object handler) {

        log.info("收到来自消费者的请求，请求内容为：[{}]", request);
        return true;
    }

    @Override
    public void postHandle(RpcRequest request, RpcResponse response, Object handler) {
        log.info("完成请求的处理，响应内容为：[{}]", response);
    }
}
