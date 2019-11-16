package com.myblog.service;

import com.myblog.entity.Reply;
import com.myblog.vo.PageBean;

import java.util.List;

public interface ReplyService {
    PageBean<Reply> findAllReplyByCommentId(Long comment_id,Integer currentPage);

    void saveReply(Reply reply);

    void deleteReplyById(Long id);

    Reply findReplyById(Long id);

    void praiseSizeIncrease(Long entityId);

    void praiseSizeReduce(Long entityId);

    List<Reply> findAllReplyByCommentId(Long id);
}
