package com.myblog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class Comment {

    private Long   id;             //bigint(20) NOT NULL,
    private Long   user_id;             //bigint(20) NOT NULL,
    private Long   blog_id;             //bigint(20) NOT NULL,
    private String   content;             //varchar(255) NOT NULL,

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;             //timestamp NOT NULL,

    private Integer   praise_count;             //int(50) NULL,


    private User user;


    //
    private boolean currentUserIspraise;
    private Integer currentPraiseId;


    //====================================


    public Integer getCurrentPraiseId() {
        return currentPraiseId;
    }

    public void setCurrentPraiseId(Integer currentPraiseId) {
        this.currentPraiseId = currentPraiseId;
    }

    public boolean isCurrentUserIspraise() {
        return currentUserIspraise;
    }

    public void setCurrentUserIspraise(boolean currentUserIspraise) {
        this.currentUserIspraise = currentUserIspraise;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Long getBlog_id() {
        return blog_id;
    }

    public void setBlog_id(Long blog_id) {
        this.blog_id = blog_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getPraise_count() {
        return praise_count;
    }

    public void setPraise_count(Integer praise_count) {
        this.praise_count = praise_count;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", blog_id=" + blog_id +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                ", praise_count=" + praise_count +
                ", user=" + user +
                ", currentUserIspraise=" + currentUserIspraise +
                ", currentPraiseId=" + currentPraiseId +
                '}';
    }
}
