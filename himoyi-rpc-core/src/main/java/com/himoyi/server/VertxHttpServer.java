package com.himoyi.server;

import io.vertx.core.Vertx;

/**
 * vertx服务器启动类
 */
public class VertxHttpServer implements Server {
    @Override
    public void startServer(int port) {

        // 创建vertx实例
        Vertx vertx = Vertx.vertx();
//        创建http服务器
        io.vertx.core.http.HttpServer httpServer = vertx.createHttpServer();

////        监听对应端口，处理请求
//        httpServer.requestHandler(req -> {
////            处理请求
//            System.out.println("Reveived request" + req.method() + " " + req.uri());
//
////            发出响应
//            req.response()
//                    .putHeader("content-type", "text/plain")
//                    .end("hello from Vert.x HTTP server!");
//        });


        // 监听端口并处理请求
        httpServer.requestHandler(new HttpServerHandler());

//        启动服务器，并监听对应接口
        httpServer.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("HTTP server started on port " + port);
            } else {
                System.out.println("Failed to start erver: " + result.cause());
            }
        });

    }
}
