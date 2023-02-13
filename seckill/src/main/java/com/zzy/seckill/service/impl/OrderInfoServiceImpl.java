package com.zzy.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzy.seckill.exception.GlobalException;
import com.zzy.seckill.mapper.OrderInfoMapper;
import com.zzy.seckill.entity.OrderInfo;
import com.zzy.seckill.service.IGoodsService;
import com.zzy.seckill.service.IOrderInfoService;
import com.zzy.seckill.vo.GoodsVo;
import com.zzy.seckill.vo.OrderDetailVo;
import com.zzy.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jy
 * @since 2022-01-26
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements IOrderInfoService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;
    @Autowired
    private IGoodsService goodsService;


    @Override
    public OrderDetailVo findDetailById(Long orderId) {
        if(orderId==null){
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        OrderInfo order = orderInfoMapper.selectById(orderId);
        System.out.println(order);
        GoodsVo goodVo = goodsService.findGoodVoByGoodsId(order.getGoodsId());
        System.out.println(goodVo);
        OrderDetailVo detailVo = new OrderDetailVo();
        detailVo.setGoodsVo(goodVo);
        detailVo.setOrder(order);
        return detailVo;
    }
}
