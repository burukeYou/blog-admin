package com.myblog.vo;


/**
        博客搜索查询参数

 SELECT* FROM blog where title LIKE "%2%" OR summary LIKE "%2%" OR tags LIKE "%2%" OR content LIKE "%2%" AND user_id = 4 AND category_id = 4 ORDER BY createTime DESC

 */
public class QueryConditionVo {

    private Long user_id;

    private String keyword;

    private String order  = "new";    //默认最新查询

    private Integer categoryId;

    private int currentPage = 1;    //默认第一页

    private int pageSize = 6;      //默认每页显示六个


    //===========================================================================


    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        if (order.equals(""))
            order = "new";
        else
             this.order = order;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "QueryConditionVo{" +
                "user_id=" + user_id +
                ", keyword='" + keyword + '\'' +
                ", order='" + order + '\'' +
                ", categoryId=" + categoryId +
                ", currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                '}';
    }
}
