package com.myblog.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component("loginFailureHandler")
public class Failurelhandler implements AuthenticationFailureHandler {


    //操作json的工具类
    private ObjectMapper json_util = new ObjectMapper();

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        //返回json字符串
/*        Map res = new HashMap();
        res.put("success",false);
        res.put("errorMsg",e.getMessage());
        String tojson = json_util.writeValueAsString(res);
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().write(tojson);*/


        //1-登陆失败，记录登陆失败次数，达到5次时限制登陆
        String address = request.getRemoteAddr();
        String failKey = "user:limitLogin:"+address;
        String lockLoginKey = "user:LockLogin:"+address;

        String result;

        //
        if (!redisTemplate.hasKey(failKey)){
            redisTemplate.opsForValue().set(failKey,1,5, TimeUnit.MINUTES);

            result = "登陆失败，在5分钟内还允许登陆："+(5-1)+"次";
            System.out.println("登陆失败，在5分钟内还允许登陆："+(5-1)+"次");

        }else {
            Integer count = (Integer) redisTemplate.opsForValue().get(failKey);

            if (count < 4){
                redisTemplate.opsForValue().increment(failKey,1);
                int count2 = (int)redisTemplate.opsForValue().get(failKey);

                System.out.println("登陆失败，在2分钟内还允许登陆："+(5-count2)+"次");
                result = "登陆失败，在2分钟内还允许登陆："+(5-count2)+"次";
            }else {
                //5此均登陆失败，限制登陆1小时
                redisTemplate.opsForValue().set(lockLoginKey,"xx",1,TimeUnit.HOURS);
                System.out.println("5次登陆失败，限制登陆1小时");
                result = "5次登陆失败，限制登陆1小时";
            }
        }


        response.setContentType("text/html; charset=utf-8");
        request.getRequestDispatcher("/toLogin?error="+result).forward(request,response);
    }
}
