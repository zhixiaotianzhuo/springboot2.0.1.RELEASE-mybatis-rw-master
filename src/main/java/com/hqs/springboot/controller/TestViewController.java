package com.hqs.springboot.controller;

import com.hqs.springboot.entity.Test;
import com.hqs.springboot.service.TestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Manu on 2018/7/10.
 */
@Controller
public class TestViewController {
    private static final Logger logger = LoggerFactory.getLogger(TestViewController.class);
    @Resource
    TestService testService;

    @RequestMapping("/tables/{id}")
    public String tablesFindTestById(@PathVariable Integer id, Model model){

        /*System.out.println(id);
        Test t1 = testService.findTestById(id);
        Test t2 = testService.findTestById(id+1);

        List<Test> ts = new LinkedList<>();
        ts.add(t1);
        ts.add(t2);

        model.addAttribute("ttt", t1);
        model.addAttribute("tttList", ts);*/

        return "tables";
    }
    @RequestMapping("/formsTest")
    public String formsTestById(){

        return "forms";
    }
}
