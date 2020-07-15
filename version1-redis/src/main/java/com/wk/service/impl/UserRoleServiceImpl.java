package com.wk.service.impl;

import com.wk.entity.UserRole;
import com.wk.mapper.UserRoleMapper;
import com.wk.service.UserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

}
