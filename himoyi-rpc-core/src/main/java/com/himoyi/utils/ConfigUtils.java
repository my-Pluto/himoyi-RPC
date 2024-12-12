package com.himoyi.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

/**
 * 配置工具类
 */
public class ConfigUtils {

    /**
     * 加载配置文件
     * @param prefix
     * @param clazz
     * @return
     * @param <T>
     */
    public static <T> T loadConfig(String prefix, Class<T> clazz) {
        return loadConfig(clazz, prefix, "");
    }

    /**
     * 加载配置文件，可区分环境
     *
     * @param clazz       配置文件类
     * @param prefix      前缀信息
     * @param environment 用于区分环境信息，如dev等
     * @param <T>
     * @return 配置文件实体类
     */
    public static <T> T loadConfig(Class<T> clazz, String prefix, String environment) {
        StringBuilder configFileBuilder = new StringBuilder("application");

        if (StrUtil.isNotBlank(environment)) {
            configFileBuilder.append("-").append(environment);
        }

        configFileBuilder.append(".properties");

        Props props = new Props(configFileBuilder.toString());
        return props.toBean(clazz, prefix);
    }
}
