package com.wk.controller;

import com.wk.common.Result;
import com.wk.domain.User;
import com.wk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService  userService;

    @GetMapping("{id}")
    public Result<User> getById(@PathVariable Integer id) {
        return Result.ok(userService.getById(id));
    }
}

