package com.myblog.dao;

import com.myblog.entity.Comment;
import org.apache.ibatis.annotations.*;
/**
 *  方法多个参数：
 *      法1：（xml）：
 *              public List<XXXBean> getXXXBeanList(String xxId, String xxCode);
 *
             <select id="getXXXBeanList" resultType="XXBean">

             　　select t.* from tableName where id = #{0} and name = #{1}

             </select>

             不用指定parameterType，由于是多参数那么就不能使用parameterType， 改用#｛index｝是第几个就用第几个的索引，索引从0开始

     法2：注解：


 *
 */
import java.util.List;

@Mapper
public interface CommentMapper {


    @Insert("insert into comment (id,user_id,blog_id,content,createTime,praise_count) values(#{id},#{user_id},#{blog_id},#{content},#{createTime},#{praise_count})")
    void saveComment(Comment comment);


    @Select("select count(*) from comment where blog_id = #{id}")
    Integer findAllCommentCountByBlog_id(@Param("id") Long id);


    //分页查找最新的评论
   // @Select("select * from comment where blog_id = #{blog_id} ORDER BY createTime DESC limit #{startRow},#{pageSize}")
    List<Comment> findAllNewCommentByBlog_id(@Param("blog_id") Long blog_id,@Param("startRow")Integer startRow, @Param("pageSize")int pageSize);

    //分页查找最热门的评论
    List<Comment> findAllHotCommentByBlog_id(@Param("blog_id") Long blog_id,@Param("startRow")Integer startRow, @Param("pageSize")int pageSize);


    @Delete("delete from comment where id = #{comment_id}")
    void deleteCommentById(@Param("comment_id")Long id);

    @Select("select * from comment where id = #{comment_id}")
    Comment findCommentById(@Param("comment_id") Long id);

    @Update("update comment set praise_count = praise_count - 1 where id = #{entityId}")
    void praiseSizeReduce(@Param("entityId")Long entityId);

    @Update("update comment set praise_count = praise_count + 1 where id = #{entityId}")
    void praiseSizeIncrease(Long entityId);
}
