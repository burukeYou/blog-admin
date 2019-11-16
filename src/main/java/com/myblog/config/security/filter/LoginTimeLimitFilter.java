package com.myblog.config.security.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component("LoginTimeLimitFilter")
public class LoginTimeLimitFilter   extends OncePerRequestFilter {

   @Autowired
   private RedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        //判断是否是登陆
        if (request.getRequestURI().contains("/login")){

            String address = request.getRemoteAddr();
            String lockLoginKey = "user:LockLogin:"+address;

            //1-判断用户是和否被限制登陆
            if (redisTemplate.hasKey("user:LockLogin:"+address)) {
                //获得剩余限制登陆时间
                Long expire = redisTemplate.getExpire(lockLoginKey,TimeUnit.MINUTES);

                System.out.println("你已经被限制登陆，剩余时间为："+expire+"分钟");


                request.setAttribute("message","已经被限制登陆，剩余时间为："+expire+"分钟");
                request.getRequestDispatcher("/limitLogin").forward(request,response);
            }else{
                filterChain.doFilter(request,response);
            }
        }else {
            filterChain.doFilter(request,response);
        }


    }
}
