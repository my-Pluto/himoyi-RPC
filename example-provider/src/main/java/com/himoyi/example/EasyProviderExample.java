package com.himoyi.example;

import com.himoyi.example.common.service.UserService;
import com.himoyi.example.service.UserServiceImpl;
import com.himoyi.registry.LocalRegistry;
import com.himoyi.server.HttpServer;
import com.himoyi.server.VertxHttpServer;

public class EasyProviderExample {
    public static void main(String[] args) {

        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        HttpServer httpServer = new VertxHttpServer();
        httpServer.startHttpServer(5050);
    }
}
