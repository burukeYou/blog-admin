package com.myblog.vo;

import java.util.List;

public class PageBean<T> {

    //总页数
    private Integer totalPageCount;

    //当前页
    private Integer currentPage;

    //每页显示数量
    private Integer pageSize = 6;

    //总个数
    private Integer totalDataCount;

    //分页数据
    private List<T> dataList;

    //------------------------------------------------------------------
    public PageBean() {

    }

    public PageBean(Integer currentPage,Integer totalDataCount,Integer pageSize){
        this.currentPage = currentPage;
        this.totalDataCount = totalDataCount;
        this.pageSize = pageSize;

        //计算总页数      --需向上取整      --10 / 3 -->4页
        this.totalPageCount = (totalDataCount + pageSize-1)/pageSize;


        //判断当前页是否合法
        if(this.currentPage<1){
            this.currentPage = 1;
        }else if(this.currentPage > this.totalPageCount){
            this.currentPage = this.totalPageCount;
        }
    }









    //===================================================================


    public Integer getTotalPageCount() {
        return totalPageCount;
    }

    public void setTotalPageCount(Integer totalPageCount) {
        this.totalPageCount = totalPageCount;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalDataCount() {
        return totalDataCount;
    }

    public void setTotalDataCount(Integer totalDataCount) {
        this.totalDataCount = totalDataCount;
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
    }

    @Override
    public String toString() {
        return "PageBean{" +
                "totalPageCount=" + totalPageCount +
                ", currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                ", totalDataCount=" + totalDataCount +
                ", dataList=" + dataList +
                '}';
    }
}
