package com.hqs.springboot.utils.interceptor;

import com.hqs.springboot.constants.CallContext;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Manu on 2018/7/18.
 */
@Component
// 添加 @Component 注解，对象的生命周期将交由 Spring 接管 - 对象级别的 autowired 方可使用
public class LogInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        CallContext.setBeginTime();
        System.out.println("LogInterceptor preHandle");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        System.out.println("LogInterceptor postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        CallContext.setEndTime();
        System.out.println("耗时："+CallContext.getCostTimeMillis()+"毫秒");
        System.out.println("LogInterceptor afterCompletion");
    }
}
