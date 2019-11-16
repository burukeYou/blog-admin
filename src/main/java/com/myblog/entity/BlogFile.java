package com.myblog.entity;


import javax.persistence.*;

@Entity
public class BlogFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long blogId;   //属于那一篇博客

    @Column(nullable = false)
    private String url;        //存储的路径;


    protected BlogFile(){

    }

    public BlogFile(Long blogId, String url) {
        this.blogId = blogId;
        this.url = url;
    }


    public Long getId() {
        return id;
    }

    public BlogFile setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getBlogId() {
        return blogId;
    }

    public void setBlogId(Long blogId) {
        this.blogId = blogId;
    }

    public String getUrl() {
        return url;
    }

    public BlogFile setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public String toString() {
        return "BlogFile{" +
                "id=" + id +
                ", blogId=" + blogId +
                ", url='" + url + '\'' +
                '}';
    }
}
