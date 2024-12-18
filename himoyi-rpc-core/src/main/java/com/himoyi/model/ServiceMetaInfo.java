package com.himoyi.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * 服务元信息（用于注册）
 */

@Data
public class ServiceMetaInfo implements Serializable {

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务版本
     * 默认1.0
     */
    private String ServiceVersion = "1.0";

    /**
     * 服务节点的地址
     */
    private String serviceHost;

    /**
     * 服务节点的端口
     */
    private Integer servicePort;

    /**
     * 服务的token配置
     */
    private String token;

    /**
     * 服务分组（未实现）
     */
    private String serviceGroup;

    /**
     * 获取服务的key
     *
     * @return
     */
    public String getServiceKey() {
        return String.format("%s:%s", getServiceName(), getServiceVersion());
    }

    /**
     * 获取具体服务节点的key
     *
     * @return
     */
    public String getServiceNodeKey() {
        return String.format("%s/%s:%s", getServiceKey(), getServiceHost(), getServicePort());
    }

    /**
     * 获取当前服务的完整地址
     * @return 服务完整地址
     */
    public String getServiceAddress() {
        if (!StrUtil.contains(getServiceHost(), "http")) {
            return String.format("http://%s:%s", getServiceHost(), getServicePort());
        }

        return String.format("%s:%s", getServiceHost(), getServicePort());
    }
}
