package com.wk.controller;

import com.wk.common.Result;
import com.wk.domain.User;
import com.wk.dto.LoginUser;
import com.wk.utils.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("login")
    public Result<String> login(@Validated @RequestBody User user){
        // 用户验证
        Authentication authentication = null;
        try {
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        } catch (Exception e) {
            if (e instanceof BadCredentialsException) {
                return Result.fail("账号或密码错误");
            }
            e.printStackTrace();
        }
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        // 生成token
        return Result.ok(tokenService.createToken(loginUser));
    }

}
