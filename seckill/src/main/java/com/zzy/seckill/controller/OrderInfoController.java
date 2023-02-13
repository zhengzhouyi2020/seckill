package com.zzy.seckill.controller;


import com.zzy.seckill.entity.User;
import com.zzy.seckill.service.IOrderInfoService;
import com.zzy.seckill.vo.OrderDetailVo;
import com.zzy.seckill.vo.RespBean;
import com.zzy.seckill.vo.RespBeanEnum;
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
 * @since 2022-01-26
 */
@Controller
@RequestMapping("/order")
public class OrderInfoController {
    @Autowired
    private IOrderInfoService orderInfoService;

    @RequestMapping("/detail")
    @ResponseBody
    public RespBean detail(User user, Long orderId){
        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        OrderDetailVo orderDetailVo = orderInfoService.findDetailById(orderId);
        return RespBean.success(orderDetailVo);

    }

}
