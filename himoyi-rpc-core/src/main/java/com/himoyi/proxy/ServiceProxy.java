package com.himoyi.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.himoyi.Config.RpcConfig;
import com.himoyi.RpcApplication;
import com.himoyi.circuitBreaker.CircuitBreaker;
import com.himoyi.circuitBreaker.CircuitBreakerCenter;
import com.himoyi.constant.RpcConstant;
import com.himoyi.fault.retry.RetryStrategy;
import com.himoyi.fault.retry.RetryStrategyFactory;
import com.himoyi.fault.tolerant.TolerantStrategy;
import com.himoyi.fault.tolerant.TolerantStrategyFactory;
import com.himoyi.loadbalancer.LoadBalancerFactory;
import com.himoyi.model.RpcRequest;
import com.himoyi.model.RpcResponse;
import com.himoyi.model.ServiceMetaInfo;
import com.himoyi.protocol.*;
import com.himoyi.registry.Registry;
import com.himoyi.registry.RegistryFactory;
import com.himoyi.serializer.JdkSerializer;
import com.himoyi.serializer.Serializer;
import com.himoyi.serializer.SerializerFactory;
import com.himoyi.server.tcp.VertxTCPClient;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 服务代理类（JDK动态代理）
 */
@Slf4j
public class ServiceProxy implements InvocationHandler {


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        // 构造请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .parameters(args)
                .build();

        if (RpcApplication.getRpcConfig().isOpenCircuitBreaker()) {
            if (!CircuitBreakerCenter.getCircuitBreaker(rpcRequest.getServiceName() + ":" + rpcRequest.getMethodName()).allowRequest()) {
                throw new RuntimeException("服务被熔断！");
            }
        }

        try {
            // 从注册中心获取服务信息
            ServiceMetaInfo serviceMetaInfo = getServiceMetaInfo(method.getDeclaringClass().getName());

            RetryStrategy retryStrategy = RetryStrategyFactory.getRetryStrategy(RpcApplication.getRpcConfig().getRetryStrategy());

            /*
             * Callable是一个函数式接口.
             * ()：表示这个lambda表达式不接受任何参数，匹配Callable接口的call()方法签名。
             * ->：lambda表达式的箭头操作符，左边是参数列表（这里为空），右边是lambda表达式的实现。
             * (RpcResponse) VertxTCPClient.doRequest(serviceMetaInfo, rpcRequest)：这是lambda表达式的实现部分，它调用了VertxTCPClient.doRequest方法，并将结果转换为RpcResponse类型
             * Java编译器可以根据上下文推断出lambda表达式应该实现的接口类型。在这里，retryStrategy.doRetry方法期望一个Callable<RpcResponse>，因此编译器知道lambda表达式应该实现Callable接口的call()方法
             *
             *
             * 展开写的话：
             * RpcResponse response = retryStrategy.doRetry(new Callable<RpcResponse>() {
             *     @Override
             *     public RpcResponse call() throws Exception {
             *         // 发送请求，返回结果
             *         return (RpcResponse) VertxTCPClient.doRequest(serviceMetaInfo, rpcRequest);
             *     }
             * });
             */
            result = retryStrategy.doRetry(() -> VertxTCPClient.doRequest(serviceMetaInfo, rpcRequest));


        } catch (Exception e) {
//            log.error("调用失败！");
//            throw new RuntimeException("调用失败！", e);

            // 如果开启了熔断机制，记录失败
            if (RpcApplication.getRpcConfig().isOpenCircuitBreaker()) {
                CircuitBreaker circuitBreaker = CircuitBreakerCenter.getCircuitBreaker(rpcRequest.getServiceName() + ":" + rpcRequest.getMethodName());
                circuitBreaker.recordFailureCount();
            }

            // 进行容错处理
            TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getTolerantStrategy(RpcApplication.getRpcConfig().getFailTolerantStrategy());
            tolerantStrategy.doTolerant(null, e);
        }

        // 如果开启了熔断机制，记录成功
        if (RpcApplication.getRpcConfig().isOpenCircuitBreaker()) {
            CircuitBreaker circuitBreaker = CircuitBreakerCenter.getCircuitBreaker(rpcRequest.getServiceName() + ":" + rpcRequest.getMethodName());
            circuitBreaker.recordFailureCount();
        }
        return result;
    }

    /**
     * 从服务中心根据服务名获取一个服务地址
     *
     * @param serviceName
     * @return
     */
    private ServiceMetaInfo getServiceMetaInfo(String serviceName) {
        Registry registry = RegistryFactory.getRegistry(RpcApplication.getRpcConfig().getRegistryConfig().getRegistry());

        List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(String.format("%s:%s", serviceName, RpcConstant.DEFAULT_SERVICE_VERSION));

        if (CollUtil.isEmpty(serviceMetaInfos)) {
            log.error("暂无服务地址");
            throw new RuntimeException("暂无服务地址");
        }

        // 构造参数map，使用负载均衡器选择一个服务地址
        // 如果调用同一个方法，请求的地址一定是同一个
        Map<String, Object> map = new HashMap<>();
        map.put("serviceName", serviceName);
        // 为了避免所有请求落到同一个服务器，添加一个随机数
        map.put("UUID", IdUtil.simpleUUID());
        return LoadBalancerFactory.getLoadBalancer(RpcApplication.getRpcConfig().getLoadBalancer()).select(map, serviceMetaInfos);
    }


}
