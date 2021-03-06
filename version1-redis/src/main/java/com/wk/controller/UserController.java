package com.wk.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wk.entity.User;
import com.wk.service.UserService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService  userService;

    /**
     * 查询分页数据
     */
    @RequiresPermissions("user:view")
    @GetMapping
    public IPage<User> findListByPage(@RequestParam(name = "pageNum", defaultValue = "1") int pageNum, @RequestParam(name = "pageSize", defaultValue = "20") int pageSize){
        return userService.page(new Page<>(pageNum,pageSize));
    }

    /**
     * 根据id查询
     */
    @RequiresPermissions("user:view")
    @GetMapping("{id}")
    public User getById(@PathVariable("id") Long id) {
        return userService.getById(id);
    }

    /**
     * 新增
     */
    @RequiresPermissions("user:add")
    @PostMapping
    public void insert (@RequestBody User user){
        userService.save(user);
    }

    /**
     * 删除
     */
    @RequiresPermissions("user:delete")
    @DeleteMapping("{id}")
    public void deleteById(@PathVariable("id") Long id) {
        userService.removeById(id);
    }

    /**
     * 修改
     */
    @RequiresPermissions("user:update")
    @PutMapping
    public void updateById(@RequestBody User user){
        userService.updateById(user);
    }

}

