package com.myblog.service;

import com.myblog.entity.User;

import java.util.List;

public interface UserService {

    void addUser(User user);

    User findUserByUsername(String username);


    User findUserById(Long user_id);

    List<User> findUserListById(List userIds);


    void updateUserAvatar(String resultFileLocation, Long id);

    void updateUser(User principal);

    User finUserByNickname(String name);

    boolean findUserIsExitByUsername(String username);

    boolean findUserIsExitByNickname(String keyword);
}
