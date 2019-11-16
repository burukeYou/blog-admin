package com.myblog.dao;

import com.myblog.entity.Praise;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PraiseMapper {


    @Select("select * from praise where parentType = #{type} and parentId = #{parentId}")
    List<Praise> findUserPraiseOfBlog(@Param("type") Integer type,@Param("parentId") Long id);

    @Insert("insert into praise values(null,#{userId},#{type},#{blogId})")
    void insert(@Param("userId") Long id,@Param("type") Integer type,@Param("blogId") Long blogId);

    @Delete("delete from praise where id = #{id}")
    void deleteById(@Param("id") Integer id);

    @Select("select count(*) from praise where user_id = #{userId} and parentType = #{type} and parentId = #{parentId}")
    Long findUserIsPraiseOfEntity(@Param("userId")Long id, @Param("type")Integer entityType, @Param("parentId")Long parentId);

    @Select("select * from praise where user_id = #{userId} and parentType = #{parentType}")
    List<Praise> findUserPraiseOfComment(@Param("userId") Long userId, @Param("parentType")Integer parentType);

    @Delete("delete from praise where parentType = #{type} and parentId = #{id}")
    void deleteByTypeAndParentId(@Param("type")Integer type, @Param("id") Long id);


}
