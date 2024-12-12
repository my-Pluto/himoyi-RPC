package com.himoyi.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.himoyi.model.RpcRequest;
import com.himoyi.model.RpcResponse;
import com.himoyi.serializer.JdkSerializer;
import com.himoyi.serializer.Serializer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 服务代理类（JDK动态代理）
 */
public class ServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 构造序列化器
        Serializer serializer = new JdkSerializer();

        // 构造请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .parameters(args)
                .build();

        try {
            // 对请求进行序列化
            byte[] serialize = serializer.serialize(rpcRequest);

            // 发起请求
            // todo 地址被硬编码，需要使用注册中心和服务发现机制解决
            try (HttpResponse httpResponse = HttpRequest.post("http://localhost:10100")
                         .body(serialize)
                         .execute()) {

                // 对请求结果反序列化
                byte[] bytes = httpResponse.bodyBytes();
                RpcResponse deserialize = serializer.deserialize(bytes, RpcResponse.class);

                // 返回结果
                return deserialize.getData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
