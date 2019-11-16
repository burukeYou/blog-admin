package com.myblog.dao;

import com.myblog.entity.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper  {


    @Select("select* from category where user_id =  #{id}")
    List<Category> findAllByUser_id(@Param("id") Long user_id);

    @Select("select * from category  where id = #{id}")
    Category findCategoryById(@Param("id")Integer category_id);

    @Insert("insert into category values(#{id},#{categoryName},#{user_id})")
    void saveCategory(Category category);

    @Update("update category set categoryName=#{categoryName} where id = #{id}")
    void updateCategory(Category category);

    @Delete("delete from  category where id = #{id}")
    void deletUserCategoryById(@Param("id") int id);

    @Select("select * from category  where user_id = #{user_id} and categoryName = #{name}")
    Category findUserCategoryByName(@Param("name") String categoryName,@Param("user_id") Long user_id);
}
