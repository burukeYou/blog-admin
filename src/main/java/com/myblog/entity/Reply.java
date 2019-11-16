package com.myblog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class Reply {

    private Long id;               //bigint(20) NOT NULL,

    private Long comment_id;               //bigint(20) NOT NULL,
    private String content;               //varchar(255) NOT NULL,
    private int parse_count;               //int(50) NULL,

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createtime;               //timestamp NOT NULL,

    private String repliedByOtherName;               //varchar(50) NOT NULL,
    private String replyToOtherName;   //即此回复所属的用户别名            //varchar(50) NOT NULL,

    private Long ownerId;


    //
    private Integer currentPraiseId;
    private boolean isPraise;  //当前用户是否点赞

   //=============================================================


    public Reply() {
    }

    public Integer getCurrentPraiseId() {
        return currentPraiseId;
    }

    public void setCurrentPraiseId(Integer currentPraiseId) {
        this.currentPraiseId = currentPraiseId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getComment_id() {
        return comment_id;
    }

    public void setComment_id(Long comment_id) {
        this.comment_id = comment_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getParse_count() {
        return parse_count;
    }

    public void setParse_count(int parse_count) {
        this.parse_count = parse_count;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getRepliedByOtherName() {
        return repliedByOtherName;
    }

    public void setRepliedByOtherName(String repliedByOtherName) {
        this.repliedByOtherName = repliedByOtherName;
    }

    public String getReplyToOtherName() {
        return replyToOtherName;
    }

    public void setReplyToOtherName(String replyToOtherName) {
        this.replyToOtherName = replyToOtherName;
    }

    public boolean isPraise() {
        return isPraise;
    }

    public void setPraise(boolean praise) {
        isPraise = praise;
    }

    @Override
    public String toString() {
        return "Reply{" +
                "id=" + id +
                ", comment_id=" + comment_id +
                ", content='" + content + '\'' +
                ", parse_count=" + parse_count +
                ", createtime=" + createtime +
                ", repliedByOtherName='" + repliedByOtherName + '\'' +
                ", replyToOtherName='" + replyToOtherName + '\'' +
                ", ownerId=" + ownerId +
                ", currentPraiseId=" + currentPraiseId +
                ", isPraise=" + isPraise +
                '}';
    }
}
