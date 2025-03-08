package com.xxxx.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {

    // 通用
    SUCCESS(200, "SUCCESS"),
    ERROR(500, "服务端异常"),

    // 登录模块
    LOGIN_ERROR(500210, "用户名或密码不正确"),
    MOBILE_ERROR(500211, "手机号码格式不正确"),
    BIND_ERROR(500212, "参数校验异常"), 
    MOBILE_NOT_EXIST(500213, "手机号码不存在"),
    UPDATE_PASSWORD_ERROR(500214, "更新密码失败"),
    SESSION_ERROR(500215, "用户不存在"),

    // 秒杀模块
    EMPTY_STOCK(500500, "库存不足"),
    REPEAT_ERROR(500501, "该商品每人限购一件"),
    SEC_KILL_OVER(500502, "秒杀结束"),
    SEC_KILL_SUCCESS(500503, "秒杀成功"),
    SEC_KILL_FAIL(500504, "秒杀失败"),

    // 订单模块 5003xx
    ORDER_NOT_EXIST(500300, "订单不存在"),
    GOODS_NOT_EXIST(500501, "商品不存在");

    private final Integer code;
    private final String message;
}
