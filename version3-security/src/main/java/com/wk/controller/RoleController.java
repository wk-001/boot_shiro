package com.wk.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wk.domain.Role;
import com.wk.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService  roleService;

    /**
     * 查询分页数据
     */
    @PreAuthorize("hasAuthority('role:view')")
    @GetMapping
    public IPage<Role> findListByPage(@RequestParam(name = "pageNum", defaultValue = "1") int pageNum, @RequestParam(name = "pageSize", defaultValue = "20") int pageSize){
        return roleService.page(new Page<>(pageNum,pageSize));
    }

    /**
     * 根据id查询
     */
    @PreAuthorize("hasAuthority('role:view')")
    @GetMapping("{id}")
    public Role getById(@PathVariable("id") Long id) {
        return roleService.getById(id);
    }

    /**
     * 新增
     */
    @PreAuthorize("hasAuthority('role:add')")
    @PostMapping
    public void insert (@RequestBody Role role){
        roleService.save(role);
    }

    /**
     * 删除
     */
    @PreAuthorize("hasAuthority('role:delete')")
    @DeleteMapping("{id}")
    public void deleteById(@PathVariable("id") Long id) {
        roleService.removeById(id);
    }

    /**
     * 修改
     */
    @PreAuthorize("hasAuthority('role:update')")
    @PutMapping
    public void updateById(@RequestBody Role role){
        roleService.updateById(role);
    }

}

