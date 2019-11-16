package com.myblog.service.impl;

import com.myblog.dao.CategoryMapper;
import com.myblog.entity.Category;
import com.myblog.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl  implements CategoryService {

    @Autowired
    private CategoryMapper cateMapper;

    @Override
    public List<Category> findAllCateByUser_id(Long user_id) {

        return cateMapper.findAllByUser_id(user_id);
    }

    @Override
    public Category findCategoryById(Integer category_id) {


        return cateMapper.findCategoryById(category_id);
    }

    @Override
    public void saveCategory(Category category) {
        cateMapper.saveCategory(category);
    }

    @Override
    public void updateCategory(Category category) {
        cateMapper.updateCategory(category);
    }

    @Override
    public void deletUserCategoryById(int id) {
        cateMapper.deletUserCategoryById(id);
    }

    @Override
    public Category findUserCategoryByName(String categoryName, Long user_id) {
        if (categoryName == "")
            return null;

        return cateMapper.findUserCategoryByName(categoryName,user_id);
    }
}









