package com.wk.shiro;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wk.entity.User;
import com.wk.service.ResourceService;
import com.wk.service.RoleService;
import com.wk.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;

@Slf4j
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ResourceService resourceService;

    /**
     * 认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String userName = token.getPrincipal().toString();
        log.info("用户："+userName+"开始认证");

        User user = userService.getOne(new QueryWrapper<User>().eq("username", userName));
        if (user == null)
            throw new UnknownAccountException("用户名或密码错误！");

        if (user.getEnable() == 0)
            throw new LockedAccountException("该账号不可用！");

        Integer userId = user.getId();

        /**
         * 该项目角色和权限针对的是一个用户有多个角色的情况
         * 如果一个用户对应一个角色，user对象设置roleCode为String类型
         * 根据用户ID查询对应角色，再根据角色ID查询所有权限
         */
        HashSet<String> roles = roleService.getRolesByUserId(userId);
        Assert.notEmpty(roles, "请分配角色");
        user.setRoleCode(roles);

        HashSet<String> resources = resourceService.getResourcesByUserId(userId);
        Assert.notEmpty(resources, "请授权");
        user.setResourceCode(resources);

        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user,user.getPassword(), new MySimpleByteSource(user.getSalt()),getName());
        //验证成功开始踢人(清除缓存和Session)
        ShiroUtils.deleteCache(user.getUsername(),true);
        log.info("用户："+userName+"认证成功");
        return info;
    }

    /**
     * 用户进行权限验证时候Shiro会去缓存中找,如果查不到数据,会执行这个方法去查权限,并放入缓存中
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principal) {
        User user = (User) principal.getPrimaryPrincipal();
        log.info("用户："+user.getUsername()+"开始授权");

        //将用户的角色编码和资源标识放入认证信息
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setRoles(user.getRoleCode());
        info.setStringPermissions(user.getResourceCode());

        log.info("用户："+user.getUsername()+"授权成功");
        return info;
    }

}
