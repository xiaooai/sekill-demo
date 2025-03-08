package com.xxxx.seckill.controller;

import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller // 告诉SpringBoot，这个类是专门处理用户请求的类

@RequestMapping("/demo") // 设置统一访问前缀，这个类的所有请求都以/demo开头
public class DemoController { //具体的类，命名功能模块+Controller

    // 测试页面调整
    @RequestMapping("/hello") // 设置具体的请求路径，当用户访问demo/hello时，会调用这个方法
    public String hello(Model model){ // 方法名随意，但参数必须有Model
        model.addAttribute("name", "xxxx"); // 创建了一个name属性，在hello.html中可以调用
        return "hello"; // 类似与当导航，自动找到hello.html页面。目的也是前后端分离
    }
}
