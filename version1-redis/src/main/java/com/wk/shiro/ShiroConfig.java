package com.wk.shiro;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Configuration
public class ShiroConfig {

    //Redis配置
    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private String port;

    /**
     * 配置核心安全事务管理器
     * @return
     */
    @Bean
    public SecurityManager securityManager(){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 配置SecurityManager，并注入自定义的shiroRealm
        securityManager.setRealm(userRealm());
        //设置Redis缓存
        securityManager.setCacheManager(redisCacheManager());
        //设置session管理器
        securityManager.setSessionManager(sessionManager());
        return securityManager;
    }

    //手动创建ShiroRealm类，需要继承shiro提供的realm,提供用户的身份和权限信息
    @Bean
    public UserRealm userRealm(){
        // 配置Realm，需自己实现
        UserRealm userRealm = new UserRealm();
        //开启缓存
        userRealm.setCachingEnabled(true);
        //启用身份验证缓存，即缓存AuthenticationInfo信息，默认false
        userRealm.setAuthenticationCachingEnabled(true);
        //缓存AuthenticationInfo信息的缓存名称
        userRealm.setAuthenticationCacheName("authenticationCache");
        //启用授权缓存，即缓存AuthorizationInfo信息，默认false
        userRealm.setAuthorizationCachingEnabled(true);
        //缓存AuthorizationInfo信息的缓存名称
        userRealm.setAuthorizationCacheName("authorizationCache");
        //配置密码比较器
        userRealm.setCredentialsMatcher(credentialsMatcher());
        return userRealm;
    }

    //密码比较器
    @Bean
    public HashedCredentialsMatcher credentialsMatcher(){
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        //设置加密算法
        credentialsMatcher.setHashAlgorithmName("MD5");
        //加密次数
        credentialsMatcher.setHashIterations(2);
        return credentialsMatcher;
    }


    //会话管理器
    @Bean
    public SessionManager sessionManager(){
		//DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        ShiroSessionManager sessionManager = new ShiroSessionManager();
        List<SessionListener> listeners = new ArrayList<>();

        //配置监听
        listeners.add(sessionListener());
        sessionManager.setSessionListeners(listeners);
        sessionManager.setSessionDAO(redisSessionDAO());
        sessionManager.setCacheManager(redisCacheManager());
        sessionManager.setSessionIdCookie(cookie());
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        return sessionManager;
    }

    //设置cookie信息
    @Bean
    public SimpleCookie cookie(){
        SimpleCookie cookie = new SimpleCookie("SHARE_JSESSIONID"); //  cookie的name,对应的默认是 JSESSIONID
        cookie.setHttpOnly(true);
        cookie.setPath("/");        //  path为 / 用于多个系统共享JSESSIONID
        //-1表示浏览器关闭时此cookie失效
        cookie.setMaxAge(-1);
        return cookie;
    }

    /**
     * Redis管理器
     */
    @Bean
    public RedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(host+":"+port);
        return redisManager;
    }

    /**
     * SessionDAO的作用是为Session提供CRUD并进行持久化的一个shiro组件
     * MemorySessionDAO 直接在内存中进行会话维护
     * EnterpriseCacheSessionDAO  提供了缓存功能的会话维护，默认情况下使用MapCache实现，内部使用ConcurrentHashMap保存缓存的会话。
     */
    @Bean
    public RedisSessionDAO redisSessionDAO(){
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        redisSessionDAO.setSessionIdGenerator(sessionIdGenerator());
        //session在redis中的保存时间,最好大于session会话超时时间
        redisSessionDAO.setExpire(2000);
        return redisSessionDAO;
    }

    @Bean
    public JavaUuidSessionIdGenerator sessionIdGenerator(){
        return new JavaUuidSessionIdGenerator();
    }

    //配置Redis缓存 存储权限和角色标识
    public RedisCacheManager redisCacheManager(){
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        //设置角色信息和权限信息的缓存时间，单位：秒
        redisCacheManager.setExpire(3600);
        return redisCacheManager;
    }

    /**
     * shiro请求过滤器
     * Web应用中,Shiro可控制的Web请求必须经过Shiro主过滤器的拦截
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager){
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //必须设置 SecurityManager,Shiro的核心安全接口
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        // 配置访问权限 必须是LinkedHashMap，因为它保证有序
        // 过滤链定义，从上向下顺序执行，一般将 /**放在最为下边 一定要注意顺序,否则无法达到过滤效果
        LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // 定义filterChain，配置不登录可以访问的资源，anon 表示资源可以匿名访问,静态资源不拦截
        //访问项目首页不限制
        filterChainDefinitionMap.put("/", "anon");
        //访问登录方法不受限制
        filterChainDefinitionMap.put("/login", "anon");
        //访问注册方法不受限制
        filterChainDefinitionMap.put("/toRegister", "anon");
        //退出过滤器,具体的退出代码Shiro已经实现，登出后跳转配置的loginUrl
        filterChainDefinitionMap.put("/logout", "logout");
        // 除上以外所有url都必须认证通过才可以访问，未通过认证自动访问LoginUrl
        filterChainDefinitionMap.put("/**", "authc");
        // 配置shiro默认登录界面地址，前后端分离中登录界面跳转应由前端路由控制，后台仅返回json数据
        shiroFilterFactoryBean.setLoginUrl("/unLogin");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    /**
     * 开启Shiro-aop注解支持 使用代理方式所以需要开启代码支持
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    /**
     * 配置session监听
     */
    @Bean
    public SessionListener sessionListener(){
        return new ShiroSessionListener();
    }

}
