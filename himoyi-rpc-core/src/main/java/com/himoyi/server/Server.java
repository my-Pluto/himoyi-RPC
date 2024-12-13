package com.himoyi.server;

/**
 * http服务器启动接口
 */
public interface Server {

    /**
     * 启动服务器
     * @param port 端口号
     */
    void startServer(int port) ;
}
