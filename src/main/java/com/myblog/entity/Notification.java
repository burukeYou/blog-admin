package com.myblog.entity;

import java.util.Date;

/**
 *      通知类
 *          谁 对 谁 做了什么事
 *
 */


public class Notification {


    private Long id;

    private String notifier;

    private String receiver;

    private int type; //消息类型      ---评论消息，点赞消息，关注消息，回复消息，收藏消息。。

    private Long typeId;      //做了什么xx 事    --点赞了你的xx，评论你的xx，回复你的xx

    private String typeName;


    private Date createtime;

    private int status;      //已读，未读

    //


    //============================
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNotifier() {
        return notifier;
    }

    public Notification setNotifier(String notifier) {
        this.notifier = notifier;
        return this;
    }

    public String getReceiver() {
        return receiver;
    }

    public Notification setReceiver(String receiver) {
        this.receiver = receiver;
        return this;
    }

    public int getType() {
        return type;
    }

    public Notification setType(int type) {
        this.type = type;
        return this;
    }

    public Long getTypeId() {
        return typeId;
    }

    public Notification setTypeId(Long typeId) {
        this.typeId = typeId;
        return this;
    }

    public String getTypeName() {
        return typeName;
    }

    public Notification setTypeName(String typeName) {
        this.typeName = typeName;
        return this;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public Notification setCreatetime(Date createtime) {
        this.createtime = createtime;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public Notification setStatus(int status) {
        this.status = status;
        return this;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", notifier='" + notifier + '\'' +
                ", receiver='" + receiver + '\'' +
                ", type=" + type +
                ", typeId=" + typeId +
                ", typeName='" + typeName + '\'' +
                ", createtime=" + createtime +
                ", status=" + status +
                '}';
    }
}
