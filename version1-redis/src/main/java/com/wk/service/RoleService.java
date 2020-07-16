package com.wk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wk.entity.Role;

import java.util.HashSet;

public interface RoleService extends IService<Role> {

	HashSet<String> getRolesByUserId(Integer userId);
}
