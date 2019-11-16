package com.myblog.controller;


import com.myblog.config.security.MyUserdetailService;
import com.myblog.entity.User;
import com.myblog.service.BlogService;
import com.myblog.service.UserService;
import com.myblog.util.AliyunOssClientUtil;
import com.myblog.util.Base64DecodeMultipartFile;
import com.myblog.vo.FileVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 *          基于阿里云OSS对象存储上传
 */
@Controller
@PreAuthorize("isAuthenticated()")
public class FIleController {

    @Autowired
    private UserService userService;

    @Autowired
    private MyUserdetailService myUserdetailService;

    @Autowired
    private BlogService blogService;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 编写博客图片上传接口
     */
    @RequestMapping("/file/upload")
    @ResponseBody
    public FileVo upload(HttpServletRequest request,String blogToken) {


        //1-转换请求
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;


        //2-获取请求参数             --文件属输入框 name = 'editormd-image-file'
        MultipartFile file = multipartRequest.getFile("editormd-image-file");


        //3-使用OSS客户端上传并返回上传的文件名地址
        String res = null;

        //4-返回特定json信息
        FileVo fileVo = new FileVo();
        try {
            res = AliyunOssClientUtil.upload(file, AliyunOssClientUtil.FileDirType.blogImg);


           if (blogToken != null || blogToken != ""){
               //https://myblog-czh.oss-cn-shenzhen.aliyuncs.com/blogImg/bf8d5623-64d5-40a4-84b3-e4d5ef24d0f2.jpeg
               String url = res.substring(res.lastIndexOf(AliyunOssClientUtil.FileDirType.blogImg.getDir()), res.length());
               //2-将上传的图片保存到redis
               redisTemplate.opsForList().leftPush(blogToken,url);
               redisTemplate.expire(url,6, TimeUnit.HOURS);
           }


            fileVo.setSuccess(1);
            fileVo.setUrl(res);


        } catch (Exception e) {
            e.printStackTrace();

            fileVo.setSuccess(0);
        }

        return fileVo;
    }


    /** 上传头像
     *      以base64数据异步上传上传头像
     */
    @RequestMapping("/file/uploadAvatar")
    @ResponseBody
    public FileVo uploadAvatar(String image,HttpServletRequest request) {

        FileVo res = new FileVo();

        if (image == null || image.equals("")) {
            res.setSuccess(0);
            return res;
        }


        //1-使用OSS客户端上传并返回上传的文件名地址
        String resultFileLocation = null;
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //2-返回特定json信息
        try {
            //2-将base64数据转成MultipartFile
            MultipartFile file = Base64DecodeMultipartFile.base64Convert(image);

            //3-上传到OSS
            //先删除

            AliyunOssClientUtil.deleteFileByUrl(user.getAvatar());

            //再上传
            resultFileLocation = AliyunOssClientUtil.upload(file, AliyunOssClientUtil.FileDirType.avatar);


            //4-更新当前用户头像
            userService.updateUserAvatar(resultFileLocation, user.getId());

            //5-修改认证信息
            user = userService.findUserByUsername(user.getUsername());

            //1.从HttpServletRequest中获取SecurityContextImpl对象
            SecurityContext context = SecurityContextHolder.getContext();
            //2.从SecurityContextImpl中获取Authentication对象
            Authentication authentication = context.getAuthentication();
            //3.初始化UsernamePasswordAuthenticationToken实例 ，这里的参数user就是我们要更新的用户信息
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user,authentication.getCredentials(),authentication.getAuthorities());
            auth.setDetails(authentication.getDetails());
            //4.重新设置SecurityContextImpl对象的Authentication
            context.setAuthentication(auth);

            //
            res.setSuccess(1);
            res.setUrl(resultFileLocation);


        } catch (Exception e) {
            e.printStackTrace();
            res.setSuccess(0);
        }


        return res;
    }


}
