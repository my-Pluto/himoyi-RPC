package com.himoyi.server.tcp;

import com.himoyi.model.RpcRequest;
import com.himoyi.model.RpcResponse;
import com.himoyi.protocol.ProtocolMessage;
import com.himoyi.protocol.ProtocolMessageDecoder;
import com.himoyi.protocol.ProtocolMessageEncoder;
import com.himoyi.protocol.ProtocolMessageTypeEnum;
import com.himoyi.rateLimit.RateLimitCenter;
import com.himoyi.registry.LocalRegistry;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * TCP请求处理器，用于实现映射到具体的服务类上执行方法并返回结果
 */
@Slf4j
public class TcpServerHandler implements Handler<NetSocket> {
    @Override
    public void handle(NetSocket netSocket) {

        // 构造Wrapper，实现对半包、粘包的处理
        TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
            ProtocolMessage<RpcRequest> protocolMessage;

            try {
                // 获取请求信息，并解码
                protocolMessage = (ProtocolMessage<RpcRequest>) ProtocolMessageDecoder.decode(buffer);
            } catch (IOException e) {
                log.error("消息解码错误！");
                throw new RuntimeException("消息解码错误！");
            }

            RpcRequest rpcRequest = protocolMessage.getData();

            // 检查限流策略，如果无法获取token，则证明被限流，直接抛出异常
            boolean token = RateLimitCenter.getRateLimiter(rpcRequest.getServiceName() + ":" + rpcRequest.getMethodName()).getToken();
            if (!token) {
                log.info("获取Token失败，调用过程被限流！");
                throw new RuntimeException("获取Token失败，调用过程被限流！");
            }

            // 执行请求的方法
            RpcResponse response = invokeMethod(rpcRequest);

            // 构造返回消息
            ProtocolMessage<RpcResponse> responseMessage = createResponse(protocolMessage, response);

            try {
                // 对返回消息进行编码
                Buffer encode_ProtocolResponseMessage = ProtocolMessageEncoder.encode(responseMessage);
                // 写入返回消息
                netSocket.write(encode_ProtocolResponseMessage);
            } catch (IOException e) {
                log.error("编码错误！");
                throw new RuntimeException("编码错误", e);
            }
        });

        // 收到请求后进行处理
        netSocket.handler(bufferHandlerWrapper);
    }


    /**
     * 执行远程调用
     *
     * @param request 请求消息
     * @return 响应结果
     */
    private RpcResponse invokeMethod(RpcRequest request) {
        try {
            Class<?> service = LocalRegistry.get(request.getServiceName());
            Method method = service.getMethod(request.getMethodName(), request.getParameterTypes());
            Object result = method.invoke(service.getDeclaredConstructor().newInstance(), request.getParameters());

            RpcResponse response = new RpcResponse();
            response.setData(result);
            response.setDataType(method.getReturnType());
            response.setMessage("SUCCESS");
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 构造返回信息
     *
     * @param protocolMessage 协议消息
     * @param response        执行结果
     * @return 协议消息
     */
    private ProtocolMessage<RpcResponse> createResponse(ProtocolMessage<RpcRequest> protocolMessage, RpcResponse response) {
        // 构造返回消息
        ProtocolMessage.Header header = protocolMessage.getHeader();
        header.setType((byte) ProtocolMessageTypeEnum.RESPONSE.getKey());
        return new ProtocolMessage<>(header, response);

    }

}
