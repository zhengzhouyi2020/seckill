package com.zzy.seckill.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzy.seckill.exception.GlobalException;
import com.zzy.seckill.mapper.UserMapper;
import com.zzy.seckill.entity.User;
import com.zzy.seckill.service.IUserService;
import com.zzy.seckill.utils.CookieUtil;
import com.zzy.seckill.utils.MD5Util;
import com.zzy.seckill.utils.UUIDUtil;
import com.zzy.seckill.vo.LoginVo;
import com.zzy.seckill.vo.RespBean;
import com.zzy.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zzy
 * @since 2022-01-23
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired(required=false)
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        /*if(StringUtils.isEmpty(mobile)||StringUtils.isEmpty(password)){
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        if(!ValidatorUtil.isMobile(mobile)){
            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
        }**/
        User user = userMapper.selectById(Long.valueOf(mobile));
        if(user==null){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
            //return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }
        if(!user.getPassword().equals(MD5Util.fromPassToDBPass(password,user.getSalt()))){
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
            //return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }

        // 生成Cookie
        String ticket = UUIDUtil.uuid();

        // request.getSession().setAttribute(ticket,user);  //方法一 Spring session
        redisTemplate.opsForValue().set("user:"+ticket,user);

        CookieUtil.setCookie(request,response,"userTicket",ticket);
        return RespBean.success(ticket);
    }

    @Override
    public User getUserByCookie(String ticket, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isEmpty(ticket)){
            return null;
        }
        User user = (User)redisTemplate.opsForValue().get("user:"+ticket);

        if(user!=null){
            CookieUtil.setCookie(request,response,"userTicket",ticket);
        }
        return user;
    }

    @Override
    public RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response) {
        User user = getUserByCookie(userTicket, request, response);
        if(null==user){
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        }
        user.setPassword(MD5Util.inputPassToDBPass(password,user.getSalt()));
        int result = userMapper.updateById(user);
        if(result==1){
            redisTemplate.delete("user:"+userTicket);
            return RespBean.success();
        }
        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
    }


}
