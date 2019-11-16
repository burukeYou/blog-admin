package com.myblog.dao;

import com.myblog.entity.BlogFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogRepository extends JpaRepository<BlogFile,Long> {


    List<BlogFile> findAllByBlogId(Long blogId);

    void deleteAllByBlogId(Long blogId);


}
