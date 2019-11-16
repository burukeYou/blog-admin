package com.myblog.controller;

import com.myblog.entity.Praise;
import com.myblog.entity.User;
import com.myblog.exception.AjaxErrorCode;
import com.myblog.service.PraiseService;
import com.myblog.vo.PraiseVo;
import com.myblog.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller

public class PraiseController {

    @Autowired
    private PraiseService praiseService;


    //查看点赞情况
    @GetMapping("/praise/{entityType}/{entityId}")
    @ResponseBody
    public ResultVo<PraiseVo> getPraise(@PathVariable("entityId") Long entityId,@PathVariable("entityType") Integer entityType){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        //查找博客所有的点赞
        List<Praise> praiseList = praiseService.findUserPraiseOfBlog(entityType,entityId);

        //1-判断用户是否点赞过
        boolean isPraise = false;
        Integer praiseId = null;

        if (principal != null && principal instanceof UserDetails){
            User user = (User)principal;

            for (Praise e : praiseList) {
                if (e.getUser_id() == user.getId()){
                    isPraise = true;
                    praiseId = e.getId();
                }
            }
        }

        //2
        ResultVo<PraiseVo> res = new ResultVo();
        res.setData(new PraiseVo(praiseId,praiseList.size(),isPraise));

        return res;
    }




    //发表点赞
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/praise/{entityType}/{entityId}")
    @ResponseBody
    public ResultVo<String> publicPraise(@PathVariable("entityId") Long entityId,@PathVariable("entityType") Integer entityType){
        User user =  (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        //1-判断用户是否点赞过
        boolean isPraise = praiseService.findUserIsPraiseOfEntity(user.getId(),entityType,entityId);
        if (isPraise == true)
            return  ResultVo.resultOf(AjaxErrorCode.PRAISE_FAILURE);

        //
        praiseService.addPraise(user,entityType,entityId);

        //新的点赞数
        return ResultVo.resultOf(AjaxErrorCode.PRAISE_SUCCESS);
    }

    //取消点赞
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/praise/{entityType}/{id}")
    @ResponseBody
    public ResultVo<String> cancelPraise(@PathVariable("id") Integer id,@PathVariable("entityType") Integer entityType,Long entityId){


        try {
            praiseService.cancelPraiseById(id,entityType,entityId);


        } catch (Exception e) {
            e.printStackTrace();
            return ResultVo.errorOf(400,"你未点赞过");

        }

        return ResultVo.okOf();
    }






}
