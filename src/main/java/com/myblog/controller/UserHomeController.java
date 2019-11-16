package com.myblog.controller;

import com.myblog.entity.Blog;
import com.myblog.entity.User;
import com.myblog.service.BlogService;
import com.myblog.service.UserService;
import com.myblog.vo.PageBean;
import com.myblog.vo.QueryConditionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *      用户个人博客主页
 *
 * 注意：
 *          model存的数据，重定向之后也是可以取到的，request取不到
 *
 */
@Controller
@RequestMapping("/home")
public class UserHomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private BlogService blogService;

    /**
     *      根据用户名进入用户的主页
     */
    @GetMapping("/{username}")
    public String toHome(@PathVariable("username") String name, Model model){

        //1-获得用户信息
        User user = userService.finUserByNickname(name);

        if (user == null)
            user = userService.findUserByUsername(name);

        //

        //1-查找当前用户总博客数
        Integer blogCount = blogService.findBlogCountByUser(user.getId());


        //2-用户总访问量
        Integer accessCount = blogService.findUserAccessCount(user.getId());

        //3-总喜欢
        Integer likeCount = blogService.findUserLikeCount(user.getId());

        //4-总评论数
        Integer commentCount = blogService.findUserCommentCount(user.getId());

        //
        model.addAttribute("blogCount",blogCount);
        model.addAttribute("accessCount",accessCount);
        model.addAttribute("likeCount",likeCount);
        model.addAttribute("commentCount",commentCount);


        //2-
        model.addAttribute("blogUser",user);

        return "users/userhomePage.html";
    }


    /**
     *      查找某一用户的所有博客
     *
     *
     *
     *  参数：keyword-搜索条件或者是标签都行
     *
     */
    @GetMapping("/{username}/blogs")
    @ResponseBody
    public PageBean<Blog> findUserBolgByKeyword(@PathVariable("username") String username,QueryConditionVo queryVo){
        /*//1-获得用户信息
         User user = userService.findUserByUsername(username);
         if (user == null){
             //return "error/error";
             return null;*/

        if (queryVo.getCurrentPage() <= 0)
            return null;

        PageBean<Blog> pageBean = blogService.findBlogByCondition(queryVo);

        return pageBean;
    }





}
