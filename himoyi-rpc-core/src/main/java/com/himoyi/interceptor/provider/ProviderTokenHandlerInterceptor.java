package com.himoyi.interceptor.provider;

import com.himoyi.RpcApplication;
import com.himoyi.model.RpcRequest;
import com.himoyi.model.RpcResponse;
import com.himoyi.registry.Registry;
import com.himoyi.registry.RegistryFactory;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 进行token验证的拦截器
 * <p>
 * 需要注意，该token验证只是为了防止消费端绕过注册中心，直接请求服务提供者
 * 如果需要更严格的身份认证，需要其他方式
 */

@NoArgsConstructor
@Slf4j
public class ProviderTokenHandlerInterceptor extends ProviderHandlerInterceptor {
    @Override
    public boolean preHandle(RpcRequest request, RpcResponse response, Object handler) {
        if (RpcApplication.getRpcConfig().isTokenAuth()) {
            String serviceName = request.getServiceName();
            Registry registry = RegistryFactory.getRegistry(RpcApplication.getRpcConfig().getRegistryConfig().getRegistry());
            String token = registry.getToken(serviceName);
            if (token.equals(request.getToken())) {
                log.info("token 验证通过！");
                return true;
            } else {
                log.error("token 验证失败！");
                response.setMessage("token验证不通过！");
                return false;
            }
        }

        return true;
    }
}
