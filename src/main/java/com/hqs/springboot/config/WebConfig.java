package com.hqs.springboot.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.hqs.springboot.utils.ServletUtil;
import com.hqs.springboot.utils.filter.FilterUtil;
import com.hqs.springboot.utils.interceptor.InterceptorUtil;
import com.hqs.springboot.utils.interceptor.LogInterceptor;
import com.hqs.springboot.utils.listener.ListenerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Manu on 2018/7/8.
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    /**
     * @return
     */
    // todo 不知它用来干啥的 ? 保证返回值是格式化的 FastJson ？
    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();

        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);

        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);

        HttpMessageConverter<?> converter = fastJsonHttpMessageConverter;

        return new HttpMessageConverters(converter);
    }


    /**
     * 项目启动时，配置 Servlet
     *
     * @return
     */
    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        return new ServletRegistrationBean(new ServletUtil(), "/servletTest");
    }

    /**
     * 项目启动时，配置过滤器
     *
     * @return
     */
//    @Bean
//    public FilterRegistrationBean timeFilter() {
//        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//
//        FilterUtil timeFilter = new FilterUtil();
//        registrationBean.setFilter(timeFilter);
//
//        List<String> urls = new ArrayList<>();
//        urls.add("/*");
//        registrationBean.setUrlPatterns(urls);
//
//        return registrationBean;
//    }

    /**
     * 项目启动时，配置监听器
     *
     * @return
     */
    @Bean
    public ServletListenerRegistrationBean<ListenerUtil> servletListenerRegistrationBean() {
        return new ServletListenerRegistrationBean<ListenerUtil>(new ListenerUtil());
    }


    /**
     * 项目启动时，配置拦截器
     */
    @Autowired
    private InterceptorUtil interceptorUtil;
    @Autowired
    private LogInterceptor logInterceptor;
    /**
     * 如有多个拦截器，按照 add 顺序执行
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // registry.addInterceptor(interceptorUtil);
        registry.addInterceptor(logInterceptor).addPathPatterns("/**");
    }


    // controller 路径 ？
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/xx").setViewName("/xx");
    }

    // static 附件
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/static/**").addResourceLocations("/resources/");
    }

}
