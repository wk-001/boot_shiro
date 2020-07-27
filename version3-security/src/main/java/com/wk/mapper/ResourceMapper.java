package com.wk.mapper;

import com.wk.domain.Resource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceMapper extends BaseMapper<Resource> {

    List<String> getResourcesByUserId(Integer userId);
}
