package com.zzy.seckill.vo;

import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {
    // 通用
    SUCCESS(200,"SUCCESS"),
    ERROR(500,"服务端异常"),

    //登录模块
    LOGIN_ERROR(500210,"用户名或密码不正确"),
    MOBILE_ERROR(500211,"用户名不正确"),
    BIND_ERROR(500212,"参数校验异常"),
    MOBILE_NOT_EXIST(500213,"手机号码不存在"),
    PASSWORD_UPDATE_FAIL(500214,"密码更新失败"),
    SESSION_ERROR(500215,"用户不存在"),
    //秒杀
    EMPTY_STOCK(500500,"库存不足"),
    REPEAT_ERROR(500501,"该商品每人限购一件"),
    REQUEST_ILLEGAL(500502,"请求非法，请重新尝试"),
    ACCESS_lIMIT(500503,"访问过于频繁，请稍后再试"),
    //订单
    ORDER_NOT_EXIST(500300,"订单不存在");






    private final Integer code;
    private final String message;


}
