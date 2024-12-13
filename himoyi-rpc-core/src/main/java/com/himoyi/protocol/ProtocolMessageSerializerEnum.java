package com.himoyi.protocol;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 协议消息序列化器枚举
 */

@Getter
@AllArgsConstructor
public enum ProtocolMessageSerializerEnum {

    /**
     * JDK序列化器
     */
    JDK(0, "jdk"),

    /**
     * JSON序列化器
     */
    JSON(1, "json"),

    /**
     * HRYO序列化器
     */
    KRYO(2, "kryo"),

    /**
     * HESSIAN序列化器
     */
    HESSIAN(3, "hessian");


    /**
     * 序列化器key
     */
    private final int key;

    /**
     * 序列化器名称
     */
    private final String name;

    /**
     * 获取所有序列化器的名称列表
     *
     * @return
     */
    public static List<String> getSerializerNameList() {
        return Arrays.stream(ProtocolMessageSerializerEnum.values()).map(ProtocolMessageSerializerEnum::getName).collect(Collectors.toList());
    }

    /**
     * 根据key获取枚举
     *
     * @param key
     * @return
     */
    public static ProtocolMessageSerializerEnum getEnumByKey(int key) {
        for (ProtocolMessageSerializerEnum protocolMessageSerializerEnum : ProtocolMessageSerializerEnum.values()) {
            if (protocolMessageSerializerEnum.getKey() == key) {
                return protocolMessageSerializerEnum;
            }
        }

        return null;
    }

    /**
     * 根据序列化器名称获取枚举
     *
     * @param name
     * @return
     */
    public static ProtocolMessageSerializerEnum getEnumByName(String name) {
        for (ProtocolMessageSerializerEnum protocolMessageSerializerEnum : ProtocolMessageSerializerEnum.values()) {
            if (protocolMessageSerializerEnum.getName().equals(name)) {
                return protocolMessageSerializerEnum;
            }
        }

        return null;
    }
}
