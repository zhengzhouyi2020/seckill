package com.zzy.seckill.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.zzy.seckill.entity.User;
import com.zzy.seckill.service.IGoodsService;
import com.zzy.seckill.service.IUserService;
import com.zzy.seckill.vo.GoodsDetailVo;
import com.zzy.seckill.vo.GoodsVo;
import com.zzy.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    @RequestMapping(value = "toList",produces = "text/html;charset=utf-8")
    @ResponseBody
    public  String toList( Model model, /*@CookieValue("userTicket")String ticket*/
                            User user,HttpServletRequest request, HttpServletResponse response){
        /*if(StringUtils.isEmpty(ticket)){
            return "login";
        }
        //方法一：Spring session
        //User user = (User)session.getAttribute(ticket);
        //方法二：直接从Redis获取user
        User user = userService.getUserByCookie(ticket, request, response);
        if(null == user){
            return "login";
        }*/
        //Redis中获取页面，如果不为空，直接返回页面
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String)valueOperations.get("goods_list");
        if(!StringUtils.isEmpty(html)){
            return html;
        }

        model.addAttribute("user",user);
        model.addAttribute("goodsList",goodsService.findGoodVo());

        //如果为空，手动渲染
        WebContext context = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", context);
        if(!StringUtils.isEmpty(html)){
            valueOperations.set("goods_list",html,60, TimeUnit.SECONDS);
        }
        //return "goods_list";
        return html;
    }
    @RequestMapping("toDetail2/{goodsId}")
    @ResponseBody
    private String toDetail2(Model model, User user,@PathVariable Long goodsId,HttpServletRequest request, HttpServletResponse response){

        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String)valueOperations.get("goods_detail:"+goodsId);
        if(!StringUtils.isEmpty(html)){
            return html;
        }

        GoodsVo goods = goodsService.findGoodVoByGoodsId(goodsId);
        Date startDate = goods.getStartDate();
        Date endDate = goods.getEndDate();
        Date nowdate = new Date();

        int seckillStatus = 0;
        int remainSeconds = 0;
        if(nowdate.before(startDate)){
            remainSeconds = (int)((startDate.getTime() - nowdate.getTime())/1000);
            seckillStatus = 0;
        }else if(nowdate.after(endDate)){
            remainSeconds = -1;
            seckillStatus = 2;
        }else {
            remainSeconds = 0;
            seckillStatus = 1;
        }
        model.addAttribute("remainSeconds",remainSeconds);
        model.addAttribute("seckillStatus",seckillStatus);
        model.addAttribute("user",user);
        model.addAttribute("goods",goods);

        //如果为空，手动渲染
        WebContext context = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", context);
        if(!StringUtils.isEmpty(html)){
            valueOperations.set("goods_detail",html,60, TimeUnit.SECONDS);
        }

        //return "goods_detail";
        return html;
    }

    @RequestMapping(value = "detail/{goodsId}")
    @ResponseBody
    public RespBean toDetail(HttpServletRequest request, HttpServletResponse response, Model model, User user, @PathVariable("goodsId") long goodsId) {

        //根据id查询商品详情
        GoodsVo goods = goodsService.findGoodVoByGoodsId(goodsId);
        model.addAttribute("goods", goods);

        Date startDate = goods.getStartDate();
        Date endDate = goods.getEndDate();
        Date nowdate = new Date();

        int seckillStatus = 0;
        int remainSeconds = 0;

        if(nowdate.before(startDate)){
            remainSeconds = (int)((startDate.getTime() - nowdate.getTime())/1000);
            seckillStatus = 0;
        }else if(nowdate.after(endDate)){
            remainSeconds = -1;
            seckillStatus = 2;
        }else {
            remainSeconds = 0;
            seckillStatus = 1;
        }
        GoodsDetailVo vo = new GoodsDetailVo();
        vo.setGoods(goods);
        vo.setUser(user);
        vo.setRemainSeconds(remainSeconds);
        vo.setSeckillStatus(seckillStatus);

        return RespBean.success(vo);
    }
}
