package com.wk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wk.entity.Role;
import org.springframework.stereotype.Repository;

import java.util.HashSet;

@Repository
public interface RoleMapper extends BaseMapper<Role> {

	HashSet<String> getRolesByUserId(Integer userId);
}
