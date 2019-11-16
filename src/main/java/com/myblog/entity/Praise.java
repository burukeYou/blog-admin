package com.myblog.entity;

/**
 *     点赞
 *
 */

public class Praise {

    private Integer id;
    private Long user_id;

    private Integer parentType;  //什么类型的点赞

    private Long parentId;   //该类型点赞下的实体id

    private Long parenOwnerId;   //该实体的拥有者

    //===========================================
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Integer getParentType() {
        return parentType;
    }

    public void setParentType(Integer parentType) {
        this.parentType = parentType;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getParenOwnerId() {
        return parenOwnerId;
    }

    public void setParenOwnerId(Long parenOwnerId) {
        this.parenOwnerId = parenOwnerId;
    }
}
