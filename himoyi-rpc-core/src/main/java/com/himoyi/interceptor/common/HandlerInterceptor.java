package com.himoyi.interceptor.common;

import com.himoyi.model.RpcRequest;
import com.himoyi.model.RpcResponse;

/**
 * 拦截器处理器接口
 */
public interface HandlerInterceptor {

    /**
     * preHandle定义
     * 在处理请求前执行
     * 如果返回true，则继续执行
     * 如果返回false，则停止执行
     *
     * @param handler
     * @return
     */
    boolean preHandle(RpcRequest request, RpcResponse response, Object handler);

    /**
     * postHandle定义
     * 在请求处理后执行
     *
     * @param handler
     */
    void postHandle(RpcRequest request, RpcResponse response, Object handler);
}
