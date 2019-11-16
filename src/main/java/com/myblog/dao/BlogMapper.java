package com.myblog.dao;

import com.myblog.entity.Blog;
import com.myblog.vo.QueryConditionVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BlogMapper {


    @Insert("insert into blog " +
            "(id,user_id,category_id,title,summary,content,htmlContent,createTime,readSize,commentSize,praise_count,tags,status,blog_img) " +
            "values(#{id},#{user_id},#{category_id},#{title},#{summary},#{content},#{htmlContent},#{createTime},#{readSize},#{commentSize},#{praise_count},#{tags},#{status},#{blog_img})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    void insertBlog(Blog blog);


    //@Select("select * from blog where id = #{blog_id}")
    Blog getBlogById(Long id);

    @Update("update blog set readSize = readSize + 1 where id =  #{blog_id}")
    void readSizeIncrease(@Param("blog_id")Long id);

    @Delete("delete from blog where id = #{blog_id}")
    void deleteBlogById(@Param("blog_id")Long id);


    @Update("update blog set category_id=#{category_id},title=#{title},summary=#{summary},content=#{content},htmlContent=#{htmlContent}," +
            "tags=#{tags},blog_img=#{blog_img} where id = #{id}")
    void updateBlog(Blog blog);


    int findBlogCountByCondition(QueryConditionVo queryVo);

    List<Blog> findBlogByCondition(QueryConditionVo queryVo);

    @Update("update blog set commentSize = commentSize + 1 where id =  #{blog_id}")
    void commentSizeIncrease(@Param("blog_id")Long blog_id);

    @Update("update blog set commentSize = commentSize - 1 where id =  #{blog_id}")
    void commentSizeReduce(@Param("blog_id")Long blog_id);

    @Update("update blog set praise_count = praise_count + 1 where id =  #{blog_id}")
    void praiseSizeIncrease(@Param("blog_id")Long blogId);

    @Update("update blog set praise_count = praise_count - 1 where id =  #{blog_id}")
    void praiseSizeReduce(@Param("blog_id")Long blogId);

    @Select("SELECT COUNT(*) FROM blog where user_id = #{id}")
    Long findBlogCountByUser(@Param("id") Long id);

    @Select("SELECT SUM(readSize) FROM blog where user_id = #{id}")
    Long findUserAccessCount(@Param("id")Long id);

    @Select("SELECT SUM(praise_count) FROM blog where user_id = #{id}")
    Long findUserLikeCount(Long id);

    @Select("SELECT SUM(commentSize) FROM blog where user_id = #{id}")
    Long findUserCommentCount(Long id);
}
