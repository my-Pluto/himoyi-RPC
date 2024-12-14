package com.himoyi;

import cn.hutool.core.util.ObjectUtil;
import com.himoyi.example.common.model.User;
import com.himoyi.example.common.service.UserService;
import com.himoyi.proxy.ServiceProxyFactory;


public class EasyConsumerExample {
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 1; i++) {
            UserService userService = ServiceProxyFactory.getProxy(UserService.class);
            User user = new User();
            user.setUserName("himoyi");
            User newUser = userService.getUser(user);
            if (ObjectUtil.isNotNull(newUser)) {
                System.out.println(newUser.getUserName());
            } else {
                System.out.println("newUser is null");
            }

            Thread.sleep(1000);
        }
    }
}
