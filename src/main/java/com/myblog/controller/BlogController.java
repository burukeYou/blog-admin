package com.myblog.controller;

import com.github.rjeschke.txtmark.Processor;
import com.myblog.entity.*;
import com.myblog.enums.PraiseEntityType;
import com.myblog.exception.AjaxErrorCode;
import com.myblog.exception.GeneralErrorCode;
import com.myblog.exception.MyCustomizeException;
import com.myblog.service.BlogService;
import com.myblog.service.CategoryService;
import com.myblog.service.PraiseService;
import com.myblog.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 *      注意：删除博客后，把博客之前上传到OSS的图片也删除
 *
 *
 *
 *
 *
 */


@Controller
@PreAuthorize("isAuthenticated()")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private PraiseService praiseService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     *      去博客编写页面
     */
    @GetMapping("/{username}/blogs/edit")
    //@PreAuthorize("hasRole('BLOGGER')")
    public String toWriteBlog(Model model){

        model.addAttribute("blog",new Blog(null,null,null,null));

        return "writeBlog";
    }



    /**
     *  获取修改博客的界面
     *
     */
    @GetMapping("/{username}/blogs/edit/{id}")
    @PreAuthorize("authentication.name.equals(#username)")
    public String updateBlog(@PathVariable("username") String username, @PathVariable("id") Long id,Model model){
        Blog blog = blogService.getBlogById(id);

        if (blog == null){
            //抛出异常博客不存在
            return "error";
        }

        model.addAttribute("blog",blog);
        return "writeBlog.html";
    }

    /**
     *      保存或者修改博客
     */
    @PostMapping("/{username}/blogs/edit")
    @PreAuthorize("authentication.name.equals(#username)")
    @ResponseBody
    public ResultVo addBlog(@PathVariable("username") String username, Blog blog,String blogToken){
        //1-封装数据
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //封装EsBlog
        blog.setUser(user);
        if ("".equals(blog.getTags()))
            blog.setTags(null);

        //3-判断是修改后还是新增后的添加博客
        try {
            if (blog.getId() == null){
                blog.setUser_id(user.getId());
                blog.setReadSize(0);
                blog.setCommentSize(0);
                blog.setPraise_count(0);
                blog.setStatus(1);
                blog.setHtmlContent(Processor.process(blog.getContent()));
                blog.setCreateTime(new Date());
                blog.setBlog_img("/blogImage/"+(new Random().nextInt(15)+1)+".jpg");

                blogService.saveBlog(blog);

            }else {
                blog.setHtmlContent(Processor.process(blog.getContent()));
                blogService.updateBlog(blog);
            }

            //异步将不可上传过的图片从redis同步到数据库
            if (blog.getId() != null)
                flushBlogImageToDataBase(blog.getId(),blogToken);

             return ResultVo.resultOf(AjaxErrorCode.PUBLIC_BLOG_SUCCESS);

        } catch (Exception e) {

            //NoNodeAvailableException
            e.printStackTrace();
            return ResultVo.resultOf(AjaxErrorCode.PUBLIC_BLOG_FAILURE);
        }
    }

    @Async
    public void flushBlogImageToDataBase(Long blogId,String blogToken) {

        List<String> urls = redisTemplate.opsForList().range(blogToken, 0, -1);

        //
        if(urls != null && urls.size() <= 0)
            return;


        List<BlogFile> blogFiles = new ArrayList<>();
        for (String url : urls) {
            blogFiles.add(new BlogFile(blogId,url));
        }

        //
        blogService.insertAllBlogFile(blogFiles);
    }


    /**
     *    删除博客
     */
    @DeleteMapping("/{username}/blogs/{id}")
    @PreAuthorize("authentication.name.equals(#username)")
    @ResponseBody
    public ResultVo deleteBlog(@PathVariable("username")String username,@PathVariable("id") Long id){

        try {
            blogService.deleteBlogById(id);

            //更新缓存

            ResultVo resultVo = ResultVo.resultOf(AjaxErrorCode.DELETE_SUCCESS);
            return resultVo;
        } catch (Exception e) {
            e.printStackTrace();

            return ResultVo.resultOf(AjaxErrorCode.DELETE_FAILURE);
        }
    }





    /**
     *      根据博客id获取一个博客
     *      username: 当前登陆用户姓名
     *      id：博客id
     */
    @GetMapping("/{username}/blogs/{id}")
    @PreAuthorize("permitAll()")
    public String toBlogDetail(@PathVariable("id") Long id, Model model){
        //1-获取博客 (博客的用户，博客的分类)
        Blog blog = blogService.getBlogById(id);

        if (blog == null){
             throw new MyCustomizeException(GeneralErrorCode.BLOG_NOT_FOUND);
        }


        if (blog.getCategory_id() != null){
            Category category = categoryService.findCategoryById(blog.getCategory_id());
            blog.setCategory(category);
        }


        //3-博客阅读数加1
        blogService.readSizeIncrease(id);



        //4-判断操作用户是否是博客的所有者    -- 这个前台也可以做,但这个方法不适合前后端分离
        boolean isBlogOwner = false;
        User user = null;
        if (SecurityContextHolder.getContext().getAuthentication() !=null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                &&  !SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();


            //添加浏览历史记录
            addhistoryBlog(user,blog);

            if (user !=null  && blog.getUser_id() == user.getId()) {
                isBlogOwner = true;
            }
        }


        //5-判断当前操作用户是否对这篇博客点赞过
        boolean isPraise = false;
        Integer praiseId = null;
        if (user != null){
            List<Praise> praises = praiseService.findUserPraiseOfBlog(PraiseEntityType.BLOG,id);

            for (Praise e : praises)
                if (user.getId() == e.getUser_id()){
                    isPraise = true;
                    praiseId = e.getId();
                }
        }


        //6-
        model.addAttribute("blog",blog);
        model.addAttribute("isBlogOwner",isBlogOwner);
        model.addAttribute("isPraise",isPraise);
        if (isPraise){
            model.addAttribute("praiseId",praiseId);
        }

        return "admins/blog/blogPage";
    }

    /**
     *      添加浏览历史记录
     *
     */
    @Async
    void addhistoryBlog(User user,Blog blog){
        String key = user.getUsername()+":historyBlog";

        redisTemplate.opsForList().leftPush(key,blog);
        redisTemplate.expire(key,7, TimeUnit.DAYS);
        if (redisTemplate.opsForList().size(key) > 9){
            redisTemplate.opsForList().trim(key,0,9);
        }
    }
}
