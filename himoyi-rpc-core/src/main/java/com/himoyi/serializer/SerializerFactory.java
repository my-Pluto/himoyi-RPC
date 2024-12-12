package com.himoyi.serializer;


import com.himoyi.utils.SPILoader;

import java.util.HashMap;
import java.util.Map;

public class SerializerFactory {

//    /**
//     * 序列化器映射，用于实现单例模式
//     */
//    private static final Map<String, Serializer> serializerMap = new HashMap<String, Serializer>() {{
//        put(SerializerKeys.JDK, new JdkSerializer());
//        put(SerializerKeys.KRYO, new KryoSerializer());
//        put(SerializerKeys.HESSIAN, new HessianSerializer());
//        put(SerializerKeys.JSON, new JsonSerializer());
//    }};

    /**
     * 使用SPI机制动态加载序列化器
     */
    static {
        SPILoader.load(Serializer.class);
    }

    /**
     * 默认的序列化器
     */
    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    /**
     * 获取序列化器对象
     *
     * @param key
     * @return
     */
    public static Serializer getInstance(String key) {
        return SPILoader.getInstance(Serializer.class, key);
    }
}
