package com.himoyi.protocol;

import cn.hutool.core.util.ObjectUtil;
import com.himoyi.serializer.Serializer;
import com.himoyi.serializer.SerializerFactory;
import io.vertx.core.buffer.Buffer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 消息编码器
 */

@Slf4j
public class ProtocolMessageEncoder {


    /**
     * 编码
     * @param message
     * @return
     * @throws IOException
     */
    public static Buffer encode(ProtocolMessage<?> message) throws IOException {

        Buffer buffer = Buffer.buffer();
        if (ObjectUtil.isNull(message) || ObjectUtil.isNull(message.getHeader())) {
            return buffer;
        }

        ProtocolMessage.Header header = message.getHeader();

        // 写入消息头数据
        buffer.appendByte(header.getMagic())
                .appendByte(header.getVersion())
                .appendByte(header.getSerializer())
                .appendByte(header.getType())
                .appendByte(header.getStatus())
                .appendLong(header.getRequestID());

        // 获取序列化器实例
        Serializer serializer = SerializerFactory.getInstance(ProtocolMessageSerializerEnum.getEnumByKey(header.getSerializer()).getName());

        if (ObjectUtil.isNull(serializer)) {
            log.error("serializer is null");
            throw new RuntimeException("serializer is null");
        }

        // 消息体编码
        byte[] serialize_data = serializer.serialize(message.getData());

        // 写入消息长度和消息体
        buffer.appendInt(serialize_data.length)
                .appendBytes(serialize_data);

        return buffer;
    }
}
