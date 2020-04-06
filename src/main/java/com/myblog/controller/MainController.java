package com.myblog.controller;


import com.myblog.dao.es.EsBlogRepository;
import com.myblog.entity.Es.EsBlog;
import com.myblog.entity.Role;
import com.myblog.entity.User;
import com.myblog.enums.NotificationStatusEnum;
import com.myblog.exception.MyCustomizeException;
import com.myblog.service.EsBlogService;
import com.myblog.service.NotificationService;
import com.myblog.service.UserService;
import com.myblog.vo.QueryConditionVo;
import com.myblog.vo.ResultVo;
import com.myblog.vo.TagVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;


/**
 *                  首页控制器
 *
 *
 *  Page对象
         int getTotalPages() 总的页数
         long getTotalElements() 返回总数
         List getContent() 返回此次查询的结果集
         "totalElements": 9,
         "totalPages": 2,
         "size": 6,
         "number": 0,
         "numberOfElements": 6,
         "first": true,
         "last": false,
         "sort": {
         "unsorted": false,
         "sorted": true

 -------------
    直接写名字会交给视图解析器解析
    加上redirect或者forward才是存储转发到其他请求
 *
 */
@Controller
public class MainController {

    @Autowired
    private UserService userService;

    @Autowired
    private EsBlogRepository esBlogRepository;

    @Autowired
    private EsBlogService esBlogService;

    @Autowired
    private NotificationService notificationService;


    private PasswordEncoder pe = new BCryptPasswordEncoder();

    @Autowired
    private RedisTemplate redisTemplate;



    @RequestMapping("/")
    public String root() {
        return "redirect:/index";
    }


    @RequestMapping("/index")
    public String toIndex(HttpSession session){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal != null && principal instanceof UserDetails){
            User user =  (User)principal;

       /*     System.out.println(user);*/
            session.setAttribute("user",user);

            //1-查看消息列表
            int  unreadCount = notificationService.findUSerTypeMsgCountByStatus(user.getNickname(),null, NotificationStatusEnum.UNREAD.getStatus());
            session.setAttribute("unreadCount",unreadCount);
        }

        return "forward:/blogs";
    }

    @GetMapping("/about")
    public String toAbout(){
        return "about";
    }

    @GetMapping("/contact")
    public String toContact(){
        return "contact";
    }

    @GetMapping("/single")
    public String toSingle(){
        return "single";
    }

    @RequestMapping("/toLogin")
    public String toLogin(){
        return "loginAndRegister.html";
    }



    /**
     *          注册用户
     */
    @PostMapping("/register")
    @ResponseBody
    public ResultVo<String> registerUser(User user){
        //判断注册信息
        if (user.getUsername().equals("") || userService.findUserIsExitByUsername(user.getUsername())
                || user.getNickname().equals("")
                || userService.findUserIsExitByNickname(user.getNickname())
                || user.getEmail().equals("") || user.getEmail() == null
                || user.getPassword().equals("") || user.getPassword() == null
        )
            return ResultVo.errorOf(400,"注册失败，请检查是否值为空");


        try {
            if (user != null){
                user.setAccountNonLocked(true);
                user.setAccountNonExpired(true);
                user.setEnabled(true);
                user.setCredentialsNonExpired(true);
                user.setAvatar("/img/user.jpeg");

                //对密码进行加密
                user.setPassword(pe.encode(user.getPassword()));

                //根据用户分配的角色设置权限
                ArrayList<Role> roleList = new ArrayList<>();
                roleList.add(new Role(20,"博主","ROLE_BLOGGER",""));
                user.setRoles(roleList);

                userService.addUser(user);

            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVo.okOf(400,"注册失败");
        }

        return ResultVo.okOf(200,"注册成功");
    }


    /**
     *  异步加载判断当前新建账号或者用户名是否重复
     */
    @GetMapping("/register/{type}/{keyword}")
    @ResponseBody
    public ResultVo<String> register(@PathVariable("type") String type, @PathVariable("keyword")String keyword){
        boolean res = false;

        if ("username".equals(type)){
           res = userService.findUserIsExitByUsername(keyword);
        } else if ("nickname".equals(type)) {
            res = userService.findUserIsExitByNickname(keyword);

        }
        return res == false ? ResultVo.okOf(200,"可用") : ResultVo.okOf(400,"该名字已存在，请重新设置");
    }




    /**
     *      首页从es中搜索获取博客列表
     */
    @GetMapping("/blogs")
    public String getIndexBlogList(QueryConditionVo queryVo, Model model){
        //0-博客列表
        Page<EsBlog> page = null;
        if ("".equals(queryVo.getKeyword()))
            queryVo.setKeyword(null);

        try {
            if (queryVo.getOrder().equals("hot")){
                //最热查询
                Sort sort = new Sort(Sort.Direction.DESC,"readSize","commentSize","praise_count","createTime");
                Pageable pageable = new PageRequest(queryVo.getCurrentPage()-1,queryVo.getPageSize(),sort);
                page = esBlogService.findHotEsBligByCondition(queryVo.getKeyword(),pageable);

            }
            else if (queryVo.getOrder().equals("new")){
                //最新查询
                Sort sort = new Sort(Sort.Direction.DESC,"createTime");
                Pageable pager = new PageRequest(queryVo.getCurrentPage()-1,queryVo.getPageSize(),sort);
                page = esBlogService.findNewEsBlogsByCondition(queryVo.getKeyword(), pager);
            }
            List<EsBlog> esblogs = page.getContent();

            /**
             *
             */
            //1-从es查找最热门的6篇文章
            List<EsBlog> hotest = esBlogService.findTop6HotEsBlog();


            //2-从es中查找最新发布的6篇文章
            List<EsBlog> newest = esBlogService.findTop6NewEsBlog();


            //3-查找最热门的用户
            List<User> users = esBlogService.findTopUsers();


            //4-查找最热门的标签
            List<TagVo> tags = esBlogService.findTopTags();


            //存储
            model.addAttribute("pageBean",page);
            model.addAttribute("esbloglist",esblogs);
            model.addAttribute("newest",newest);
            model.addAttribute("hotest",hotest);
            model.addAttribute("users",users);
            model.addAttribute("tags",tags);
        } catch (Exception e) {
            e.printStackTrace();
            return "index";
        }

        return "index";
    }



    @RequestMapping("/limitLogin")
    public void tolimitLogin(HttpServletRequest request){
        throw new MyCustomizeException((String) request.getAttribute("message"));
    }





}
