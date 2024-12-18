package com.himoyi.serializer;

import com.himoyi.exception.Serializer.RpcSerializerFailException;

import java.io.*;

/**
 * JDK序列化器
 */
public class JdkSerializer implements Serializer {
    /**
     * 序列化
     *
     * @param obj 要序列化的对象
     * @param <T> 对象的参数类型
     * @return 序列化后的数组
     * @throws IOException
     */
    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        // 创建一个ByteArrayOutputStream来存储序列化后的数据
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 使用ObjectOutputStream将对象写入到ByteArrayOutputStream中
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

//        写对象
        objectOutputStream.writeObject(obj);
        objectOutputStream.close();

//        返回结果
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * 反序列化
     *
     * @param data 包含序列化对象的字节数组
     * @param clazz 对象的class对象
     * @return 反序列化后的对象
     * @param <T> 对象类型
     * @throws IOException
     */
    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws IOException {
        // 创建一个ByteArrayInputStream来读取字节数组
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        // 使用ObjectInputStream从ByteArrayInputStream中读取对象
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

        try {
            // 读取对象并进行类型转换
            return (T) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            throw new RpcSerializerFailException("消息反序列化失败！");
        } finally {
            objectInputStream.close();
        }
    }
}
