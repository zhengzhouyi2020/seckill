package com.zzy.seckill.rabbitmq;

import com.zzy.seckill.entity.SeckillMessage;
import com.zzy.seckill.entity.SeckillOrder;
import com.zzy.seckill.entity.User;
import com.zzy.seckill.service.IGoodsService;
import com.zzy.seckill.service.ISeckillOrderService;
import com.zzy.seckill.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSON;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SeckillRecevier {
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    
    @RabbitListener(queues = "seckillQueue")
    public void recevie(String msg){
        log.info("接受" + msg);
        SeckillMessage seckillMessage = JSON.parseObject(msg, SeckillMessage.class);
        User user = seckillMessage.getUser();
        Long goodsId = seckillMessage.getGoodsId();
        GoodsVo goodVo = goodsService.findGoodVoByGoodsId(goodsId);
        //是否商品库存少于1
        if(goodVo.getStockCount()<1){
            return;
        }
        //判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue()
                .get("order:" + user.getId() + ":" + goodsId);
        if(seckillOrder!=null){
            return ;
        }
        //执行秒杀
        seckillOrderService.seckill(user,goodVo);



    }
}
