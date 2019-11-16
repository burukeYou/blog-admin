package com.myblog.service.impl;

import com.myblog.async.EventModel;
import com.myblog.async.EventProducer;
import com.myblog.dao.PraiseMapper;
import com.myblog.entity.*;
import com.myblog.enums.EventTypeEnum;
import com.myblog.enums.PraiseEntityType;
import com.myblog.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PraiseServiceImpl implements PraiseService {

    @Autowired
    private PraiseMapper praiseMapper;

    @Autowired
    private BlogService blogService;

    @Autowired
    private EsBlogService esBlogService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ReplyService replyService;


    @Autowired
    private EventProducer eventProducer;  //事件生产者

    //查找用户点赞过的所有博客
    @Override
    public List<Praise> findUserPraiseOfBlog(Integer type,Long id) {

        return praiseMapper.findUserPraiseOfBlog(type,id);
    }

    @Override
    public void addPraise(User user, Integer type,Long entityId) {
        //添加点赞
        praiseMapper.insert(user.getId(),type,entityId);


        //创建异步任务消息
        EventModel model = new EventModel(EventTypeEnum.LIKE)
                .setActorId(user.getId())     //事件发起者
                .setEntityId(entityId)
                .setEntityType(type);

        if (type == PraiseEntityType.BLOG){
            Blog blog = blogService.getBlogById(entityId);

            model.setEntityOwnerId(blog.getUser_id());
        }
        else if (type == PraiseEntityType.COMMENT){
            Comment comment = commentService.findCommentById(entityId);
            model.setEntityOwnerId(comment.getUser_id());
        }else if (type == PraiseEntityType.REPLY){
            Reply reply = replyService.findReplyById(entityId);
            model.setEntityOwnerId(reply.getOwnerId());
        }


        eventProducer.fireEvent(
                model
        );

    }

    @Override
    public void cancelPraiseById(Integer id,Integer entityType,Long entityId) {
        praiseMapper.deleteById(id);

        //创建异步任务消息
        eventProducer.fireEvent(
                new EventModel(EventTypeEnum.UNLIKE)
                        .setEntityId(entityId)
                        .setEntityType(entityType));
    }

    @Override
    public boolean findUserIsPraiseOfEntity(Long id, Integer entityType, Long entityId) {

        Long res = praiseMapper.findUserIsPraiseOfEntity(id,entityType,entityId);
        if (res >0)
            return true;
        else
            return false;
    }

    @Override
    public List<Praise> findUserPraiseOfType(Long userId, Integer type) {

        return praiseMapper.findUserPraiseOfComment(userId,type);
    }

    @Override
    public void deletePraiseByTypeAndParentId(Integer type, Long id) {
        praiseMapper.deleteByTypeAndParentId(type,id);
    }


}
