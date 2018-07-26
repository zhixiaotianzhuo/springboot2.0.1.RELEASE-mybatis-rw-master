package com.hqs.springboot.config;

import com.hqs.springboot.cache.RedisSessionDao;
import com.hqs.springboot.utils.RealmUtil;
import com.hqs.springboot.utils.filter.LoginFilter;
import io.buji.pac4j.filter.CallbackFilter;
import io.buji.pac4j.filter.LogoutFilter;
import io.buji.pac4j.filter.SecurityFilter;
import io.buji.pac4j.realm.Pac4jRealm;
import io.buji.pac4j.subject.Pac4jSubjectFactory;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.session.mgt.ExecutorServiceSessionValidationScheduler;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.config.CasProtocol;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Manu on 2018/7/17.
 */
@Configuration
public class ShiroConfig {

    @Value("${shiro.shiro-server-url-prefix}")
    public String shiroServerUrlPrefix;

    @Value("${shiro.cas-login-url}")
    public String casLoginUrl;

    @Value("${shiro.login-successful-url}")
    public String loginSuccessfulUrl;

    @Value("${shiro.login-url}")
    public String loginUrl;

    @Value("${shiro.unauthorized-url}")
    public String unauthorizedUrl;

    @Value("${shiro.login-url}")
    public String logoutUrl;

    @Value("${shiro.session-timeout}")
    private Integer sessionTimeout;

    @Value("${shiro.session-validation-scheduler-interval}")
    private Integer sessionValidationSchedulerInterval;


    /**
     * 注册 shiroFilter 给到 WebConfig 使用
     * @return
     */
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        // DelegatingFilterProxy 会自动到 Spring 容器中查找名字为 shiroFilter 的 bean 并把 filter 请求交给它处理
        filterRegistration.setFilter(new DelegatingFilterProxy("shiroFilter"));
        //  该值缺省为false,表示生命周期由 SpringApplicationContext 管理,设置为 true 则表示由 ServletContainer 管理
        filterRegistration.addInitParameter("targetFilterLifecycle", "true");
        filterRegistration.setEnabled(true);
        filterRegistration.addUrlPatterns("");// 可以自己灵活的定义很多，避免一些根本不需要被Shiro处理的请求被包含进来

        return filterRegistration;
    }

    /**
     * SHIRO 登陆验证 配置
     * @return
     */
    @Bean(name = "shiroFilter")
    protected ShiroFilterFactoryBean getShiroFilterFactoryBean() {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager());
