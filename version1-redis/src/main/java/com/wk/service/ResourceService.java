package com.wk.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wk.entity.Resource;

import java.util.HashSet;

public interface ResourceService extends IService<Resource> {

	HashSet<String> getResourcesByUserId(Integer userId);
}
