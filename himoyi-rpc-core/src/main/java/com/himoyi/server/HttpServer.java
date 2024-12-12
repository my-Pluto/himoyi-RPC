package com.himoyi.server;

/**
 * http服务器启动接口
 */
public interface HttpServer {

    /**
     * 启动服务器
     * @param port 端口号
     */
    void startHttpServer(int port) ;
}
