package com.himoyi.protocol;

import cn.hutool.core.util.ObjectUtil;
import com.himoyi.exception.Protocol.RpcProtocolTypeException;
import com.himoyi.exception.Serializer.RpcNotSerializerException;
import com.himoyi.exception.Protocol.RpcProtocolMagicException;
import com.himoyi.model.RpcRequest;
import com.himoyi.model.RpcResponse;
import com.himoyi.serializer.Serializer;
import com.himoyi.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;


/**
 * 消息解码器
 */

@Slf4j
public class ProtocolMessageDecoder {

    public static ProtocolMessage<?> decode(Buffer buffer) throws IOException {
        ProtocolMessage.Header header = new ProtocolMessage.Header();

        byte magic = buffer.getByte(0);

        if (magic != ProtocolConstant.PROTOCOL_MAGIC) {
            log.error("消息非法！");
            throw new RpcProtocolMagicException("消息非法！");
        }

        // 解析消息头
        header.setMagic(magic);
        header.setVersion(buffer.getByte(1));
        header.setSerializer(buffer.getByte(2));
        header.setType(buffer.getByte(3));
        header.setStatus(buffer.getByte(4));
        header.setRequestID(buffer.getLong(5));
        header.setLength(buffer.getInt(13));

        // 解码消息体
        // 为了解决粘包问题，只读取指定长度的数据
        byte[] serializer_data = buffer.getBytes(17, 17 + header.getLength());

        // 获取序列化器
        Serializer serializer = SerializerFactory.getInstance(ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer()).getName());
        if (ObjectUtil.isNull(serializer)) {
            log.error("序列化器不存在！");
            throw new RpcNotSerializerException("序列化器不存在");
        }

        // 获取消息类型信息
        ProtocolMessageTypeEnum messageTypeEnum = ProtocolMessageTypeEnum.getEnumByKey(header.getType());
        if (ObjectUtil.isNull(messageTypeEnum)) {
            log.error("消息类型错误");
            throw new RpcProtocolTypeException("序列化消息类型错误！");
        }

        // 反序列化并返回对应消息信息
        return switch (messageTypeEnum) {
            case REQUEST -> {
                RpcRequest request = serializer.deserialize(serializer_data, RpcRequest.class);
                yield new ProtocolMessage<>(header, request);
            }
            case RESPONSE -> {
                RpcResponse response = serializer.deserialize(serializer_data, RpcResponse.class);
                yield new ProtocolMessage<>(header, response);
            }
            default -> {
                log.error("未知消息类型！");
                throw new RpcProtocolTypeException("未知消息类型！");
            }
        };


    }
}
