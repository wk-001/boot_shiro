package com.wk.service.impl;

import com.wk.domain.User;
import com.wk.mapper.UserMapper;
import com.wk.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
