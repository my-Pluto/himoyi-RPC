package com.himoyi.server.tcp;

import com.himoyi.server.Server;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;

/**
 * 自定义的TCP服务器
 */
public class VertxTcpServer implements Server {

    /**
     * 处理请求数据，并构造返回数据
     *
     * @param requestData 请求数据
     * @return 响应数据
     */
    private byte[] handleRequest(byte[] requestData) {
        // todo 此处编写对于请求的处理逻辑，构造一个相应数据并保存
        return "hello Client".getBytes();
    }

    /**
     * 启动
     *
     * @param port 端口号
     */
    @Override
    public void startServer(int port) {

        // 创建实例
        Vertx vertx = Vertx.vertx();

        // 创建一个TCP服务器
        NetServer netServer = vertx.createNetServer();

        // 数据的异步处理逻辑
        netServer.connectHandler(socket -> {
            // 处理链接
            new TcpServerHandler().handle(socket);
//            socket.handler(buffer -> {
//
//                // 接收收到的请求数据
//                byte[] requestData = buffer.getBytes();
//
//                // 处理请求，构造响应数据
//                byte[] responseData = handleRequest(requestData);
//                // 发送响应
//                socket.write(Buffer.buffer(responseData));
//            });
        });

        // 启动TCP服务器并监听指定接口
        netServer.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("TCP Server started on port " + port);
            } else
                System.out.println("TCP Server failed to start on port " + port + ": " + result.cause());
        });
    }

    public static void main(String[] args) {
        new VertxTcpServer().startServer(5050);
    }
}
