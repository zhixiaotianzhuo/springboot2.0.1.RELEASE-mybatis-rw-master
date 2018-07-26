package com.hqs.springboot.utils.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by MANU on 2018/6/26.
 */
@Component
public class InterceptorUtil implements HandlerInterceptor {
    // 添加 @Component 注解，对象的生命周期将交由 Spring 接管 - 对象级别的 autowired 方可使用

    /*
        实现HandlerInterceptor接口或者继承HandlerInterceptor的子类,比如比如Spring 已经提供的实现了HandlerInterceptor 接口的抽象类HandlerInterceptorAdapter ,
        下面讲实现其接口的写法,先看一下这个接口的三个方法.
        - 方法preHandle: 顾名思义，该方法将在请求处理之前进行调用。SpringMVC 中的Interceptor 是链式的调用的，在一个应用中或者说是在一个请求中可以同时存在多个Interceptor 。每个Interceptor 的调用会依据它的声明顺序依次执行，而且最先执行的都是Interceptor 中的preHandle 方法，所以可以在这个方法中进行一些前置初始化操作或者是对当前请求的一个预处理，也可以在这个方法中进行一些判断来决定请求是否要继续进行下去。该方法的返回值是布尔值Boolean 类型的，当它返回为false 时，表示请求结束，后续的Interceptor 和Controller 都不会再执行；当返回值为true 时就会继续调用下一个Interceptor 的preHandle 方法，如果已经是最后一个Interceptor 的时候就会是调用当前请求的Controller 方法。
        - 方法postHandle:由preHandle 方法的解释我们知道这个方法包括后面要说到的afterCompletion 方法都只能是在当前所属的Interceptor 的preHandle 方法的返回值为true 时才能被调用。postHandle 方法，顾名思义就是在当前请求进行处理之后，也就是Controller 方法调用之后执行，但是它会在DispatcherServlet 进行视图返回渲染之前被调用，所以我们可以在这个方法中对Controller 处理之后的ModelAndView 对象进行操作。postHandle 方法被调用的方向跟preHandle 是相反的，也就是说先声明的Interceptor 的postHandle 方法反而会后执行，这和Struts2 里面的Interceptor 的执行过程有点类型。Struts2 里面的Interceptor 的执行过程也是链式的，只是在Struts2 里面需要手动调用ActionInvocation 的invoke 方法来触发对下一个Interceptor 或者是Action 的调用，然后每一个Interceptor 中在invoke 方法调用之前的内容都是按照声明顺序执行的，而invoke 方法之后的内容就是反向的。
        - 方法afterCompletion:该方法也是需要当前对应的Interceptor 的preHandle 方法的返回值为true 时才会执行。顾名思义，该方法将在整个请求结束之后，也就是在DispatcherServlet 渲染了对应的视图之后执行。这个方法的主要作用是用于进行资源清理工作的。
    */

    /**
     * 该方法将在请求处理之前进行调用。
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        System.out.println("========preHandle=========");
        System.out.println(((HandlerMethod) handler).getBean().getClass().getName());
        System.out.println(((HandlerMethod) handler).getMethod().getName());

        request.setAttribute("startTime", System.currentTimeMillis());

        return true;
    }

    /**
     * Controller 方法调用之后执行，但是它会在DispatcherServlet 进行视图返回渲染之前被调用
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {

        System.out.println("========postHandle=========");
        Long start = (Long) request.getAttribute("startTime");
        System.out.println("耗时:" + (System.currentTimeMillis() - start));
    }

    /**
     * 在DispatcherServlet 渲染了对应的视图之后执行。这个方法的主要作用是用于进行资源清理工作的。
     *
     * @param request
     * @param response
     * @param handler
     * @param exception
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception)
            throws Exception {

        System.out.println("========afterCompletion=========");
        Long start = (Long) request.getAttribute("startTime");
        System.out.println("耗时:" + (System.currentTimeMillis() - start));

        System.out.println(exception);
    }

}

