package com.himoyi.serializer;

import java.io.IOException;

/**
 * 序列化器接口
 */
public interface Serializer {

    /**
     * 序列化
     *
     * @param obj
     * @param <T>
     * @return
     * @throws IOException
     */
//  <T>是泛型方法的语法，表示该方法是一个泛型方法，
//  T是一个类型参数。允许方法在调用时接受任何类型，并在方法内部使用这个类型。
    <T> byte[] serialize(T obj) throws IOException;

    /**
     * 反序列化
     *
     * @param data
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T deserialize(byte[] data, Class<T> clazz) throws IOException;
}
