package com.himoyi.example;

import com.himoyi.Config.RegistryConfig;
import com.himoyi.Config.RpcConfig;
import com.himoyi.RpcApplication;
import com.himoyi.bootstrap.ProviderBootStrap;
import com.himoyi.example.common.service.UserService;
import com.himoyi.example.service.UserServiceImpl;
import com.himoyi.model.ServiceMetaInfo;
import com.himoyi.model.ServiceRegisterInfo;
import com.himoyi.registry.LocalRegistry;
import com.himoyi.registry.Registry;
import com.himoyi.registry.RegistryFactory;
import com.himoyi.server.Server;
import com.himoyi.server.tcp.VertxTcpServer;

import java.util.ArrayList;

public class ProviderBootStrapExample {
    public static void main(String[] args) {
        ArrayList<ServiceRegisterInfo<?>> serviceRegisterInfos = new ArrayList<>();
        serviceRegisterInfos.add(new ServiceRegisterInfo<>(UserService.class.getName(), UserServiceImpl.class));
        ProviderBootStrap.init(serviceRegisterInfos);
    }
}
