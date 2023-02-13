package com.zzy.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzy.seckill.mapper.OrderInfoMapper;
import com.zzy.seckill.mapper.SeckillOrderMapper;
import com.zzy.seckill.entity.OrderInfo;
import com.zzy.seckill.entity.SeckillGoods;
import com.zzy.seckill.entity.SeckillOrder;
import com.zzy.seckill.entity.User;
import com.zzy.seckill.service.ISeckillGoodsService;
import com.zzy.seckill.service.ISeckillOrderService;
import com.zzy.seckill.utils.MD5Util;
import com.zzy.seckill.utils.UUIDUtil;
import com.zzy.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jy
 * @since 2022-01-26
 */
@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements ISeckillOrderService {
    @Autowired
    private ISeckillGoodsService seckillGoodsService;
    @Autowired
    private OrderInfoMapper orderInfoMapper;
    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Transactional
    @Override
    public OrderInfo seckill(User user, GoodsVo goods) {
        ValueOperations valueOperations = redisTemplate.opsForValue();

        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>()
                .eq("goods_id", goods.getId()));
        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);

        boolean updateResult = seckillGoodsService.update(seckillGoods, new UpdateWrapper<SeckillGoods>()
                .set("stock_count", seckillGoods.getStockCount())
                .eq("id", seckillGoods.getId()).gt("stock_count", 0));

        if(!updateResult){
            //设置库存为0
            valueOperations.set("isStockEmpty:"+ goods.getId(),"0");
            return null;
        }

        OrderInfo order = new OrderInfo();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(goods.getGoodsPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        order.setPayDate(new Date());
        orderInfoMapper.insert(order);

        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrderMapper.insert(seckillOrder);

        redisTemplate.opsForValue().set("order:"+user.getId()+":"+goods.getId(),seckillOrder);

        return order;
    }

    /**
     *
     * @param user
     * @param goodsId
     * @return: oderId: 成功， -1，秒杀失败, 0:排队中
     */
    @Override
    public Long getResult(User user, Long goodsId) {
        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(new QueryWrapper<SeckillOrder>()
                .eq("user_id", user.getId()).eq("goods_Id", goodsId));
        if(seckillOrder!=null){
            return seckillOrder.getOrderId();
        }else if(redisTemplate.hasKey("isStockEmpty:"+goodsId)){
            return -1L;
        }else {
            return 0L;
        }
    }

    /**
     * 创建请求地址
     * @param user
     * @param goodsId
     * @return String
     */
    @Override
    public String createPath(User user, Long goodsId) {
        String s = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisTemplate.opsForValue().set("seckillPath:"+user.getId()+":"
                +goodsId,s,60, TimeUnit.SECONDS);
        return s;
    }

    /**
     * 检查请求地址
     * @param user
     * @param goodsId
     * @param path
     * @return Boolean
     */
    @Override
    public Boolean checkPath(User user, Long goodsId, String path) {
        if(user ==null||goodsId<0|| StringUtils.isEmpty(path)) return false;
        String redisPath = (String)redisTemplate.opsForValue().get("seckillPath:" + user.getId() + ":" + goodsId);

        return path.equals(redisPath);
    }
}
