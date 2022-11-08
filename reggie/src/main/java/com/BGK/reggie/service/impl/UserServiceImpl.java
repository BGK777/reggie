package com.BGK.reggie.service.impl;

import com.BGK.reggie.mapper.UserMapper;
import com.BGK.reggie.pojo.User;
import com.BGK.reggie.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
