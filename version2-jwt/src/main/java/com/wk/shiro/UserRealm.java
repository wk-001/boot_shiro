package com.wk.shiro;

import com.alibaba.fastjson.JSON;
import com.wk.common.RedisConstant;
import com.wk.domain.User;
import com.wk.jwt.JWTToken;
import com.wk.jwt.JWTUtil;
import com.wk.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 必须重写此方法，不然Shiro会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    /**
     * 认证用户
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        // 这里的 token是从 JWTFilter 的 executeLogin 方法传递过来的
        String token = (String) authenticationToken.getCredentials();
        String userId = JWTUtil.getUserId(token);
        if(userId == null)
            throw new AuthenticationException("token非法无效!");

        User user;

        //获取缓存中的user对象
        String userStr = redisTemplate.boundValueOps(RedisConstant.USER_INFO + userId).get();
        if(StringUtils.isNotBlank(userStr))
            user = JSON.parseObject(userStr, User.class);
        else
            user = userService.getById(userId);

        if (user == null)
            throw new UnauthorizedException("用户不存在！");

        //校验token
        if (!JWTUtil.verify(token, userId, user.getPassword()))
            throw new AuthenticationException("token校验不通过");

        //如果Redis中token的过期时间小于10分钟则刷新过期时间
        refreshToken(token);

        return new SimpleAuthenticationInfo(user, token,getName());
    }

    private void refreshToken(String token) {
        String userId = JWTUtil.getUserId(token);
        BoundValueOperations<String, String> valueOps = redisTemplate.boundValueOps(RedisConstant.USER_INFO + userId);

        Long expire = valueOps.getExpire();
        //如果token过期时间小于10分钟就刷新过期时间
        if(expire <= 600){
            valueOps.expire(30, TimeUnit.MINUTES);
        }

    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principal) {
        User user = (User) principal.getPrimaryPrincipal();
        String userStr = redisTemplate.boundValueOps(RedisConstant.USER_INFO + user.getId()).get();
        user = JSON.parseObject(userStr, User.class);
        Assert.notNull(user, "token或用户数据不存在，请重新登录");
        //将用户的角色编码和资源标识放入认证信息
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setRoles(user.getRoleCode());
        info.setStringPermissions(user.getResourceCode());

        return info;
    }

}
