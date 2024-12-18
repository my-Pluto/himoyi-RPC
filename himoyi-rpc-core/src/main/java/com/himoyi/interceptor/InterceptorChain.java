package com.himoyi.interceptor;

import cn.hutool.core.util.ObjectUtil;
import com.himoyi.model.RpcRequest;
import com.himoyi.model.RpcResponse;
import com.himoyi.protocol.ProtocolMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * 拦截器执行链
 */
public class InterceptorChain {

    /**
     * 拦截器列表
     */
    private final List<HandlerInterceptor> handlerInterceptorsList;

    /**
     * 用于缓存拦截器实例列表
     */
    private HandlerInterceptor[] handlerInterceptors;

    /**
     * 记录目前前置处理器执行了多少
     */
    private int currentIndex = 0;

    /**
     * 创建一个新的拦截器链
     */
    public InterceptorChain() {
        this.handlerInterceptorsList = new ArrayList<>();
        this.handlerInterceptors = null;
    }

    /**
     * 添加拦截器
     *
     * @param handlerInterceptor
     */
    public void addHandlerInterceptor(HandlerInterceptor handlerInterceptor) {
        this.handlerInterceptorsList.add(handlerInterceptor);
    }

    /**
     * 获取拦截器数组
     *
     * @return
     */
    private HandlerInterceptor[] getHandlerInterceptors() {
        if (ObjectUtil.isNull(handlerInterceptors) && ObjectUtil.isNotNull(handlerInterceptorsList)) {
            this.handlerInterceptors = handlerInterceptorsList.toArray(new HandlerInterceptor[0]);
        }

        return this.handlerInterceptors;
    }

    /**
     * 执行前置处理器
     *
     * @param request
     * @param response
     * @return
     */
    public boolean applyPreHandle(RpcRequest request, RpcResponse response) {
        HandlerInterceptor[] handlerInterceptors = getHandlerInterceptors();
        for (int i = 0; i < handlerInterceptors.length; i++) {
            HandlerInterceptor handlerInterceptor = handlerInterceptors[i];
            if (!handlerInterceptor.preHandle(request, response, handlerInterceptor)) {
                return false;
            }

            this.currentIndex = i;
        }

        return true;
    }

    /**
     * 执行后置处理器
     *
     * @param request
     * @param response
     */
    public void applyPostHandle(RpcRequest request, RpcResponse response) {
        HandlerInterceptor[] handlerInterceptors = getHandlerInterceptors();
        for (int i = this.currentIndex; i >= 0; i--) {
            HandlerInterceptor handlerInterceptor = handlerInterceptors[i];
            handlerInterceptor.postHandle(request, response, handlerInterceptor);
        }
    }
}
