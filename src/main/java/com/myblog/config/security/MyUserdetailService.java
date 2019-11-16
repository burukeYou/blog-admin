package com.myblog.config.security;

import com.myblog.dao.UserMapper;
import com.myblog.entity.Role;
import com.myblog.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("myUserDetailService")
public class MyUserdetailService implements UserDetailsService {

    @Autowired
    private UserMapper dao;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //
        User user= dao.findUserByUsername(username);

        if (user != null){
            //配置权限信息
            List<GrantedAuthority> permissionList = new ArrayList<>();
            for (Role e : user.getRoles()){
                permissionList.add(new SimpleGrantedAuthority(e.getRoleTag()));
            }

            user.setAuthorities(permissionList);
        }

        return user;
    }
}
