package com.hqs.springboot.utils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by MANU on 2018/6/26.
 */
public class ServletUtil extends HttpServlet {

    /*
        调用的service(request, response) 方法功能是判断用户发出是什么请求
        如果是get则调用子类（ServletUtil）的doGet方法，如果是post则调用子类（ServletUtil）的doPost方法。
    */

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("处理 GET 请求...");
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("处理 POST 请求...");
        resp.setContentType("text/html;charset=utf-8");
        resp.getWriter().write("自定义 Servlet");
    }

}
