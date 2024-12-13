package com.himoyi.server;

import cn.hutool.core.util.ObjectUtil;
import com.himoyi.RpcApplication;
import com.himoyi.model.RpcRequest;
import com.himoyi.model.RpcResponse;
import com.himoyi.registry.LocalRegistry;
import com.himoyi.serializer.JdkSerializer;
import com.himoyi.serializer.Serializer;
import com.himoyi.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * HTTP请求处理
 * <p>
 * 业务流程如下：
 * <p>
 * 反序列化请求为对象，并从请求对象中获取参数。
 * 根据服务名称从本地注册器中获取到对应的服务实现类。
 * 通过反射机制调用方法，得到返回结果。
 * 对返回结果进行封装和序列化，并写入到响应中。
 */
public class HttpServerHandler implements Handler<HttpServerRequest> {

    @Override
    public void handle(HttpServerRequest request) {
        // 初始化序列化器
//        System.out.println(RpcApplication.getRpcConfig().getSerializer());
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());
//        final Serializer serializer = new JdkSerializer();
        // 打印日志
        System.out.println("Received request: " + request.method() + " " + request.uri());

        // 异步处理请求
        // 不同的 web 服务器对应的请求处理器实现方式也不同，比如 Vert.x 中是通过实现 Handler<HttpServerRequest> 接口来自定义请求处理器的。并且可以通过 request.bodyHandler 异步处理请求
        request.bodyHandler(body -> {
            // 第一步
            // 获取请求中的数据
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            // 对数据进行反序列化，得到请求对象
            try {
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            RpcResponse rpcResponse = new RpcResponse();

            // 如果请求信息不为空
            if (ObjectUtil.isNotNull(rpcRequest)) {
                try {
                    // 第二步
                    // 通过本地注册器，获取对应的服务类
                    Class<?> service = LocalRegistry.get(rpcRequest.getServiceName());

                    // 第三步
                    // 通过反射机制执行对应的方法
                    Method method = service.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                    Object result = method.invoke(service.getDeclaredConstructor().newInstance(), rpcRequest.getParameters());

                    // 构造对应的返回结果
                    rpcResponse.setData(result);
                    rpcResponse.setDataType(method.getReturnType());
                    rpcResponse.setMessage("200");
                } catch (Exception e) {
                    e.printStackTrace();
                    rpcResponse.setMessage(e.getMessage());
                    rpcResponse.setException(e);
                }
            } else {
                // 请求为空
                rpcResponse.setMessage("rpcRequest is null");
            }

            // 第四步
            // 返回响应结果
            doResponse(request, rpcResponse, serializer);

        });
    }

    /**
     * 响应请求
     *
     * @param request     需要响应的请求
     * @param rpcResponse 响应信息
     * @param serializer  序列化器
     */
    private void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        // 构造响应
        HttpServerResponse httpServerResponse = request.response()
                .putHeader("content-type", "application/json; charset=utf-8");

        try {
            // 序列化响应结果
            byte[] rpcResponse_serializer = serializer.serialize(rpcResponse);

            // 写入结果并返回
            httpServerResponse.end(Buffer.buffer(rpcResponse_serializer));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
