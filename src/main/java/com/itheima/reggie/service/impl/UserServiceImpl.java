package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.mapper.UserMapper;
import com.itheima.reggie.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @ClassName UserServiceImpl
 * @Description TODO
 * @Author lmy
 * @Date 2022/8/25 08:16
 **/
@SuppressWarnings({"all"})
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
