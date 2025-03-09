package com.xxxx.seckill.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.rabbitmq.MQSender;
import com.xxxx.seckill.vo.RespBean;

import org.springframework.beans.factory.annotation.Autowired;
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


    @Autowired
    private MQSender mqSender;

    // // 获取用户信息(测试)
    // @RequestMapping("/info")
    // @ResponseBody
    // public RespBean info(User user) {
    //     return RespBean.success(user);
    // }

    // //测试发送消息
    // @RequestMapping("/mq")
    // @ResponseBody
    // public void mq() {
    //     mqSender.send("hello");
    // }       

    // // 测试发送fanout消息
    // @RequestMapping("/mq/fanout")
    // @ResponseBody
    // public void mq01() {
    //     mqSender.send("hello");
    // }

    // // 测试发送direct消息
    // @RequestMapping("/mq/direct01")
    // @ResponseBody
    // public void mq02() {
    //     mqSender.send01("hello red");
    // }

    // @RequestMapping("/mq/direct02")
    // @ResponseBody
    // public void mq03() {
    //     mqSender.send02("hello green");
    // }

    // // 测试发送topic消息
    // @RequestMapping("/mq/topic01")
    // @ResponseBody
    // public void mq04() {
    //     mqSender.send03("hello red");
    // }

    // // 测试发送topic消息
    // @RequestMapping("/mq/topic02")
    // @ResponseBody
    // public void mq05() {
    //     mqSender.send04("hello green");
    // }

    // // 测试发送headers消息
    // @RequestMapping("/mq/headers01")
    // @ResponseBody
    // public void mq06() {
    //     mqSender.send05("hello header01");
    // }

    // // 测试发送headers消息
    // @RequestMapping("/mq/headers02")
    // @ResponseBody
    // public void mq07() {
    //     mqSender.send06("hello header02");
    // }

}
