package com.himoyi.utils;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.ObjectUtil;
import com.himoyi.constant.RpcConstant;
import com.himoyi.exception.spi.RpcSPIFailClassException;
import com.himoyi.exception.spi.RpcSPINoInterfaceException;
import com.himoyi.exception.spi.RpcSPINoKeyException;
import com.himoyi.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SPI加载器，支持键值对映射
 */

@Slf4j
public class SPILoader {

    /**
     * 存储已经加载的类：接口名，实现类map
     */
    private static Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();

    /**
     * 针对对象实例的缓存，防止重复加载
     * 类路径，对象实例
     */
    private static Map<String, Object> hasLoaderMap = new ConcurrentHashMap<>();

    /**
     * 扫描路径
     */
    private static final String[] SCAN_DIRS = new String[]{RpcConstant.RPC_SYSTEM_SPI_DIR, RpcConstant.RPC_CUSTOM_SPI_DIR};

    /**
     * 动态加载的类列表
     */
    private static final List<Class<?>> LOAD_CLASS_LIST = Arrays.asList(Serializer.class);

    /**
     * 加载所有 SPI
     */
    public static void loadAll() {
        log.info("加载所有 SPI");
        LOAD_CLASS_LIST.forEach(SPILoader::load);
    }

    /**
     * 加载指定类型的 SPI
     *
     * @param clazz
     * @return
     */
    public static Map<String, Class<?>> load(Class<?> clazz) {
        log.info("加载类型为 {} 的 SPI", clazz.getName());

        // 记录加载列表
        Map<String, Class<?>> keyClassMap = new HashMap<>();

        // 扫描，用户定义的 SPI 优先级高于系统SPI（因为后加载，可以覆盖前面的）
        for (String scanDir : SCAN_DIRS) {

            // 获取配置文件路径
            List<URL> resources = ResourceUtil.getResources(scanDir + clazz.getName());

            // 循环加载
            for (URL resource : resources) {
                try {
                    // 读取文件
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    String line;
                    // 读取每一行，然后实例化，保存到map中
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] split = line.split("=");
                        if (split.length == 2) {
                            keyClassMap.put(split[0], Class.forName(split[1]));
                        }
                    }
                } catch (Exception e) {
                    log.error("SPI resources load error");
                }
            }
        }

        // 保存加载结果
        loaderMap.put(clazz.getName(), keyClassMap);
        return keyClassMap;
    }

    ;

    /**
     * 获取某个接口的实例
     *
     * @param clazz
     * @param key
     * @param <T>
     * @return
     */
    public static <T> T getInstance(Class<T> clazz, String key) {

        // 获得指定接口的所有实现类
        String className = clazz.getName();
        Map<String, Class<?>> keyClassMap = loaderMap.get(className);

        // 如果不存在该接口，证明它未实现SPI
        if (ObjectUtil.isNull(keyClassMap)) {
            throw new RpcSPINoInterfaceException(String.format("SPILoader 未加载 %s 类型", className));
        }

        // 如果map中不存在key，证明该实现类不存在
        if (!keyClassMap.containsKey(key)) {
            throw new RpcSPINoKeyException(String.format("SpiLoader 的 %s 不存在 key=%s 的类型", className, key));
        }

        // 获取实现类的class信息
        Class<?> aClass = keyClassMap.get(key);

        // 如果该实现类未被缓存，则缓存，防止重复实现
        String name = aClass.getName();
        if (!hasLoaderMap.containsKey(name)) {
            try {
                hasLoaderMap.put(name, aClass.newInstance());
            } catch (Exception e) {
                throw new RpcSPIFailClassException(String.format("%s 类实例化失败", name));
            }
        }

        return (T) hasLoaderMap.get(name);
    }

    /**
     * 获取指定类的class信息
     *
     * @param clazz
     * @param key
     * @param <T>
     * @return
     */
    public static <T> T getNewInstance(Class<T> clazz, String key, Class[] paramaterTypes, Object[] params) {
        // 获得指定接口的所有实现类
        String className = clazz.getName();
        Map<String, Class<?>> keyClassMap = loaderMap.get(className);

        // 如果不存在该接口，证明它未实现SPI
        if (ObjectUtil.isNull(keyClassMap)) {
            throw new RpcSPINoInterfaceException(String.format("SPILoader 未加载 %s 类型", className));
        }

        // 如果map中不存在key，证明该实现类不存在
        if (!keyClassMap.containsKey(key)) {
            throw new RpcSPINoKeyException(String.format("SpiLoader 的 %s 不存在 key=%s 的类型", className, key));
        }

        // 获取实现类的class信息
        try {
            return (T) keyClassMap.get(key).getDeclaredConstructor(paramaterTypes).newInstance(params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取加载的所有类对象
     * @param className
     * @return
     */
    public static Map<String, Class<?>> getClassMap(String className) {
        return loaderMap.get(className);
    }
}
