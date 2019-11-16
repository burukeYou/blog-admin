package com.myblog.service.impl;

import com.myblog.dao.NotificationMapper;
import com.myblog.entity.Notification;
import com.myblog.service.NotificationService;
import com.myblog.vo.MsgConditionVo;
import com.myblog.vo.PageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {


    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public void publicNotice(Notification notification) {
        notificationMapper.insertNotice(notification);
    }

    @Transactional
    @Override
    public PageBean<Notification> findUserAllMsgByCondition(MsgConditionVo msgConditionVo) {
        //
        int totalCount = notificationMapper.findUserAllMsgByConditionCount(msgConditionVo);
        int currentPage = msgConditionVo.getCurrentPage();

        //
        msgConditionVo.setCurrentPage((msgConditionVo.getCurrentPage()-1)*msgConditionVo.getPageSize());
        List<Notification> notificationList = notificationMapper.findAllMsgByCondition(msgConditionVo);

        //
        PageBean<Notification> pageBean = new PageBean<>(currentPage,totalCount,msgConditionVo.getPageSize());
        pageBean.setDataList(notificationList);

        return pageBean;
    }

    @Override
    public int findUSerTypeMsgCountByStatus(String nickname,Integer type,Integer status) {
        MsgConditionVo msgConditionVo = new MsgConditionVo();
        msgConditionVo.setNickanme(nickname);
        msgConditionVo.setType(type);
        msgConditionVo.setStatus(status);
        return notificationMapper.findUserAllMsgByConditionCount(msgConditionVo);
    }

    @Override
    public void deleteAllUserMsgByType(String nickname, Integer type) {
        notificationMapper.deleteUserMsgByCondition(nickname,type,null);
    }

    @Override
    public void deleteUserMsgByTypeAndId(String nickname, Integer type,Long id) {
        notificationMapper.deleteUserMsgByCondition(nickname,type,id);
    }

    @Override
    public void readMessage(Long id) {
        notificationMapper.updateReadStatus(id,1);
    }

    @Override
    public void readAllMessageByUserAndType(String nickname, int type) {
        notificationMapper.updateAllReadStatus(nickname,type,1);
    }


}
