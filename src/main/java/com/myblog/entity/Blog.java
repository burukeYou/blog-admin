package com.myblog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class Blog {

    private Long id;        //bigint(20) NOT NULL AUTO_INCREMENT,
    private Long user_id;        //bigint(20) NULL,
    private Integer category_id;        //int(50) NULL,

    private String title;        //varchar(50) NOT NULL,
    private String summary;        //varchar(200) NOT NULL,
    private String content;        //longtext NOT NULL,
    private String htmlContent;        //longtext NOT NULL,

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;        //timestamp NOT NULL,

    private Integer readSize;        //int(50) NULL,
    private Integer commentSize;        //int(50) NULL,
    private Integer praise_count;        //int(50) NULL,
    private String tags;        //varchar(255) NULL,
    private Integer status;        //int(10) NULL,
    private String blog_img;


    //==============================================



    private User user;

    private Category category;


    //===============================================


    public Blog() {
    }

    public Blog(String title, String summary, String content, String htmlContent) {
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.htmlContent = htmlContent;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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

    public Integer getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Integer category_id) {
        this.category_id = category_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getReadSize() {
        return readSize;
    }

    public void setReadSize(Integer readSize) {
        this.readSize = readSize;
    }

    public Integer getCommentSize() {
        return commentSize;
    }

    public void setCommentSize(Integer commentSize) {
        this.commentSize = commentSize;
    }

    public Integer getPraise_count() {
        return praise_count;
    }

    public void setPraise_count(Integer praise_count) {
        this.praise_count = praise_count;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getBlog_img() {
        return blog_img;
    }

    public void setBlog_img(String blog_img) {
        this.blog_img = blog_img;
    }

    @Override
    public String toString() {
        return "Blog{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", category_id=" + category_id +
                ", title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", content='" + content + '\'' +
                ", htmlContent='" + htmlContent + '\'' +
                ", createTime=" + createTime +
                ", readSize=" + readSize +
                ", commentSize=" + commentSize +
                ", praise_count=" + praise_count +
                ", tags='" + tags + '\'' +
                ", status=" + status +
                ", blog_img='" + blog_img + '\'' +
                ", user=" + user +
                ", category=" + category +
                '}';
    }
}
