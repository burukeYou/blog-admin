package com.myblog.controller;

import com.myblog.entity.Blog;
import com.myblog.entity.Category;
import com.myblog.entity.User;
import com.myblog.service.BlogService;
import com.myblog.service.CategoryService;
import com.myblog.service.UserService;
import com.myblog.vo.PageBean;
import com.myblog.vo.QueryConditionVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 *          个人中心
 */


@Controller
@RequestMapping("/personal")
@PreAuthorize("isAuthenticated()")
public class PersonalCenter {

    @Autowired
    private BlogService blogService;


    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    //个人资料
    @GetMapping("index")
    public String toPersonalCenter(Model model){
        User principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //1-查找当前用户总博客数
        Integer blogCount = blogService.findBlogCountByUser(principal.getId());

        //2-用户总访问量
        Integer accessCount = blogService.findUserAccessCount(principal.getId());

        //3-总喜欢
        Integer likeCount = blogService.findUserLikeCount(principal.getId());

        //4-总评论数
        Integer commentCount = blogService.findUserCommentCount(principal.getId());

        //
        model.addAttribute("blogCount",blogCount);
        model.addAttribute("accessCount",accessCount);
        model.addAttribute("likeCount",likeCount);
        model.addAttribute("commentCount",commentCount);


        return "PersonalCenter/userSetting";
    }

    //修改个人资料
    @PostMapping("profile/{username}")
    @PreAuthorize("authentication.name.equals(#username)")
    public String updatePersonalCenter(@PathVariable("username") String username, User user){


        boolean isExit = userService.findUserIsExitByNickname(user.getNickname());
        if (isExit)
            return "PersonalCenter/userSetting" ;

        User principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //
        principal.setNickname(user.getNickname());
        principal.setEmail(user.getEmail());

        //判断密码是否更改
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String newPassword = encoder.encode(user.getPassword());
        if (!encoder.matches(principal.getPassword(),newPassword)){
            principal.setPassword(newPassword);
        }

        userService.updateUser(principal);


        return "PersonalCenter/userSetting";
    }



    //个人分类
    @GetMapping("category")
    public String toCategory(Model model){
        //1-
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //
        List<Category> categoryList = categoryService.findAllCateByUser_id(user.getId());
        model.addAttribute("categoryList",categoryList);

        return "PersonalCenter/userCategory";
    }



    //个人博客管理
    @GetMapping("bloglist")
    public String tobloglist(QueryConditionVo queryVo, Model model){
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        queryVo.setUser_id(user.getId());

        PageBean<Blog> pageBean = blogService.findBlogByCondition(queryVo);


        model.addAttribute("pageBean",pageBean);

        //
        model.addAttribute("order",queryVo.getOrder());
        model.addAttribute("keyword",queryVo.getKeyword());
        model.addAttribute("categoryId",queryVo.getCategoryId());

        return "PersonalCenter/bloglist";
    }


    //浏览历史记录
    @GetMapping("historyBlog")
    public String tohistoryBlog(Model model){
        User principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Blog> blogList = redisTemplate.opsForList().range(principal.getUsername()+":historyBlog", 0, -1);

        model.addAttribute("blogList",blogList);

        return "PersonalCenter/historyBlog";
    }


}
