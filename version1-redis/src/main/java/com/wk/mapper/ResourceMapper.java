package com.wk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wk.entity.Resource;
import org.springframework.stereotype.Repository;

import java.util.HashSet;

@Repository
public interface ResourceMapper extends BaseMapper<Resource> {

	HashSet<String> getResourcesByUserId(Integer userId);
}
