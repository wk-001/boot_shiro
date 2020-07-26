package com.wk.service;

import com.wk.domain.Role;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.HashSet;

public interface RoleService extends IService<Role> {

    HashSet<String> getRolesByUserId(Integer userId);
}
