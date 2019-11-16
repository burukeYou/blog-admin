package com.myblog.service.impl;

import com.myblog.dao.UserMapper;
import com.myblog.entity.User;
import com.myblog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userDao;


    //添加用户并分配角色
    @Transactional
    public void addUser(User user) {
        userDao.addUser(user);
        userDao.addUser_Role(user);
    }

    @Override
    public User findUserByUsername(String username) {
        return userDao.findUserByUsername(username);
    }

    @Override
    public User findUserById(Long user_id) {
        return userDao.findUserById(user_id);
    }


    //批量查找用户
    @Override
    public List<User> findUserListById(List userIds) {
        if (userIds == null || userIds.size() <= 0)
            return null;

        return userDao.findUserListInId(userIds);
    }

    @Override
    public void updateUserAvatar(String resultFileLocation, Long id) {
        userDao.updateUserAvatar(resultFileLocation,id);
    }

    @Override
    public void updateUser(User principal) {
        userDao.updateUser(principal);
    }

    @Override
    public User finUserByNickname(String name) {
        return userDao.finUserByNickname(name);
    }

    @Override
    public boolean findUserIsExitByUsername(String username) {
        Long res = userDao.findUserCountByUsername(username);
        return res > 0 ? true:false;
    }

    @Override
    public boolean findUserIsExitByNickname(String keyword) {
        return userDao.findUserCountByNickname(keyword) >0 ? true : false;
    }


}
