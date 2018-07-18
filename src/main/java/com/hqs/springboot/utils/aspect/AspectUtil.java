package com.hqs.springboot.utils.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Created by MANU on 2018/6/26.
 */
@Aspect
@Component
public class AspectUtil {
    // 添加 @Component 注解，对象的生命周期将交由 Spring 接管 - 对象级别的 autowired 方可使用
    // 添加 @Aspect 注解，标记为切面

    @Around("execution(* com.one.zero.manu.controller.FastjsonController..*(..))")
    public Object method(ProceedingJoinPoint pjp) throws Throwable {

        System.out.println("=====Aspect处理=======");
        Object[] args = pjp.getArgs();
        for (Object arg : args) {
            System.out.println("参数为:" + arg);
        }

        long start = System.currentTimeMillis();

        Object object = pjp.proceed();

        System.out.println("Aspect 耗时:" + (System.currentTimeMillis() - start));

        return object;
    }
}