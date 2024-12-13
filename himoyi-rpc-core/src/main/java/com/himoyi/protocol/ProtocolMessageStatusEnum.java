package com.himoyi.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProtocolMessageStatusEnum {

    /**
     * 成功
     */
    SUCCESS("success", 20000),

    /**
     * 请求错误
     */
    REQUEST_ERROR("request_error", 40000),

    /**
     * 相应错误
     */
    RESPONSE_ERROR("response_error", 50000);

    /**
     * 状态名
     */
    private final String name;

    /**
     * 状态码
     */
    private final int value;

    /**
     * 通过value获取对应的Enum
     * @param value
     * @return
     */
    public static ProtocolMessageStatusEnum getEnumByValue(int value) {
        for (ProtocolMessageStatusEnum statusEnum : ProtocolMessageStatusEnum.values()) {
            if (statusEnum.value == value) {
                return statusEnum;
            }
        }

        return null;
    }
}
