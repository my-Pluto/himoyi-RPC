package com.himoyi.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.himoyi.RpcApplication;
import com.himoyi.constant.RpcConstant;
import com.himoyi.model.RpcRequest;
import com.himoyi.model.RpcResponse;
import com.himoyi.model.ServiceMetaInfo;
import com.himoyi.protocol.*;
import com.himoyi.registry.Registry;
import com.himoyi.registry.RegistryFactory;
import com.himoyi.serializer.JdkSerializer;
import com.himoyi.serializer.Serializer;
import com.himoyi.serializer.SerializerFactory;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * 服务代理类（JDK动态代理）
 */
@Slf4j
public class ServiceProxy implements InvocationHandler {


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        // 构造序列化器
//        System.out.println(RpcApplication.getRpcConfig().getSerializer());
//        Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());;
//        final Serializer serializer = new JdkSerializer();
        // 构造请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .parameters(args)
                .build();

        try {
//            // 对请求进行序列化
//            byte[] serialize = serializer.serialize(rpcRequest);

            // 从注册中心获取服务信息
            ServiceMetaInfo serviceMetaInfo = getServiceMetaInfo(method.getDeclaringClass().getName());

            NetClient netClient = Vertx.vertx().createNetClient();

            // 用于异步获取调用结果
            CompletableFuture<RpcResponse> responseFuture = new CompletableFuture<>();

            // 向服务器发起一个链接
            netClient.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(), result -> {
                if (result.succeeded()) {
                    System.out.println("Connected to " + serviceMetaInfo.getServiceHost() + ":" + serviceMetaInfo.getServicePort());

                    // 获取socket信息
                    NetSocket netSocket = result.result();

                    // 发起请求
                    request(rpcRequest, netSocket);

                    // 处理响应
                    getResponse(netSocket, responseFuture);

                } else {
                    System.out.println("Failed to connect to " + serviceMetaInfo.getServiceHost() + ":" + serviceMetaInfo.getServicePort());
                }
            });

            netClient.close();
            // 通过get阻塞等待，直到结果返回
            return responseFuture.get();

//            // 发起请求
//            try (HttpResponse httpResponse = HttpRequest.post(serviceMetaInfo.getServiceAddress())
//                         .body(serialize)
//                         .execute()) {
//
//                // 对请求结果反序列化
//                byte[] bytes = httpResponse.bodyBytes();
//                RpcResponse deserialize = serializer.deserialize(bytes, RpcResponse.class);
//
//                // 返回结果
//                return deserialize.getData();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
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

        // todo 暂时返回第一个，之后需要实现轮询
        return serviceMetaInfos.get(0);
    }

    /**
     * 构造请求信息
     *
     * @param request 请求数据
     * @return 协议消息
     */
    private ProtocolMessage<RpcRequest> createProtocolMessage(RpcRequest request) {
        ProtocolMessage<RpcRequest> protocolMessage = new ProtocolMessage<>();
        ProtocolMessage.Header header = new ProtocolMessage.Header();

        header.setMagic(ProtocolConstant.PROTOCOL_MAGIC);
        header.setVersion(ProtocolConstant.PROTOCOL_VERSION);
        header.setSerializer((byte) Objects.requireNonNull(ProtocolMessageSerializerEnum.getEnumByName(RpcApplication.getRpcConfig().getSerializer())).getKey());
        header.setType((byte) ProtocolMessageTypeEnum.REQUEST.getKey());
        header.setRequestID(IdUtil.getSnowflakeNextId());

        protocolMessage.setHeader(header);
        protocolMessage.setData(request);

        return protocolMessage;
    }

    /**
     * 发送请求
     *
     * @param rpcRequest 需要执行的方法
     * @param netSocket  socket链接
     */
    private void request(RpcRequest rpcRequest, NetSocket netSocket) {
        ProtocolMessage<RpcRequest> protocolMessage = createProtocolMessage(rpcRequest);

        try {
            Buffer encode_ProtocolMessage = ProtocolMessageEncoder.encode(protocolMessage);
            netSocket.write(encode_ProtocolMessage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 处理响应
     *
     * @param netSocket      socket链接
     * @param responseFuture 用于异步接收结果
     */
    private void getResponse(NetSocket netSocket, CompletableFuture<RpcResponse> responseFuture) {

        // 对返回结果进行处理
        netSocket.handler(buffer -> {
            try {
                ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                // complete方法是CompletableFuture提供的一个方法，用于将异步操作的结果设置为已完成状态，并将结果值传递给CompletableFuture
                // 当complete方法被调用时，任何等待这个CompletableFuture完成的线程（通过get()或其他方法）将被通知，异步操作已经完成，并且可以获取结果
                responseFuture.complete(rpcResponseProtocolMessage.getData());
            } catch (IOException e) {
                log.error("协议消息解码错误！");
                throw new RuntimeException("协议消息解码错误！", e);
            }
        });
    }
}
