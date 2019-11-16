package com.myblog.service.impl;

import com.myblog.dao.CommentMapper;
import com.myblog.entity.Comment;
import com.myblog.entity.Es.EsBlog;
import com.myblog.entity.Notification;
import com.myblog.entity.Reply;
import com.myblog.enums.NotificationStatusEnum;
import com.myblog.enums.NotificationTypeEnum;
import com.myblog.enums.PraiseEntityType;
import com.myblog.service.*;
import com.myblog.vo.PageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private BlogService blogService;

    @Autowired
    private EsBlogService esBlogService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PraiseService praiseService;

    @Autowired
    private ReplyService replyService;


    @Transactional
    @Override
    public void saveComment(Comment comment) {
        //1-保存评论
        commentMapper.saveComment(comment);

        //2-评论所属于博客评论数加1
        blogService.commentSizeIncrease(comment.getBlog_id());

        //3-同时更新es
        EsBlog esblog = esBlogService.getEsBlogByBlogId(comment.getBlog_id());
        esblog.setCommentSize(esblog.getCommentSize()+1);
        esBlogService.addBlog(esblog);

        //4-创建通知
        Notification notification = new Notification()
            .setCreatetime(new Date())
            .setType(NotificationTypeEnum.COMMENT_BLOG.getType())
            .setTypeId(comment.getBlog_id())
            .setTypeName(esblog.getTitle()+"<br><br>评论内容为："+comment.getContent())
            .setNotifier(comment.getUser().getNickname()) //评论的用户
            .setReceiver(esblog.getUsername())  //博客所属的用户
            .setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notificationService.publicNotice(notification);
    }

    @Override
    public PageBean<Comment> fingAllCommentByBlog_id(Long id,Integer currentPage,String order) {

        //查找评论的总个数
        Integer totalDataCount = commentMapper.findAllCommentCountByBlog_id(id);

        //1-判断查询评论的顺序
        List<Comment> commentList = null;
        int pageSize = 5;
        int startRow = (currentPage-1)*pageSize;
        if (order.equals("new")){
           commentList = commentMapper.findAllNewCommentByBlog_id(id,startRow,pageSize);

        }else if (order.equals("hot")){
            commentList = commentMapper.findAllHotCommentByBlog_id(id,startRow,pageSize);
        }

        //2-封装PageBean
        PageBean<Comment> page = new  PageBean<>(currentPage,totalDataCount,pageSize);
        page.setCurrentPage(currentPage);
        page.setTotalDataCount(totalDataCount);
        page.setDataList(commentList);

        return page;
    }

    @Transactional
    @Override
    public void deleteCommentById(Long id) {

        Comment comment = commentMapper.findCommentById(id);

        //2-评论所属评论数-1
        blogService.commentSizeReduce(comment.getBlog_id());

        //3-es的评论数-1
        EsBlog esBlog = esBlogService.getEsBlogByBlogId(comment.getBlog_id());
        esBlog.setCommentSize(esBlog.getCommentSize()-1);
        esBlogService.addBlog(esBlog);

        //4-删除此评论下的所有点赞
        praiseService.deletePraiseByTypeAndParentId(PraiseEntityType.COMMENT,id);
        List<Reply> list = replyService.findAllReplyByCommentId(id);  //删除从评论下所有回复的点赞
        for (Reply reply : list) {
            praiseService.deletePraiseByTypeAndParentId(PraiseEntityType.REPLY,reply.getId());
        }

        //1-从数据库删除
        commentMapper.deleteCommentById(id);

    }



    @Override
    public Comment findCommentById(Long id) {

        return commentMapper.findCommentById(id);
    }

    @Override
    public void praiseSizeReduce(Long entityId) {
        commentMapper.praiseSizeReduce(entityId);
    }

    @Override
    public void praiseSizeIncrease(Long entityId) {
        commentMapper.praiseSizeIncrease(entityId);

    }
}
