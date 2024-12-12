package com.himoyi.registry;

import com.himoyi.model.ServiceMetaInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 已注册的服务的缓存
 */
public class RegistryServiceCache {

    // 缓存
    Map<String, List<ServiceMetaInfo>> cache= new HashMap<>();

    /**
     * 获取服务缓存
     * @param serviceKey 服务名称
     * @return 服务元数据列表
     */
    public List<ServiceMetaInfo> getCache(String serviceKey) {
        return cache.get(serviceKey);
    }

    /**
     * 添加缓存
     * @param serviceKey 服务名称
     * @param serviceMetaInfos 服务元数据列表
     */
    public void putCache(String serviceKey, List<ServiceMetaInfo> serviceMetaInfos) {
        cache.put(serviceKey, serviceMetaInfos);
    }

    /**
     * 清空某一服务的缓存
     * @param serviceKey
     */
    public void clearCache(String serviceKey) {
        cache.remove(serviceKey);
    }

    /**
     * 清空所有缓存
     */
    public void clearAllCache() {
        cache.clear();
    }
}
