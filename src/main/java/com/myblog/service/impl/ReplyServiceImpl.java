package com.myblog.service.impl;

import com.myblog.dao.ReplyMapper;
import com.myblog.entity.Comment;
import com.myblog.entity.Notification;
import com.myblog.entity.Reply;
import com.myblog.enums.NotificationStatusEnum;
import com.myblog.enums.NotificationTypeEnum;
import com.myblog.enums.PraiseEntityType;
import com.myblog.service.CommentService;
import com.myblog.service.NotificationService;
import com.myblog.service.PraiseService;
import com.myblog.service.ReplyService;
import com.myblog.vo.PageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ReplyServiceImpl implements ReplyService {

    @Autowired
    private ReplyMapper replyMapper;

    @Autowired
    private CommentService commentService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PraiseService praiseService;


    @Override
    public PageBean<Reply> findAllReplyByCommentId(Long comment_id,Integer currentPage) {

        //1-查找评论下所有的回复数
        Integer allReplyCount = replyMapper.findAllReplyCount(comment_id);


        List<Reply> replyList = replyMapper.findAllReplyByCommentId(comment_id,currentPage-1,10);

        PageBean<Reply> pageBean = new PageBean<>(currentPage,allReplyCount,10);
        pageBean.setDataList(replyList);

        return pageBean;
    }

    @Transactional
    @Override
    public void saveReply(Reply reply) {
        replyMapper.insertReply(reply);

        Comment comment = commentService.findCommentById(reply.getComment_id());

        //创建通知
        Notification notification = new Notification()
                .setCreatetime(new Date())
                .setType(NotificationTypeEnum.REPLY_COMMENT.getType())
                .setTypeId(comment.getBlog_id())
                .setTypeName(comment.getContent()+"<br><br> 回复内容为："+reply.getContent())   //
                .setNotifier(reply.getReplyToOtherName()) //评论的用户
                .setReceiver(reply.getRepliedByOtherName()) //博客所属的用户
                .setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notificationService.publicNotice(notification);
    }

    @Override
    @Transactional
    public void deleteReplyById(Long id) {
        replyMapper.deleteReplyById(id);

        //同时删除此回复下的所有点赞
        praiseService.deletePraiseByTypeAndParentId(PraiseEntityType.REPLY,id);

    }

    @Override
    public Reply findReplyById(Long id) {
        return replyMapper.findReplyById(id);
    }

    @Override
    public void praiseSizeIncrease(Long entityId) {
        replyMapper.praiseSizeIncrease(entityId);
    }

    @Override
    public void praiseSizeReduce(Long entityId) {
        replyMapper.praiseSizeReduce(entityId);
    }

    @Override
    public List<Reply> findAllReplyByCommentId(Long id) {
        return replyMapper.findAllByCommentId(id);
    }
}
