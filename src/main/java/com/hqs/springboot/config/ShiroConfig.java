package com.hqs.springboot.config;

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

    @Bean(name = "shiroFilter")
    protected ShiroFilterFactoryBean getShiroFilterFactoryBean() {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager());
//        SecurityUtils.setSecurityManager(securityManager);
        //配置登录的url和登录成功的url
        shiroFilterFactoryBean.setLoginUrl(loginUrl);
        shiroFilterFactoryBean.setUnauthorizedUrl(unauthorizedUrl);
        shiroFilterFactoryBean.setSuccessUrl(loginSuccessfulUrl);

        //配置filter
        Map<String, Filter> filterMap = new HashMap<>();
        filterMap.put("casSecurityFilter", getCasSecurityFilter());
        filterMap.put("callbackFilter", getCallbackFilter());
        filterMap.put("loginFilter", new LoginFilter());
        filterMap.put("logoutFilter", getLogoutFilter());

        shiroFilterFactoryBean.setFilters(filterMap);
        loadShiroFilterChain(shiroFilterFactoryBean);

        return shiroFilterFactoryBean;
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

    @Bean(name = "sessionValidationScheduler")
    public ExecutorServiceSessionValidationScheduler getExecutorServiceSessionValidationScheduler() {
        ExecutorServiceSessionValidationScheduler scheduler = new ExecutorServiceSessionValidationScheduler();
        scheduler.setInterval(300000);

        return scheduler;
    }

    @Bean(name = "sessionManager")
    public DefaultWebSessionManager getSessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setGlobalSessionTimeout(1800000000);
        sessionManager.setSessionValidationScheduler(getExecutorServiceSessionValidationScheduler());
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setDeleteInvalidSessions(true);
        sessionManager.setSessionIdCookieEnabled(true);
        sessionManager.setSessionIdCookie(getSessionIdCookie());
        EnterpriseCacheSessionDAO cacheSessionDAO = new EnterpriseCacheSessionDAO();
        cacheSessionDAO.setCacheManager(getEhCacheManager());
//        sessionManager.setSessionDAO(getRedisSessionDao());
        // -----可以添加session 创建、删除的监听器

        return sessionManager;
    }

    /*@Bean(name = "redisSessionDao")
    public RedisSessionDao getRedisSessionDao(){
        return new RedisSessionDao(sessionTimeout);
    }*/

    @Bean("securityManager")
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager manager=new DefaultWebSecurityManager();
        manager.setCacheManager(getEhCacheManager());
        manager.setSessionManager(getSessionManager());
        manager.setRealm(pac4jRealm(getEhCacheManager()));
        manager.setSubjectFactory(pac4jSubjectFactory());

        return  manager;
    }

    @Bean(name = "lifecycleBeanPostProcessor")
    public static LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);

        return creator;
    }

    @Bean(name = "pac4jRealm")
    public Pac4jRealm pac4jRealm(EhCacheManager ehCacheManager) {
        Pac4jRealm shiroRealm = new RealmUtil();
        shiroRealm.setCacheManager(ehCacheManager);

        return shiroRealm;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(new DelegatingFilterProxy("shiroFilter"));
        //  该值缺省为false,表示生命周期由SpringApplicationContext管理,设置为true则表示由ServletContainer管理
        filterRegistration.addInitParameter("targetFilterLifecycle", "true");
        filterRegistration.setEnabled(true);
        filterRegistration.addUrlPatterns("/*");// 可以自己灵活的定义很多，避免一些根本不需要被Shiro处理的请求被包含进来

        return filterRegistration;
    }

    @Bean(name = "ehCacheManager")
    public EhCacheManager getEhCacheManager() {
        EhCacheManager ehCacheManager = new EhCacheManager();
        ehCacheManager.setCacheManagerConfigFile("classpath:ehcache-shiro.xml");

        return ehCacheManager;
    }


    @Bean(name = "sessionIdCookie")
    public SimpleCookie getSessionIdCookie() {
        SimpleCookie cookie = new SimpleCookie("sid");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(1800000);

        return cookie;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager());

        return advisor;
    }

}
