package com.himoyi.bootstrap;

import com.himoyi.RpcApplication;

/**
 * 消费者服务启动类
 */
public class ConsumerBootStrap {

    public static void init() {
        // RPC 框架初始化，主要是配置和注册中心
        RpcApplication.init();
    }
}
