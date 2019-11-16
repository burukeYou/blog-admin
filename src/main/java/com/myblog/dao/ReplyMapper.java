package com.myblog.dao;

import com.myblog.entity.Reply;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ReplyMapper {

    @Select("select * from reply where comment_id = #{id} order by parse_count limit #{startRow},#{size}")
    List<Reply> findAllReplyByCommentId(@Param("id") Long comment_id, @Param("startRow")Integer startRow,@Param("size")Integer pageSize);


    @Select("select * from reply where comment_id = #{id}")
    List<Reply> findAllByCommentId(Long id);


    @Select("select count(*) from reply where comment_id = #{comment_id}")
    Integer findAllReplyCount(@Param("comment_id")Long comment_id);

    @Insert("insert into reply values(#{id},#{ownerId},#{comment_id},#{content},#{parse_count},#{createtime},#{repliedByOtherName},#{replyToOtherName})")
    void insertReply(Reply reply);

    @Delete("delete from reply where id = #{id}")
    void deleteReplyById(@Param("id") Long id);

    @Select("select * from reply where id = #{id}")
    Reply findReplyById(@Param("id")Long id);

    @Update("update reply set parse_count = parse_count+1 where id = #{entityId}")
    void praiseSizeIncrease(@Param("entityId") Long entityId);

    @Update("update reply set parse_count = parse_count-1 where id = #{entityId}")
    void praiseSizeReduce(@Param("entityId")Long entityId);


}
