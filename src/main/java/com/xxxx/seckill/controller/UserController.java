package com.xxxx.seckill.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.vo.RespBean;

import org.springframework.stereotype.Controller;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhoubin
 * @since 2025-02-24
 */
@Controller
@RequestMapping("/user")
public class UserController {

    // 获取用户信息(测试)
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user) {
        return RespBean.success(user);
    }

}
