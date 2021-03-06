package com.wk.mapper;

import com.wk.domain.Resource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.HashSet;

@Repository
public interface ResourceMapper extends BaseMapper<Resource> {

    HashSet<String> getResourcesByUserId(Integer userId);
}
