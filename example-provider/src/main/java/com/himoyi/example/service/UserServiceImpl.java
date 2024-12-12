package com.himoyi.example.service;

import com.himoyi.example.common.model.User;
import com.himoyi.example.common.service.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("用户名："+ user.getUserName());
        return user;
    }
}
