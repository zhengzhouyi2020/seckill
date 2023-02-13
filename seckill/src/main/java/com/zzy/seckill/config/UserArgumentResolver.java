package com.zzy.seckill.config;

import com.zzy.seckill.entity.User;
import com.zzy.seckill.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private IUserService userService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {  // 判断是否是需要的参数
        Class<?> clazz = parameter.getParameterType(); //判断类型
        return clazz == User.class;  //true则进入下面的方法
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
       /* //获取HttpServletRequest
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        //获取HttpServletResponse
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        //直接从Cookie中获取
        String ticket = CookieUtil.getCookieValue(request,"userTicket");
        if(StringUtils.isEmpty(ticket)){ //判断是否为空
            return null;
        }
        return userService.getUserByCookie(ticket, request, response);*/
        return UserContext.getUser();

    }
}
