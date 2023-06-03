package com.chenchen.reggie.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chenchen.reggie.entity.User;
import com.chenchen.reggie.mapper.UserMapper;
import com.chenchen.reggie.service.UserService;
import org.springframework.stereotype.Service;

/**
 * Service：用于标记该类是一个Spring Bean。
 * ServiceImpl：继承这个类可以免去编写基本的增删改查等操作
 * UserService：可以调用这个接口的常用方法
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
