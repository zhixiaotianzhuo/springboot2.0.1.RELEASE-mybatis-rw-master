package com.hqs.springboot.utils.filter;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by Manu on 2018/7/17.
 */
public class LoginFilter extends AuthorizationFilter {

    private static final Logger logger = LoggerFactory.getLogger(LoginFilter.class);


    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object o) throws Exception {
        HttpServletRequest req = (HttpServletRequest) request;
        String url = req.getRequestURI();
        logger.info(String.format("LoginFilter:isAccessAllowed:%s", url));
        if (url.startsWith("/cas") ||
                url.startsWith("/callback")||
                url.startsWith("/logout") ||
                url.startsWith("/exit") ||
                url.startsWith("/dist") ||
                url.startsWith("/tools") ||
                url.startsWith("/sys/enum") ||
                url.startsWith("/api") ||
                url.startsWith("/home")) {
            return true;
        }
        return isLogin(request, response);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        redirectToLogin(request, response);
        return false;
    }

    private boolean isLogin(ServletRequest request, ServletResponse response) {
        Subject subject = getSubject(request, response);

        if (subject == null || StringUtils.isEmpty((CharSequence) subject.getPrincipal())) {
            return false;
        }

        return true;
    }

    private boolean isAjax(HttpServletRequest request) {
        String header = request.getHeader("x-requested-with");
        if (null != header && "XMLHttpRequest".endsWith(header)) {
            return true;
        }
        return false;
    }
}
