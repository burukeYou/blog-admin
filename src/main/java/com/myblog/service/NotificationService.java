package com.myblog.service;

import com.myblog.entity.Notification;
import com.myblog.vo.MsgConditionVo;
import com.myblog.vo.PageBean;

public interface NotificationService {


    void publicNotice(Notification notification);


    PageBean<Notification> findUserAllMsgByCondition(MsgConditionVo msgConditionVo);

    int findUSerTypeMsgCountByStatus(String nickname,Integer type,Integer status);

    void deleteAllUserMsgByType(String nickname, Integer type);

    void deleteUserMsgByTypeAndId(String nickname, Integer type,Long id);

    void readMessage(Long id);

    void readAllMessageByUserAndType(String nickname, int res);
}
