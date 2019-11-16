package com.myblog.vo;

/**
 *      博客详情页显示点赞情况
 */
public class PraiseVo {

    private Integer id;

    private Integer praiseCount;

    private boolean isPraise;


    public PraiseVo(Integer id, Integer praiseCount, boolean isPraise) {
        this.id = id;
        this.praiseCount = praiseCount;
        this.isPraise = isPraise;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPraiseCount() {
        return praiseCount;
    }

    public void setPraiseCount(Integer praiseCount) {
        this.praiseCount = praiseCount;
    }

    public boolean isPraise() {
        return isPraise;
    }

    public void setPraise(boolean isPraise) {
        isPraise = isPraise;
    }
}
