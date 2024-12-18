package com.himoyi.interceptor.consumer;

import com.himoyi.model.RpcRequest;
import com.himoyi.model.RpcResponse;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@Slf4j
public class ConsumerRequestHandlerInterceptor extends ConsumerHandlerInterceptor {
    @Override
    public boolean preHandle(RpcRequest request, RpcResponse response, Object handler) {
        log.info("正在发出请求： [{}]", request);
        return true;
    }

    @Override
    public void postHandle(RpcRequest request, RpcResponse response, Object handler) {
        log.info("收到请求的回复：[{}]", response);
    }
}
