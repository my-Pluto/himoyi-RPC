package com.himoyi.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.himoyi.RpcApplication;
import com.himoyi.constant.RpcConstant;
import com.himoyi.model.RpcRequest;
import com.himoyi.model.RpcResponse;
import com.himoyi.model.ServiceMetaInfo;
import com.himoyi.registry.Registry;
import com.himoyi.registry.RegistryFactory;
import com.himoyi.serializer.JdkSerializer;
import com.himoyi.serializer.Serializer;
import com.himoyi.serializer.SerializerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 服务代理类（JDK动态代理）
 */
public class ServiceProxy implements InvocationHandler {

    private static final Logger log = LoggerFactory.getLogger(ServiceProxy.class);

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 构造序列化器
        System.out.println(RpcApplication.getRpcConfig().getSerializer());
        Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());;
//        final Serializer serializer = new JdkSerializer();
        // 构造请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .parameters(args)
                .build();

        try {
            // 对请求进行序列化
            byte[] serialize = serializer.serialize(rpcRequest);

            // 从注册中心获取服务信息
            ServiceMetaInfo serviceMetaInfo = getServiceMetaInfo(method.getDeclaringClass().getName());

            // 发起请求
            try (HttpResponse httpResponse = HttpRequest.post(serviceMetaInfo.getServiceAddress())
                         .body(serialize)
                         .execute()) {

                // 对请求结果反序列化
                byte[] bytes = httpResponse.bodyBytes();
                RpcResponse deserialize = serializer.deserialize(bytes, RpcResponse.class);

                // 返回结果
                return deserialize.getData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 从服务中心根据服务名获取一个服务地址
     * @param serviceName
     * @return
     */
    private ServiceMetaInfo getServiceMetaInfo(String serviceName) {
        Registry registry = RegistryFactory.getRegistry(RpcApplication.getRpcConfig().getRegistryConfig().getRegistry());

        List<ServiceMetaInfo> serviceMetaInfos = registry.serviceDiscovery(String.format("%s:%s", serviceName, RpcConstant.DEFAULT_SERVICE_VERSION));

        if (CollUtil.isEmpty(serviceMetaInfos)) {
            log.error("暂无服务地址");
            throw new RuntimeException("暂无服务地址");
        }

        // todo 暂时返回第一个，之后需要实现轮询
        return serviceMetaInfos.get(0);


    }
}
