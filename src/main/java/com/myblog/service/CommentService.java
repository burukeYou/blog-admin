package com.myblog.service;

import com.myblog.entity.Comment;
import com.myblog.vo.PageBean;

public interface CommentService {
    void saveComment(Comment comment);

    PageBean<Comment> fingAllCommentByBlog_id(Long id,Integer currentPage,String order);

    void deleteCommentById(Long id);

    Comment findCommentById(Long id);

    void praiseSizeReduce(Long entityId);

    void praiseSizeIncrease(Long entityId);
}
