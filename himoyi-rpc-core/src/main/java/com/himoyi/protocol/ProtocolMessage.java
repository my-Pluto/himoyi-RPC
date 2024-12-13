package com.himoyi.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自定义消息结构
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolMessage<T> {

    /**
     * 消息头
     */
    private Header header;

    /**
     * 消息体
     */
    private T data;

    /**
     * 消息头定义
     */
    @Data
    public static class Header {

        /**
         * 魔数，用于保证安全性
         */
        private byte magic;

        /**
         * 版本
         */
        private byte version;

        /**
         * 序列化器类型
         */
        private byte serializer;

        /**
         * 消息类型
         */
        private byte type;

        /**
         * 消息状态
         */
        private byte status;

        /**
         * 请求ID
         */
        private long requestID;

        /**
         * 消息体长度
         */
        private int length;


    }
}
