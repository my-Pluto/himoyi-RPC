package com.himoyi.server.tcp;

import io.vertx.core.Vertx;
import io.vertx.core.net.NetSocket;

/**
 * TCP客户端实现类
 */
public class VertxTCPClient {

    public void startClient(String host, int port) {
        Vertx vertx = Vertx.vertx();

        vertx.createNetClient().connect(port, host, res -> {
           if (res.succeeded()) {
               System.out.println("Connected to " + host + ":" + port);
               NetSocket socket = res.result();

               socket.write("Hello Server");

               socket.handler(buffer -> {
                   System.out.println("Client received: " + buffer.toString());
               });
           } else {
               System.out.println("Failed to connect to " + host + ":" + port);
           }
        });
    }

    public static void main(String[] args) {
        new VertxTCPClient().startClient("127.0.0.1", 5050);
    }
}
