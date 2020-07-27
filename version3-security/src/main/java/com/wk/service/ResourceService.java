package com.wk.service;

import com.wk.domain.Resource;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

public interface ResourceService extends IService<Resource> {

    List<String> getResourcesByUserId(Integer userId);
}
