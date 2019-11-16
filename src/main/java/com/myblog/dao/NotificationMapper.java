package com.myblog.dao;

import com.myblog.entity.Notification;
import com.myblog.vo.MsgConditionVo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 *     使用<script>时，if标签里判断的字段不用加#{}
 */

@Mapper
public interface NotificationMapper {

    @Insert("insert into Notification values(#{id},#{notifier},#{receiver},#{type},#{typeId},#{typeName},#{createtime},#{status})")
    void insertNotice(Notification notification);


    @Select("select * from  Notification where receiver=#{nickanme} and type = #{type} order by createtime desc limit #{currentPage},#{pageSize}")
    List<Notification> findAllMsgByCondition(MsgConditionVo msg);

    @Select("<script> select count(*) from  Notification where receiver=#{nickanme} " +
            "<if test='type != null'> and type = #{type} </if> <if test='status != null'> and status = #{status}  </if></script>")
    int findUserAllMsgByConditionCount(MsgConditionVo msgConditionVo);


    @Delete("<script>delete from Notification where receiver=#{nickanme} <if test='type != null'> and type = #{type} </if> " +
            "<if test='id != null'> and id = #{id} </if> </script>")
    void deleteUserMsgByCondition(@Param("nickanme") String nickname, @Param("type")Integer type, @Param("id")Long id);

    @Update("update Notification set status = #{status} where id = #{id}")
    void updateReadStatus(@Param("id")Long id,@Param("status")int status);

    @Update("update Notification set status = #{status} where receiver = #{nickname} and type = #{type}")
    void updateAllReadStatus(@Param("nickname")String nickname, @Param("type")int type,@Param("status")int status);
}