//        SecurityUtils.setSecurityManager(securityManager);
        // 配置 url
        shiroFilterFactoryBean.setLoginUrl(loginUrl);
        shiroFilterFactoryBean.setUnauthorizedUrl(unauthorizedUrl);
        shiroFilterFactoryBean.setSuccessUrl(loginSuccessfulUrl);

        //配置 filter
        Map<String, Filter> filterMap = new HashMap<>();
        filterMap.put("casSecurityFilter", getCasSecurityFilter());
        filterMap.put("callbackFilter", getCallbackFilter());
        filterMap.put("loginFilter", new LoginFilter());
        filterMap.put("logoutFilter", getLogoutFilter());

        shiroFilterFactoryBean.setFilters(filterMap);
        // 用于定义 FILTER和URL 之间的关系
        loadShiroFilterChain(shiroFilterFactoryBean);

        return shiroFilterFactoryBean;
    }


    /**
     * 会话管理器管理着应用中所有 Subject 的会话的创建、维护、删除、失效、验证等工作。 - 顶层组件
     * @return
     */
    @Bean("securityManager")
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager manager=new DefaultWebSecurityManager();
        // cacheManager：使用 cacheManager 获取相应的 cache 来缓存用户登录的会话；
        // 用于保存用户—会话之间的关系的；
        manager.setCacheManager(getEhCacheManager());
        // sessionManager：用于根据会话 ID，获取会话进行踢出操作的；
        manager.setSessionManager(getSessionManager());
        manager.setRealm(pac4jRealm(getEhCacheManager()));
        manager.setSubjectFactory(pac4jSubjectFactory());

        return  manager;
    }


    /**
     * SHIRO 使用 ehCacheManager 进行会话存储,管理如用户、角色、权限等的缓存
     * @return
     */
    @Bean(name = "ehCacheManager")
    public EhCacheManager getEhCacheManager() {
        EhCacheManager ehCacheManager = new EhCacheManager();
        ehCacheManager.setCacheManagerConfigFile("classpath:ehcache-shiro.xml");

        return ehCacheManager;
    }

    private void loadShiroFilterChain(ShiroFilterFactoryBean shiroFilterFactoryBean)
    {
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
//        filterChainDefinitionMap.put("/**", "anon");
        filterChainDefinitionMap.put("/unauthorized","anon");
        filterChainDefinitionMap.put("/cas/**","casSecurityFilter");
        filterChainDefinitionMap.put("/callback/**","callbackFilter");
        filterChainDefinitionMap.put("/logout","logoutFilter");
        filterChainDefinitionMap.put("/**", "loginFilter");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
    }

    @Bean
    public CasConfiguration casConfiguration() {
        CasConfiguration casConfiguration = new CasConfiguration();
        casConfiguration.setLoginUrl(casLoginUrl);
        casConfiguration.setProtocol(CasProtocol.CAS20);

        return casConfiguration;
    }

    @Bean
    public CasClient casClient() {
        CasClient casClient = new CasClient();
        casClient.setName("CasClient");
        casClient.setConfiguration(casConfiguration());

        return casClient;
    }

    @Bean
    public Clients clients() {
        return new Clients(shiroServerUrlPrefix + "/callback", casClient());
    }

    @Bean
    public Config config() {
        return new Config(clients());
    }

    @Bean
    public Pac4jSubjectFactory pac4jSubjectFactory() {
        return new Pac4jSubjectFactory();
    }

    public SecurityFilter getCasSecurityFilter() {
        SecurityFilter securityFilter = new SecurityFilter();
        securityFilter.setConfig(config());
        securityFilter.setClients("CasClient");

        return securityFilter;
    }

    public CallbackFilter getCallbackFilter() {
        CallbackFilter callbackFilter = new CallbackFilter();
        callbackFilter.setConfig(config());
        callbackFilter.setMultiProfile(true);
        callbackFilter.setDefaultUrl(loginSuccessfulUrl);

        return callbackFilter;
    }

    public LogoutFilter getLogoutFilter() {
        LogoutFilter logoutFilter = new LogoutFilter();
        logoutFilter.setDefaultUrl(logoutUrl);
        logoutFilter.setConfig(config());
        logoutFilter.setCentralLogout(true);

        return logoutFilter;
    }

    /**
     * sessionValidationScheduler：会话验证调度器，sessionManager 默认就是使用 ExecutorServiceSessionValidationScheduler，
     * 其使用JDK的ScheduledExecutorService 进行定期调度并验证会话是否过期；
     * sessionValidationScheduler.interval：设置调度时间间隔，单位毫秒，默认就是1小时 - 不操作登录信息将被擦除；
     * sessionValidationScheduler.sessionManager：设置会话验证调度器进行会话验证时的会话管理器；
     * @return
     */
    @Bean(name = "sessionValidationScheduler")
    public ExecutorServiceSessionValidationScheduler getExecutorServiceSessionValidationScheduler() {
        ExecutorServiceSessionValidationScheduler scheduler = new ExecutorServiceSessionValidationScheduler();
        scheduler.setInterval(sessionValidationSchedulerInterval);

        return scheduler;
    }

    /**
     * SHIRO 会话管理组件
     * 主要是 会话的生命周期管理 + 会话验证 + 会话缓存管理
     * @return
     */
    @Bean(name = "sessionManager")
    public DefaultWebSessionManager getSessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        /* sessionManager.globalSessionTimeout：设置全局会话超时时间，默认30分钟，即如果 30 分钟内没有访问会话将过期；*/
        /* 默认情况下 globalSessionTimeout 将应用给所有 Session。可以单独设置每个 Session 的 timeout 属性来为每个 Session 设置其超时时间 */
        sessionManager.setGlobalSessionTimeout(sessionTimeout);
        // 会话验证调度器 - 定期调度并验证会话是否过期
        sessionManager.setSessionValidationScheduler(getExecutorServiceSessionValidationScheduler());
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setDeleteInvalidSessions(true);
        sessionManager.setSessionIdCookieEnabled(true);
        sessionManager.setSessionIdCookie(getSessionIdCookie());
        /*EnterpriseCacheSessionDAO cacheSessionDAO = new EnterpriseCacheSessionDAO();
        cacheSessionDAO.setCacheManager(getEhCacheManager());*/
        sessionManager.setSessionDAO(getRedisSessionDao());
        // -----可以添加 session 创建、删除的监听器

        return sessionManager;
    }


    /**
     * 在 Servlet 容器中，默认使用 JSESSIONID Cookie 维护会话，且会话默认是跟容器绑定的
     * sessionIdCookie 是 sessionManager 创建会话 Cookie 的模板：
     * @return
     */
    @Bean(name = "sessionIdCookie")
    public SimpleCookie getSessionIdCookie() {
        SimpleCookie cookie = new SimpleCookie("sid");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(sessionTimeout);

        return cookie;
    }

    /**
     * Shiro 通过 SessionDAO 用于会话的 CRUD - 这里使用 redisSessionDao
     * @return
     */
    @Bean(name = "redisSessionDao")
    public RedisSessionDao getRedisSessionDao(){
        return new RedisSessionDao(sessionTimeout);
    }

    /**
     * Ream 是认证和授权的规则提供方
     * @param ehCacheManager
     * @return
     */
    @Bean(name = "pac4jRealm")
    public Pac4jRealm pac4jRealm(EhCacheManager ehCacheManager) {
        Pac4jRealm shiroRealm = new RealmUtil();
        // w为什么不是 sessionManager
        // TODO: 2018/7/26
        shiroRealm.setCacheManager(ehCacheManager);

        shiroRealm.setAuthenticationCachingEnabled(false);//禁止认证缓存
        shiroRealm.setAuthorizationCachingEnabled(true);
        return shiroRealm;
    }

    /**
     * LifecycleBeanPostProcessor 用于在实现了 Initializable 接口的 Shiro bean 初始化时调用
     Initializable 接口回调，在实现了 Destroyable 接口的 Shiro bean 销毁时调用 Destroyable 接
     口回调。
     ？保证 doGetAuthorizationInfo 顺利执行
     * @return
     */
    @Bean(name = "lifecycleBeanPostProcessor")
    public static LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    // 保证 doGetAuthorizationInfo 顺利执行
    // TODO: 2018/7/25  // 不知道干嘛滴 ！！！
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);

        return creator;
    }

    /**
     * 添加 Shiro Spring AOP 权限注解的支持
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager());

        return advisor;
    }

}
