package com.zzy.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzy.seckill.entity.User;
import com.zzy.seckill.vo.LoginVo;
import com.zzy.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jy
 * @since 2022-01-23
 */
public interface IUserService extends IService<User> {

    //登录
    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    User getUserByCookie(String ticket, HttpServletRequest request,HttpServletResponse response);

    RespBean updatePassword(String userTicket,String password,HttpServletRequest request,HttpServletResponse response);
}
