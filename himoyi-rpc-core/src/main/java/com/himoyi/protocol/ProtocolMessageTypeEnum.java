package com.himoyi.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProtocolMessageTypeEnum {

    /**
     * 请求
     */
    REQUEST(0),

    /**
     * 响应
     */
    RESPONSE(1),

    /**
     * 心跳包
     */
    HEART_BEAT(2),

    /**
     * 其他类型
     */
    OTHERS(3);

    /**
     * 类型码
     */
    private final int key;

    /**
     * 根据key获取对应的Enum
     * @param key
     * @return
     */
    public static ProtocolMessageTypeEnum getEnumByKey(int key) {
        for (ProtocolMessageTypeEnum protocolMessageTypeEnum : ProtocolMessageTypeEnum.values()) {
            if (protocolMessageTypeEnum.key == key) {
                return protocolMessageTypeEnum;
            }
        }

        return null;
    }
}
