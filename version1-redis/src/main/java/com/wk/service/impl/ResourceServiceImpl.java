package com.wk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wk.entity.Resource;
import com.wk.mapper.ResourceMapper;
import com.wk.service.ResourceService;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {

	@Override
	public HashSet<String> getResourcesByUserId(Integer userId) {
		return baseMapper.getResourcesByUserId(userId);
	}
}
