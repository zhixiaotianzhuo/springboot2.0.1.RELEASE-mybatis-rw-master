package com.hqs.springboot.controller;

import com.hqs.springboot.constants.CallContext;
import com.hqs.springboot.constants.ModuleNameCollection;
import com.hqs.springboot.entity.SysUser;
import com.hqs.springboot.entity.Test;
import com.hqs.springboot.service.TestService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Manu on 2018/7/10.
 */
@Controller
@RequestMapping(produces = "application/json;charset=UTF-8")
public class TestViewController {
    private static final Logger logger = LoggerFactory.getLogger(TestViewController.class);
    @Resource
    TestService testService;

    // http://localhost:8090/tables/10
    @RequestMapping("/tables/{id}")
    @RequiresPermissions(ModuleNameCollection.DepController_update)
    public String tablesFindTestById(@PathVariable Integer id, Model model){

        System.out.println(id);


        SysUser sysUser = CallContext.getCurUser();
        logger.info(sysUser.toString());

        List<String> headers = new LinkedList<>();
        headers.add("ID");
        headers.add("名字");
        headers.add("备注");
        // headers.add("小记");

        Test t1 = testService.findTestById(id);
        Test t2 = testService.findTestById(id+1);

        List<Test> ts = new LinkedList<>();
        ts.add(t1);
        ts.add(t2);

        model.addAttribute("headers", headers);
        model.addAttribute("ts", ts);

        return "tables";
    }

    @RequestMapping("/formsTest")
    @RequiresPermissions(ModuleNameCollection.AuthUserController_list)
    public String formsTestById(){

        return "forms";
    }

    @RequestMapping("/index")
    public String indexPage(){
        ServletRequest request;

        return "index";
    }
}
