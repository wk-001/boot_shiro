package com.wk.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wk.domain.Resource;
import com.wk.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resource")
public class ResourceController {

    @Autowired
    private ResourceService  resourceService;

    /**
     * 查询分页数据
     */
    @PreAuthorize("hasAuthority('resource:view')")
    @GetMapping
    public IPage<Resource> findListByPage(@RequestParam(name = "pageNum", defaultValue = "1") int pageNum, @RequestParam(name = "pageSize", defaultValue = "20") int pageSize){
        return resourceService.page(new Page<>(pageNum,pageSize));
    }

    /**
     * 根据id查询
     */
    @PreAuthorize("hasAuthority('resource:view')")
    @GetMapping("{id}")
    public Resource getById(@PathVariable("id") Long id) {
        return resourceService.getById(id);
    }

    /**
     * 新增
     */
    @PreAuthorize("hasAuthority('resource:add')")
    @PostMapping
    public void insert (@RequestBody Resource resource){
        resourceService.save(resource);
    }

    /**
     * 删除
     */
    @PreAuthorize("hasAuthority('resource:delete')")
    @DeleteMapping("{id}")
    public void deleteById(@PathVariable("id") Long id) {
        resourceService.removeById(id);
    }

    /**
     * 修改
     */
    @PreAuthorize("hasAuthority('resource:update')")
    @PutMapping
    public void updateById(@RequestBody Resource resource){
        resourceService.updateById(resource);
    }

}

