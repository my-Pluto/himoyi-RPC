package com.himoyi;

import com.himoyi.Config.RpcConfig;
import com.himoyi.utils.ConfigUtils;

/**
 * 简易服务消费者示例
 *
 */

public class ConsumerExample {
    public static void main(String[] args) {
        RpcConfig rpc = ConfigUtils.loadConfig("rpc", RpcConfig.class);
        System.out.println(rpc);
    }
}
