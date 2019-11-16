package com.myblog.controller;

import com.myblog.entity.Praise;
import com.myblog.entity.Reply;
import com.myblog.entity.User;
import com.myblog.enums.PraiseEntityType;
import com.myblog.service.PraiseService;
import com.myblog.service.ReplyService;
import com.myblog.vo.PageBean;
import com.myblog.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 *      不加contentType:"application/json"的时候，发送类型变为默认的application/x-www-form-urlencoded，而这种方式会以键值对的形式将对象序列化
 *      在jquery的ajax中，如果没加contentType:"application/json"，那么data就应该对应的是json对象；
 *      反之，如果加了contentType:"application/json"，那么ajax发送的就必须是字符串。(这种方式配合@RequestBody穿参数)
 *
 *
 *
 *
 */
@Controller
public class ReplyController {

    @Autowired
    private ReplyService replyService;

    @Autowired
    private PraiseService praiseService;

    /**
            分页获取评论下的回复
     */
    @GetMapping("/replys/{id}")
    @ResponseBody
    public PageBean<Reply> findAllReplyByCommentId(@PathVariable("id") Long comment_id,@RequestParam(defaultValue = "1") Integer currentPage){
        //实现加载更多式的分页查询，默认起始页式0，最热查询
        PageBean<Reply> pageBean= replyService.findAllReplyByCommentId(comment_id,currentPage);

        //判断当前登陆用户是否对该回复点过赞
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal != null && principal instanceof UserDetails){
            User user = (User)principal;

            //1-查询用户点赞的所有评论点赞
            List<Praise> userPraises = praiseService.findUserPraiseOfType(user.getId(), PraiseEntityType.REPLY);

            for (Reply reply : pageBean.getDataList()) {
                for (Praise e : userPraises) {
                    if (e.getParentId() == reply.getId().intValue()){
                        reply.setPraise(true);
                        reply.setCurrentPraiseId(e.getId());
                    }

                }
            }
        }

        return pageBean;

    }

    /**
     *      发表回复
     */
     @PostMapping("/replys")
     @PreAuthorize("isAuthenticated()")
     @ResponseBody
     public ResultVo<String> addReply(@RequestBody Reply reply){
         User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

         //1
         reply.setCreatetime(new Date());
         reply.setParse_count(0);
         reply.setReplyToOtherName(principal.getNickname());
         reply.setOwnerId(principal.getId());

         try {
             //2
             replyService.saveReply(reply);
             return ResultVo.okOf();

         } catch (Exception e) {
             e.printStackTrace();
             return ResultVo.errorOf(400,"回复失败");
         }
     }


    /**
     *      删除回复
     */
     @DeleteMapping("/replys/{id}")
     @PreAuthorize("isAuthenticated()")
     @ResponseBody
     public ResultVo<String> deleteReply(@PathVariable("id")Long id){

        //1-判断能否删除
         Reply reply = replyService.findReplyById(id);
         User principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
         if (!reply.getReplyToOtherName().equals(principal.getNickname())){
             return ResultVo.errorOf(400,"你没有权限删除");
         }

         //2
         try {
             replyService.deleteReplyById(id);
             return ResultVo.okOf();

         } catch (Exception e) {
             e.printStackTrace();
             return ResultVo.errorOf(400,"删除失败");
         }
     }







}
