package com.wk.service;

import com.wk.domain.Resource;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.HashSet;

public interface ResourceService extends IService<Resource> {

    HashSet<String> getResourcesByUserId(Integer userId);
}
