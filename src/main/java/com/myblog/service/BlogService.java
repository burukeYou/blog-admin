package com.myblog.service;

import com.myblog.entity.Blog;
import com.myblog.entity.BlogFile;
import com.myblog.vo.PageBean;
import com.myblog.vo.QueryConditionVo;

import java.util.List;

public interface BlogService {

    void saveBlog(Blog blog);

    Blog getBlogById(Long id);

    void readSizeIncrease(Long id);

    void deleteBlogById(Long id);

    void updateBlog(Blog blog);

    PageBean<Blog> findBlogByCondition(QueryConditionVo queryVo);

    void commentSizeIncrease(Long blog_id);

    void commentSizeReduce(Long blog_id);

    void praiseSizeIncrease(Long blogId);

    void praiseSizeReduce(Long blogId);


    void insertAllBlogFile(List<BlogFile> blogFiles);

    Integer findBlogCountByUser(Long id);

    Integer findUserAccessCount(Long id);

    Integer findUserLikeCount(Long id);

    Integer findUserCommentCount(Long id);
}
