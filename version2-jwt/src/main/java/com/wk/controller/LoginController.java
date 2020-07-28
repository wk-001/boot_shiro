package com.wk.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wk.common.RedisConstant;
import com.wk.common.Result;
import com.wk.domain.User;
import com.wk.jwt.JWTUtil;
import com.wk.service.ResourceService;
import com.wk.service.RoleService;
import com.wk.service.UserService;
import com.wk.shiro.ShiroUtil;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

@RestController
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ResourceService resourceService;

    private static final String TOKEN = "Authorization";

    @PostMapping("login")
    public Result<User> login(@Validated @RequestBody User loginUser){
        String username = loginUser.getUsername();
        String password = loginUser.getPassword();

        User dbUser = userService.getOne(new QueryWrapper<User>().eq("username", username));

        if (dbUser == null)
            throw new UnauthorizedException("用户不存在!");

        String encodedPassword = new SimpleHash("MD5", password, ByteSource.Util.bytes(dbUser.getSalt()), 2).toString();
        if (!dbUser.getPassword().equals(encodedPassword))
            throw new IncorrectCredentialsException("账号或密码错误");

        Integer userId = dbUser.getId();

        //生成token
        String token = JWTUtil.sign(userId, encodedPassword);
        dbUser.setToken(token);

        //返回前端需要的数据 token、角色、权限
        HashSet<String> roles = roleService.getRolesByUserId(userId);
        dbUser.setRoleCode(roles);

        HashSet<String> resources = resourceService.getResourcesByUserId(userId);
        dbUser.setResourceCode(resources);

        dbUser.setPassword("");
        dbUser.setSalt("");
        redisTemplate.boundValueOps(RedisConstant.USER_INFO + userId).set(JSON.toJSONString(dbUser), 30, TimeUnit.MINUTES);

        return Result.ok(dbUser);
    }

    @RequiresAuthentication
    @GetMapping("logout")
    public void logout(HttpServletRequest req){
        String token = req.getHeader(TOKEN);
        String userId = JWTUtil.getUserId(token);
        redisTemplate.delete(RedisConstant.USER_INFO + userId);
        ShiroUtil.logout();
        redisTemplate.delete(RedisConstant.SHIRO_AUTHOR + userId);
    }

}
