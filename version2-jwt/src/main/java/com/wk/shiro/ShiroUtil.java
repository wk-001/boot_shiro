package com.wk.shiro;

import com.wk.domain.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;

public class ShiroUtil {

    /**
     * 用户登出
     * @Author Sans
     * @CreateTime 2019/6/17 17:23
     */
    public static void logout() {
        SecurityUtils.getSubject().logout();
    }

    /**
     * 获取当前用户信息
     * @Author Sans
     * @CreateTime 2019/6/17 17:03
     * @Return User 用户信息
     */
    public static User getUserInfo() {
        return (User) SecurityUtils.getSubject().getPrincipal();
    }

    /**
     * 对用户输入的密码进行加密
     */
    public static User encryptPassword(User user) {
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        int times = 2;
        String encodedPassword = new SimpleHash("MD5", user.getPassword(), salt, times).toString();
        user.setPassword(encodedPassword);
        user.setSalt(salt);
        return user;
    }

}
