package com.wk.shiro;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Configuration
public class ShiroConfig {

    private final String CACHE_KEY = "shiro:cache:";

    private final String SESSION_KEY = "shiro:session:";

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

    //配置会话管理器，设定会话超时及保存
    @Bean
    public SessionManager sessionManager(){
		DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        //ShiroSessionManager sessionManager = new ShiroSessionManager();
        List<SessionListener> listeners = new ArrayList<>();

        //配置监听
        sessionManager.setSessionListeners(listeners);
        sessionManager.setSessionIdCookie(sessionIdCookie());
        sessionManager.setSessionDAO(sessionDAO());
        sessionManager.setCacheManager(redisCacheManager());

        //全局会话超时时间(单位：毫秒)，默认30分钟，暂时设置10秒钟用来测试
		sessionManager.setGlobalSessionTimeout(10000);
        //删除无效session对象 默认为true
		sessionManager.setDeleteInvalidSessions(true);
        //开启定时调度器进行检测过期session 默认为true
		sessionManager.setSessionValidationSchedulerEnabled(true);
        //设置session失效的扫描时间(单位：毫秒), 清理用户直接关闭浏览器造成的孤立会话 默认为 1个小时 暂时设置10秒钟用来测试
		sessionManager.setSessionValidationInterval(10000);
        //取消url 后面的 JSESSIONID
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        return sessionManager;
    }

    /**
     * 配置保存sessionId的cookie
     * 注意：这里的cookie 不是上面的记住我 cookie 记住我需要一个cookie session管理 也需要自己的cookie
     */
    @Bean
    public SimpleCookie sessionIdCookie(){
        //设置cookie的名称
        SimpleCookie simpleCookie = new SimpleCookie("sid");
		/*setcookie的httponly属性设置为true，会增加对xss防护的安全系数
		只能通过http访问，javascript无法访问
		防止xss读取cookie*/
        simpleCookie.setHttpOnly(true);
        //-1表示浏览器关闭时此cookie失效
        simpleCookie.setMaxAge(-1);
        return simpleCookie;
    }


    /**
     * SessionDAO的作用是为Session提供CRUD并进行持久化的一个shiro组件
     * MemorySessionDAO 直接在内存中进行会话维护
     * EnterpriseCacheSessionDAO  提供了缓存功能的会话维护，默认情况下使用MapCache实现，内部使用ConcurrentHashMap保存缓存的会话。
     */
    @Bean
    public SessionDAO sessionDAO(){
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(new RedisManager());
        //session在redis中的保存时间,最好大于session会话超时时间
        redisSessionDAO.setExpire(12000);
        //sessionId生成器
        redisSessionDAO.setSessionIdGenerator(sessionIdGenerator());
        redisSessionDAO.setKeyPrefix(SESSION_KEY);
        return redisSessionDAO;
    }

    /**
     * 配置会话ID生成器
     */
    @Bean
    public SessionIdGenerator sessionIdGenerator() {
        return new JavaUuidSessionIdGenerator();
    }

    //配置Redis缓存 存储权限和角色标识
    public RedisCacheManager redisCacheManager(){
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(new RedisManager());
        redisCacheManager.setKeyPrefix(CACHE_KEY);
        //redis中针对不同用户缓存,user对象的userName属性
        redisCacheManager.setPrincipalIdFieldName("userName");
        //用户权限信息缓存时间
        redisCacheManager.setExpire(200000);
        return redisCacheManager;
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
        // 除上以外所有url都必须认证通过才可以访问，未通过认证自动访问LoginUrl
		filterChainDefinitionMap.put("/**", "authc");

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

    //密码比较器
    @Bean
    public HashedCredentialsMatcher credentialsMatcher(){
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        //设置加密算法
        credentialsMatcher.setHashAlgorithmName("MD5");
        //加密次数
        credentialsMatcher.setHashIterations(2);
        //存储为16进制
        credentialsMatcher.setStoredCredentialsHexEncoded(true);
        return credentialsMatcher;
    }

}
