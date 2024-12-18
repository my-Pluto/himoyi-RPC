package com.himoyi.registry;

import com.himoyi.Config.RegistryConfig;
import com.himoyi.model.ServiceMetaInfo;

import java.util.List;

/**
 * 注册中心接口
 */
public interface Registry {

    /**
     * 初始化注册中心
     *
     * @param registryConfig 注册中心配置文件
     */
    void init(RegistryConfig registryConfig);

    /**
     * 服务销毁
     * 用于销毁注册中心
     */
    void destroy();

/**************************************** 服务端使用的方法 ***************************************************/

    /**
     * 服务注册
     * 服务端使用
     *
     * @param metaInfo 服务元信息
     * @throws Exception
     */
    void register(ServiceMetaInfo metaInfo) throws Exception;

    /**
     * 服务注销
     * 服务端使用
     *
     * @param metaInfo 服务元信息
     * @throws Exception
     */
    void unRegister(ServiceMetaInfo metaInfo) throws Exception;


    /**
     * 心跳检测，实现自动续约
     * 服务端使用
     */
    void heartbeat();

    /**
     * 获取token
     * 当开启token验证时有效
     *
     * @return
     */
    String getToken(String key);


/**************************************** 消费端使用的方法 ***************************************************/

    /**
     * 服务发现，获取某个服务的所有节点
     * 消费端使用
     *
     * @param serviceKey
     * @return
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    /**
     * 监听机制，用于监听某个服务的信息是否发生变化
     *
     * @param serviceKey
     */
    void watch(String serviceKey);
}
