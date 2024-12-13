package com.himoyi.example;

import com.himoyi.example.common.service.UserService;
import com.himoyi.example.service.UserServiceImpl;
import com.himoyi.registry.LocalRegistry;
import com.himoyi.server.Server;
import com.himoyi.server.VertxHttpServer;

public class EasyProviderExample {
    public static void main(String[] args) {

        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        Server server = new VertxHttpServer();
        server.startServer(5050);
    }
}
