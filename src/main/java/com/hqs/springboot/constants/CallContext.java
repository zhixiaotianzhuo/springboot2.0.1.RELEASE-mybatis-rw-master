package com.hqs.springboot.constants;

import com.hqs.springboot.controller.TestController;
import com.hqs.springboot.entity.SysUser;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Manu on 2018/7/18.
 */
public class CallContext {

    private static final Logger logger = LoggerFactory.getLogger(CallContext.class);
    public static final String CUR_USER = "cur_user";

    private static InheritableThreadLocal<Long> BEGIN_TIME = new InheritableThreadLocal<>();

    private static InheritableThreadLocal<Long> END_TIME = new InheritableThreadLocal<>();


    public static SysUser getCurUser() {
        SysUser sysUser = new SysUser();
        try {
            if(SecurityUtils.getSubject().getSession().getAttribute(CUR_USER) != null) {
                sysUser = (SysUser) SecurityUtils.getSubject().getSession().getAttribute(CUR_USER);
            }
        } catch (Exception e) {
            logger.warn(String.format("CallContext getCurrentUserId failed:%s", e.getMessage()));
        } finally {
            return sysUser;
        }
    }

    public static void setBeginTime(){
        BEGIN_TIME.set(System.nanoTime());
    }

    public static void setEndTime(){
        END_TIME.set(System.nanoTime());
    }

    public static long getCostTimeMillis(){
        return (END_TIME.get() - BEGIN_TIME.get()) / 1000000;
    }

}
