package com.zzy.seckill.controller;


import com.zzy.seckill.entity.User;
import com.zzy.seckill.rabbitmq.MQSender;
import com.zzy.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zzy
 * @since 2022-01-23
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private MQSender mqSender;
    //测试用户
    @RequestMapping("/info")
    @ResponseBody
    private RespBean info(User user){
        return RespBean.success(user);
    }

    @RequestMapping("mq/fanout")
    @ResponseBody
    public void mq(){
        mqSender.send("Hello");
    }
    @RequestMapping("mq/direct01")
    @ResponseBody
    public void mq01(){
        mqSender.send03("Hello red");
    }
    @RequestMapping("mq/direct02")
    @ResponseBody
    public void mq02(){
        mqSender.send04("Hello green");
    }

    @RequestMapping("mq/topic01")
    @ResponseBody
    public void mq03(){
        mqSender.send05("Hello red");
    }
    @RequestMapping("mq/topic02")
    @ResponseBody
    public void mq04(){
        mqSender.send06("Hello green");
    }

    @RequestMapping("mq/header01")
    @ResponseBody
    public void mq05(){
        mqSender.send07("Hello header01");
    }
    @RequestMapping("mq/header02")
    @ResponseBody
    public void mq06(){
        mqSender.send08("Hello header02");
    }
}
