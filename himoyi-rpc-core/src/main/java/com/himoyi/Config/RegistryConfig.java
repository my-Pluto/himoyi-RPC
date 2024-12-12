package com.himoyi.Config;

import lombok.Builder;
import lombok.Data;

/**
 * PRC注册中心配置类
 */

@Data
public class RegistryConfig {

    /**
     * 名称
     */
    private String registry = "etcd";

    /**
     * 主机
     */
    private String host = "http://localhost";

    /**
     * 端口号
     */
    private Integer port = 2379;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 超时时间（毫秒）
     */
    private Long timeout = 10000L;
}
