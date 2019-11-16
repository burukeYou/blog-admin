package com.myblog.service;

import com.myblog.entity.Praise;
import com.myblog.entity.User;

import java.util.List;

public interface PraiseService {

    List<Praise> findUserPraiseOfBlog(Integer type,Long id);

    void addPraise(User user,Integer type, Long entityId);

    void cancelPraiseById(Integer id,Integer entityType,Long blogId);

    boolean findUserIsPraiseOfEntity(Long id, Integer entityType, Long entityId);

    List<Praise> findUserPraiseOfType(Long userId, Integer parentType);


    void deletePraiseByTypeAndParentId(Integer type, Long id);
}
