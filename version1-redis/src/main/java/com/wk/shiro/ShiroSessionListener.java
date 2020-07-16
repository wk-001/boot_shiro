package com.wk.shiro;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;

/**
 * 配置shiroSession监听器
 */
@Slf4j
public class ShiroSessionListener implements SessionListener {


    /**
     * 会话创建时触发
     */
    @Override
    public void onStart(Session session) {
        log.info("会话创建");
    }

    /**
     * 退出会话时触发
     */
    @Override
    public void onStop(Session session) {
        log.info("会话退出");
    }

    /**
     * 会话过期时触发
     */
    @Override
    public void onExpiration(Session session) {
        log.info("会话过期");
    }

}
