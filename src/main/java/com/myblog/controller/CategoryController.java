package com.myblog.controller;

import com.myblog.entity.Category;
import com.myblog.entity.User;
import com.myblog.exception.AjaxErrorCode;
import com.myblog.service.CategoryService;
import com.myblog.service.UserService;
import com.myblog.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CategoryController {


    @Autowired
    private CategoryService cateService;

    @Autowired
    private UserService userService;


    /**
     *      查找用户下所有个人分类
     */
    @GetMapping("/categorys/{user_id}")
    @ResponseBody
    public List<Category> getCategory(@PathVariable("user_id") Long user_id){

        List<Category> cateList = cateService.findAllCateByUser_id(user_id);

        System.out.println(cateList);

        return cateList;
    }


    /**
     *      查找用户下所有个人分类
     */
    @GetMapping("/categorys/")
    @ResponseBody
    public List<Category> getCategory(@RequestParam("username") String username){

        User user = userService.findUserByUsername(username);

        List<Category> cateList = cateService.findAllCateByUser_id(user.getId());

        System.out.println(cateList);

        return cateList;
    }


    /**
     *   用户新建或者保存个人分类
     *          --当前登陆用户要等于要新建个人分类的用户username
     */
    @PostMapping("/categorys")
    @PreAuthorize("authentication.name.equals(#username)")
    @ResponseBody
    public ResultVo<String> addCategory(Category category,String username){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        //2-判断是修改还是新增
        try {
            //3-查找此分类是否已经存在
            Category cate = cateService.findUserCategoryByName(category.getCategoryName(),user.getId());
            if (cate != null)
                return ResultVo.resultOf(AjaxErrorCode.ADD_CATEGORY_FAILURE);


            if (category.getId() == null){
                category.setUser_id(user.getId());
                cateService.saveCategory(category);
            }else {
                cateService.updateCategory(category);
            }

            return ResultVo.resultOf(AjaxErrorCode.ADD_CATEGORY_SUCCESS);

        } catch (Exception e) {
            e.printStackTrace();
            return ResultVo.resultOf(AjaxErrorCode.SAVE_FAILURE);
        }
    }


    /**
     *   删除用户的某一分类
     */
    @DeleteMapping("/categorys/{id}")
    @ResponseBody
    @PreAuthorize("authentication.name.equals(#username)")
    public ResultVo<String> delete(String username, @PathVariable("id") int id) {

        try {
            cateService.deletUserCategoryById(id);
            return ResultVo.okOf();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVo.errorOf(400,"删除失败");
        }
    }




}
