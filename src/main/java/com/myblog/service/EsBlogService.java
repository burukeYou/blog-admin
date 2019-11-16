package com.myblog.service;

import com.myblog.entity.Es.EsBlog;
import com.myblog.entity.User;
import com.myblog.vo.TagVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EsBlogService {


    void addBlog(EsBlog blog);


    EsBlog getEsBlogByBlogId(Long id);

    void deleteEsBlog(EsBlog esBlog);

    Page<EsBlog> findNewEsBlogsByCondition(String keyword, Pageable pager);

    Page<EsBlog> findHotEsBligByCondition(String keyword, Pageable pageable);

    List<EsBlog> findTop6NewEsBlog();

    List<EsBlog> findTop6HotEsBlog();

    List<User> findTopUsers();

    List<TagVo> findTopTags();
}
