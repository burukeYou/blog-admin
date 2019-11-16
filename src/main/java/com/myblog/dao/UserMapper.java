package com.myblog.dao;


import com.myblog.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMapper {


    //添加用户
    void addUser(User user);

    //给用户添加角色
    void addUser_Role(User user);

    //还包括用户的角色，权限
    User findUserByUsername(String username);

    @Select("select * from user where id = #{id}")
    User findUserById(@Param("id") Long user_id);


    List<User> findUserListInId(List userIds);

    @Update("update user set avatar = #{url} where id = #{id}" )
    void updateUserAvatar(@Param("url")String url, @Param("id")Long id);

    @Update("update user set nickname = #{nickname},email = #{email},password = #{password} where id = #{id}" )
    void updateUser(User principal);

    @Select("select * from user where nickname = #{name}")
    User finUserByNickname(@Param("name") String name);

    @Select("select count(*) from user where username = #{username}")
    Long findUserCountByUsername(@Param("username") String username);

    @Select("select count(*) from user where nickname = #{keyword}")
    Long findUserCountByNickname(@Param("keyword")String keyword);
}
