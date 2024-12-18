package com.himoyi.server.tcp;

import cn.hutool.core.util.IdUtil;
import com.himoyi.RpcApplication;
import com.himoyi.exception.Serializer.RpcSerializerFailException;
import com.himoyi.model.RpcRequest;
import com.himoyi.model.RpcResponse;
import com.himoyi.model.ServiceMetaInfo;
import com.himoyi.protocol.*;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.SocketException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * TCP客户端实现类
 */

@Slf4j
public class VertxTCPClient {

    /**
     * 执行请求动作
     *
     * @param serviceMetaInfo 服务元信息
     * @param rpcRequest      请求
     * @return 执行结果
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static Object doRequest(ServiceMetaInfo serviceMetaInfo, RpcRequest rpcRequest) throws ExecutionException, InterruptedException {
        NetClient netClient = Vertx.vertx().createNetClient();

        // 用于异步获取调用结果
        CompletableFuture<ProtocolMessage<RpcResponse>> responseFuture = new CompletableFuture<>();

        try {
            // 向服务器发起一个链接
            netClient.connect(serviceMetaInfo.getServicePort(), serviceMetaInfo.getServiceHost(), result -> {
                if (result.succeeded()) {
                    System.out.println("Connected to " + serviceMetaInfo.getServiceHost() + ":" + serviceMetaInfo.getServicePort());

                    // 获取socket信息
                    NetSocket netSocket = result.result();

                    // 发起请求
                    sendRequest(rpcRequest, netSocket);

                    // 获取响应
                    getResponse(netSocket, responseFuture);


                } else {
                    System.out.println("Failed to connect to " + serviceMetaInfo.getServiceHost() + ":" + serviceMetaInfo.getServicePort());
                }
            });

            // 通过get阻塞等待，直到结果返回
            return responseFuture.get().getData();
        } finally {
            netClient.close();
        }

    }


/************************************************ 私有方法 ************************************************/

    /**
     * 构造请求信息
     *
     * @param request 请求数据
     * @return 协议消息
     */
    private static ProtocolMessage<RpcRequest> createProtocolMessage(RpcRequest request) {
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
    private static void sendRequest(RpcRequest rpcRequest, NetSocket netSocket) {
        ProtocolMessage<RpcRequest> protocolMessage = createProtocolMessage(rpcRequest);

        try {
            Buffer encode_ProtocolMessage = ProtocolMessageEncoder.encode(protocolMessage);
            netSocket.write(encode_ProtocolMessage);
        } catch (IOException e) {
            throw new RpcSerializerFailException("协议消息编码失败！");
        }
    }

    /**
     * 获取响应
     *
     * @param netSocket      socket链接
     * @param responseFuture 用于异步接收结果
     */
    private static void getResponse(NetSocket netSocket, CompletableFuture<ProtocolMessage<RpcResponse>> responseFuture) {

        // 对返回结果进行处理
        netSocket.handler(buffer -> {
            try {
                ProtocolMessage<RpcResponse> rpcResponseProtocolMessage = (ProtocolMessage<RpcResponse>) ProtocolMessageDecoder.decode(buffer);
                // complete方法是CompletableFuture提供的一个方法，用于将异步操作的结果设置为已完成状态，并将结果值传递给CompletableFuture
                // 当complete方法被调用时，任何等待这个CompletableFuture完成的线程（通过get()或其他方法）将被通知，异步操作已经完成，并且可以获取结果
                responseFuture.complete(rpcResponseProtocolMessage);
            } catch (IOException e) {
                log.error("协议消息解码错误！");
                throw new RpcSerializerFailException("协议消息解码错误！");
            }
        });
    }
}
