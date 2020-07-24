package com.wk.service.impl;

import com.wk.domain.Role;
import com.wk.mapper.RoleMapper;
import com.wk.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

}
