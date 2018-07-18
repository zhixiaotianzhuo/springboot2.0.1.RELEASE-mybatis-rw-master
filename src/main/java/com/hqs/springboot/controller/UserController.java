package com.hqs.springboot.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Manu on 2018/7/19.
 */
@RestController
@RequestMapping(value = "/auth" )
public class UserController {



    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @RequestMapping(value = "/user/list")
//    @RequiresPermissions(ModuleNameCollection.AuthUserController_list)
    public String listUser(){
        try {

            return "哔哔哔哔哔 ！";
        }catch (Exception e){
            e.printStackTrace();
            return "服务器内部错误";
        }
    }
}
