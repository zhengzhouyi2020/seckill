package com.zzy.seckill.controller;

import com.alibaba.fastjson.JSON;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.zzy.seckill.config.AccessLimit;
import com.zzy.seckill.entity.OrderInfo;
import com.zzy.seckill.entity.SeckillMessage;
import com.zzy.seckill.entity.SeckillOrder;
import com.zzy.seckill.entity.User;
import com.zzy.seckill.rabbitmq.SeckillSender;
import com.zzy.seckill.service.IGoodsService;
import com.zzy.seckill.service.ISeckillOrderService;
import com.zzy.seckill.vo.GoodsVo;
import com.zzy.seckill.vo.RespBean;
import com.zzy.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean{
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SeckillSender seckillSender;
    @Autowired
    private RedisScript<Long> script;

    // 商品id 是否停止与Redis互通（是否为空库存）
    private Map<Long,Boolean> EmptyStockMap = new HashMap<>();

    //系统初始化，把商品库存数量加载到REDIS
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodVo();
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        list.forEach((goodsVo ->{
            redisTemplate.opsForValue().set("seckillGoods:"+ goodsVo.getId(),goodsVo.getStockCount());
            EmptyStockMap.put(goodsVo.getId(),false);
        }
        ));
    }

    //获取秒杀接口
    @AccessLimit(second = 5,maxCount = 5,needLogin = true)
    @RequestMapping(value = "path",method = RequestMethod.GET)
    @ResponseBody
    public RespBean getPath(User user, Long goodsId, HttpServletRequest request){
        if(null == user){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        /*ValueOperations valueOperations = redisTemplate.opsForValue();
        //限制访问次数 5秒内访问5次
        String uri = request.getRequestURI();
        Integer count =(Integer) valueOperations.get(uri + ":" + user.getId());
        if(count == null){
            valueOperations.set(uri + ":" + user.getId(),1,5, TimeUnit.SECONDS);
        }else if(count<5){
            valueOperations.increment(uri + ":" + user.getId());
        }else{
            return RespBean.error(RespBeanEnum.ACCESS_lIMIT);
        }*/


        String str = seckillOrderService.createPath(user,goodsId);
        return RespBean.success(str);
    }

    //静态页面
    @RequestMapping(value ="/{path}/doSeckill",method = RequestMethod.POST)
    @ResponseBody
    private RespBean doSeckill(/*Model model*/ @PathVariable String path, User user, Long goodsId){
        if(null == user){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        // 方法一：没有预减库存
        /*GoodsVo goods = goodsService.findGoodVoByGoodsId(goodsId);

        //  判断库存
        if (goods.getStockCount()<1){
        model.addAttribute("errormsg", RespBeanEnum.EMPTY_STOCK.getMessage());
        return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //判断是否重复抢
        *//*SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>()
        .eq("user_id", user.getId())
        .eq("goods_id", goodsId));*//*
        // 默认秒杀时间小于 缓存存活时间
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue()
        .get("order:" + user.getId() + ":" + goodsId);

        if(seckillOrder!=null){
        model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
        return RespBean.error(RespBeanEnum.REPEAT_ERROR);
        }

        OrderInfo order = seckillOrderService.seckill(user,goods);

        return RespBean.success(order);*/

        //方法二：Redis预减库存
        ValueOperations valueOperations = redisTemplate.opsForValue();

        //隐藏接口地址，是否非法请求
        Boolean check = seckillOrderService.checkPath(user,goodsId,path);
        if(!check){
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }

        //判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue()
                .get("order:" + user.getId() + ":" + goodsId);
        if(seckillOrder!=null){
            return RespBean.error(RespBeanEnum.REPEAT_ERROR);
        }
        //通过内存标记 标记为空时，减少Redis的访问
        if(EmptyStockMap.get(goodsId)){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //预减库存
        //方法一：redis 原子性自加
        //Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
        //方法二：script
        Long stock= (Long) redisTemplate.execute(script,
                Collections.singletonList("seckillGoods:" + goodsId), Collections.EMPTY_LIST);

        if(stock<0){
            EmptyStockMap.put(goodsId,true);
            valueOperations.increment("seckillGoods:"+goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        SeckillMessage msg = new SeckillMessage(user,goodsId);
        seckillSender.send(JSON.toJSONString(msg));
        return RespBean.success(0);
    }

    //获取秒杀结果
    @RequestMapping(value = "result",method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user, Long goodsId){
        if(user==null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user,goodsId);
        return RespBean.success(orderId);

    }


    //非静态页面，需要每次渲染
    @RequestMapping("doSeckill2")
    private String doSeckill2(Model model, User user, Long goodsId){
        if(null == user){
            return "login";
        }
        GoodsVo goods = goodsService.findGoodVoByGoodsId(goodsId);
        Integer count = goods.getStockCount();
        //  判断库存
        if (count<1){
            model.addAttribute("errormsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "seckill_fail";
        }
        //判断是否重复抢
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>()
                .eq("user_id", user.getId())
                .eq("goods_id", goodsId));

        if(seckillOrder!=null){
            model.addAttribute("errmsg", RespBeanEnum.REPEAT_ERROR.getMessage());
            return "seckill_fail";
        }

        OrderInfo order = seckillOrderService.seckill(user,goods);
        model.addAttribute("orderInfo",order);
        model.addAttribute("goods",goods);
        return "order_detail";

    }



}
