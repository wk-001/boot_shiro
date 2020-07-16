package com.wk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wk.entity.Role;
import com.wk.mapper.RoleMapper;
import com.wk.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

	@Override
	public HashSet<String> getRolesByUserId(Integer userId) {
		return baseMapper.getRolesByUserId(userId);
	}
}
