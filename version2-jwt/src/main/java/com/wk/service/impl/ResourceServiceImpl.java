package com.wk.service.impl;

import com.wk.domain.Resource;
import com.wk.mapper.ResourceMapper;
import com.wk.service.ResourceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {

}
