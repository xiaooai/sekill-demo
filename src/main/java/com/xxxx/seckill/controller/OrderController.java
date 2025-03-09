package com.xxxx.seckill.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.service.IOrderService;
import com.xxxx.seckill.vo.OrderDetailVo;
import com.xxxx.seckill.vo.RespBean;
import com.xxxx.seckill.vo.RespBeanEnum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhoubin
 * @since 2025-02-26
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @RequestMapping("/detail")
    @ResponseBody
    public RespBean detail(User user, Long orderId){
        System.out.println("接收到订单查询请求，订单ID: " + orderId); // 调试信息
        if(user == null){
            System.out.println("用户未登录"); // 调试信息
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        if(orderId == null){
            return RespBean.error(RespBeanEnum.ORDER_NOT_EXIST);
        }
        try {
            OrderDetailVo detail = orderService.detail(orderId);
            if(detail == null || detail.getOrder() == null || detail.getGoodsVo() == null){
                return RespBean.error(RespBeanEnum.ORDER_NOT_EXIST);
            }
            System.out.println("查询到订单详情: " + detail); // 调试信息
            return RespBean.success(detail);
        } catch (Exception e) {
            e.printStackTrace(); // 打印完整堆栈信息
            System.err.println("查询订单详情出错: " + e.getMessage()); // 调试错误信息
            return RespBean.error(RespBeanEnum.ERROR);
        }
    }

}
