package com.myblog.advice;


import com.myblog.exception.MyCustomizeException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 *  自定义异常处理器
 *
 *
 *
 */


@ControllerAdvice
public class CustomizeExceptionHandler {

    @ExceptionHandler(Exception.class)  //指定需要的异常对象，这里指定所有
    public String  handle(HttpServletRequest request, Throwable ex, Model model) {


        if (ex instanceof MyCustomizeException){
            model.addAttribute("message",ex.getMessage());
        }else {
            model.addAttribute("message","服务器繁忙，请稍后再试一试！");
            ex.printStackTrace();
        }
        return "/error/error";
    }




}
