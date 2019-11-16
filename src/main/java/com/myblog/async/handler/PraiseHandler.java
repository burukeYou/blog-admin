package com.myblog.async.handler;

import com.myblog.async.EventHandler;
import com.myblog.async.EventModel;
import com.myblog.entity.Comment;
import com.myblog.entity.Es.EsBlog;
import com.myblog.entity.Notification;
import com.myblog.entity.Reply;
import com.myblog.entity.User;
import com.myblog.enums.EventTypeEnum;
import com.myblog.enums.NotificationStatusEnum;
import com.myblog.enums.NotificationTypeEnum;
import com.myblog.enums.PraiseEntityType;
import com.myblog.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 *      点赞是事件处理器
 *          --处理发表点赞事件
 *          --处理取消点赞事件
 */

@Component("likeHandlerBean")
public class PraiseHandler implements EventHandler {


    @Autowired
    private BlogService blogService;

    @Autowired
    private EsBlogService esBlogService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private  ReplyService replyService;

    @Override
    @Transactional
    public void doHandle(EventModel eventModel) {
        System.out.println("========== 点赞 ==============");

        //发表点赞事件
        if (eventModel.getType() == EventTypeEnum.LIKE){
            //点赞博客
            if (eventModel.getEntityType() == PraiseEntityType.BLOG){

                User user =  userService.findUserById(eventModel.getActorId());
                //1-数据库博客点赞数加一
                blogService.praiseSizeIncrease(eventModel.getEntityId());

                //2-es博客点赞数也加一
                EsBlog esBlog =  esBlogService.getEsBlogByBlogId(eventModel.getEntityId());
                esBlog.setPraise_count(esBlog.getPraise_count()+1);
                esBlogService.addBlog(esBlog);

                //创建通知
                Notification notice = new Notification();
                notice.setCreatetime(new Date())
                        .setStatus(NotificationStatusEnum.UNREAD.getStatus())
                        .setType(NotificationTypeEnum.PRAISE.getType())
                        .setTypeId(eventModel.getEntityId())
                        .setTypeName(esBlog.getTitle())
                        .setNotifier(user.getNickname())
                        .setReceiver(esBlog.getUsername());
                notificationService.publicNotice(notice);
            }
            //点赞评论
            else if (eventModel.getEntityType() == PraiseEntityType.COMMENT){
                //评论数加一
                commentService.praiseSizeIncrease(eventModel.getEntityId());

                User Notifier =  userService.findUserById(eventModel.getActorId());
                User Receiver = userService.findUserById(eventModel.getEntityOwnerId());

                //创建通知
                Comment comment = commentService.findCommentById(eventModel.getEntityId());

                Notification notice = new Notification();
                notice.setCreatetime(new Date())
                        .setStatus(NotificationStatusEnum.UNREAD.getStatus())
                        .setType(NotificationTypeEnum.PRAISE.getType())  //通知类型-点赞消息
                        .setTypeId(eventModel.getEntityId())
                        .setTypeName(comment.getContent())
                        .setNotifier(Notifier.getNickname())
                        .setReceiver(Receiver.getNickname());
                notificationService.publicNotice(notice);
            }
            //点赞回复
            else if (eventModel.getEntityType() == PraiseEntityType.REPLY){
                //回复点赞数加一
                replyService.praiseSizeIncrease(eventModel.getEntityId());

                Reply reply = replyService.findReplyById(eventModel.getEntityId());

                User notifier =  userService.findUserById(eventModel.getActorId());
                User Receiver = userService.findUserById(eventModel.getEntityOwnerId());

                //创建通知
                Notification notice = new Notification()
                        .setCreatetime(new Date())
                        .setStatus(NotificationStatusEnum.UNREAD.getStatus())
                        .setType(NotificationTypeEnum.PRAISE.getType())
                        .setTypeId(eventModel.getEntityId())
                        .setTypeName(reply.getContent())
                        .setNotifier(notifier.getNickname())
                        .setReceiver(Receiver.getNickname());

                notificationService.publicNotice(notice);
            }
        }
        //取消点赞事件
        else if (eventModel.getType() == EventTypeEnum.UNLIKE){

            //取消点赞博客
            if (eventModel.getEntityType() == PraiseEntityType.BLOG){
                //1-数据库博客点赞数减一
                blogService.praiseSizeReduce(eventModel.getEntityId());

                //es博客点赞数也减一
                EsBlog esBlog =  esBlogService.getEsBlogByBlogId(eventModel.getEntityId());

                synchronized (esBlog){
                    esBlog.setPraise_count(esBlog.getPraise_count()-1);
                }

                esBlogService.addBlog(esBlog);
            }
            //取消点赞评论
            else if (eventModel.getEntityType() == PraiseEntityType.COMMENT){
                commentService.praiseSizeReduce(eventModel.getEntityId());
            }
            //取消点赞回复
            else if (eventModel.getEntityType() == PraiseEntityType.REPLY){
                replyService.praiseSizeReduce(eventModel.getEntityId());
            }


        }
    }




    //注册事件
    @Override
    public List<EventTypeEnum> getSupportEvenTypes() {
        return Arrays.asList(EventTypeEnum.LIKE,EventTypeEnum.UNLIKE);
    }
}
