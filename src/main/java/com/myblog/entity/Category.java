package com.myblog.entity;

public class Category {



    private Integer id;        //int(50) NOT NULL,


    private String categoryName;        //varchar(50) NOT NULL,


    private Long user_id;        //bigint(20) NOT NULL,



    //============================================
    //

    public Category() {
    }

    public Category(String categoryName, Long user_id) {
        this.categoryName = categoryName;
        this.user_id = user_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", categoryName='" + categoryName + '\'' +
                ", user_id=" + user_id +
                '}';
    }
}
