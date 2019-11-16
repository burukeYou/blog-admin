package com.myblog.service;

import com.myblog.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> findAllCateByUser_id(Long user_id);

    Category findCategoryById(Integer category_id);

    void saveCategory(Category category);

    void updateCategory(Category category);

    void deletUserCategoryById(int id);

    Category findUserCategoryByName(String categoryName, Long user_id);
}


