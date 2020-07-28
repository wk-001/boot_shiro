package com.wk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wk.domain.User;
import com.wk.dto.LoginUser;
import com.wk.service.ResourceService;
import com.wk.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Autowired
    private ResourceService resourceService;

    /**
     * 认证授权，相当于shiro的realm
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getOne(new QueryWrapper<User>().eq("username", username));
        if (user == null) {
            throw new AuthenticationCredentialsNotFoundException("用户名不存在");
        } else if (user.getEnable() != 1) {
            throw new LockedException("用户被锁定,请联系管理员");
        }

        LoginUser loginUser = new LoginUser();
        BeanUtils.copyProperties(user, loginUser);

        //设置权限标识符到对象中
        loginUser.setPermissions(resourceService.getResourcesByUserId(user.getId()));

        return loginUser;
    }
}
