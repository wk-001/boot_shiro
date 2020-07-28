package com.wk.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wk.domain.User;
import com.wk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService  userService;

    /**
     * 查询分页数据
     */
    @PreAuthorize("hasAuthority('user:view')")
    @GetMapping
    public IPage<User> findListByPage(@RequestParam(name = "pageNum", defaultValue = "1") int pageNum, @RequestParam(name = "pageSize", defaultValue = "20") int pageSize){
        return userService.page(new Page<>(pageNum,pageSize));
    }

    /**
     * 根据id查询
     */
    @PreAuthorize("hasAuthority('user:view')")
    @GetMapping("{id}")
    public User getById(@PathVariable("id") Long id) {
        return userService.getById(id);
    }

    /**
     * 新增
     */
    @PreAuthorize("hasAuthority('user:add')")
    @PostMapping
    public void insert (@RequestBody User user){
        userService.save(user);
    }

    /**
     * 删除
     */
    @PreAuthorize("hasAuthority('user:delete')")
    @DeleteMapping("{id}")
    public void deleteById(@PathVariable("id") Long id) {
        userService.removeById(id);
    }

    /**
     * 修改
     */
    @PreAuthorize("hasAuthority('user:update')")
    @PutMapping
    public void updateById(@RequestBody User user){
        userService.updateById(user);
    }

}

