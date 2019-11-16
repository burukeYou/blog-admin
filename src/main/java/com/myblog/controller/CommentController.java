package com.myblog.controller;

import com.myblog.entity.Comment;
import com.myblog.entity.Praise;
import com.myblog.entity.User;
import com.myblog.enums.PraiseEntityType;
import com.myblog.service.CommentService;
import com.myblog.service.PraiseService;
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
 *
 |  @RequestParam(value=”userid”)Integer id),这样会把地址栏参数名为userid的值赋给参数id,value不写默认参数名为id

 *
 */
@Controller
public class CommentController {


    @Autowired
    private CommentService commentService;

    @Autowired
    private PraiseService praiseService;

    /**
     * 发表评论
     */
    @PostMapping("/comments")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ResultVo addComment(Comment comment) {
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //1-
        comment.setCreateTime(new Date());
        comment.setPraise_count(0);
        comment.setUser_id(principal.getId());
        comment.setUser(principal);

        try {
            //2-
            commentService.saveComment(comment);

            return ResultVo.okOf();

        } catch (Exception e) {
            e.printStackTrace();
            return ResultVo.errorOf(400,"评论失败");
        }

    }


    /**
     *      查找博客下所有评论
     *              --博客id
     */
    @GetMapping("/comments/{id}")
    @ResponseBody
    public PageBean<Comment> findAllComment(@PathVariable("id") Long id,
                                        @RequestParam(value = "currentPage",required = false,defaultValue="1") Integer currentPage,
                                        @RequestParam(value="order",required=false,defaultValue="new") String order
                                        ) {

        //1-
        if (currentPage <= 0)
            currentPage = 1;

        PageBean<Comment> comments = commentService.fingAllCommentByBlog_id(id, currentPage,order);

        //2-判断是否登陆
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal != null && principal instanceof UserDetails){
            User user = (User)principal;

            //1-查询用户点赞的所有评论点赞
            List<Praise> praiseList = praiseService.findUserPraiseOfType(user.getId(),PraiseEntityType.COMMENT);

            //遍历评论列表判断当前用户是否点赞过
            if (praiseList != null)
                for (Comment e : comments.getDataList()) {
                    for (Praise praise : praiseList) {
                        if (e.getId().longValue() == praise.getParentId().longValue()){
                            e.setCurrentUserIspraise(true);
                            e.setCurrentPraiseId(praise.getId());
                            break;
                        }
                    }
                }
        }


        return comments;
    }


    /**
     *      删除评论
     *             --评论id
     */
    @DeleteMapping("/comments/{id}")
    @PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ResultVo<String> deletredComment(@PathVariable("id") Long id) {

        //1-判断删除的评论所属的用户是否是当前登陆用户
        //根据评论id查找评论及所属用户        --这个评论所属用户id不要从前台传过来不然别人可以伪装
        Comment comment = commentService.findCommentById(id);
        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        if (principal.getId() != comment.getUser_id()){
            return ResultVo.errorOf(400,"你没有权限删除");
        }

        //2-删除
        try {
            commentService.deleteCommentById(id);
            return ResultVo.okOf();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVo.errorOf(400,"删除失败");
        }
    }




}
