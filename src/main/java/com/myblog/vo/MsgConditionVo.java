package com.myblog.vo;

public class MsgConditionVo {


    private String nickanme;    //消息所属用户

    private Integer type; //消息类型

    private int currentPage = 1;    //默认第一页

    private int pageSize = 6;      //默认每页显示六个

    private Integer status;  //消息状态


    //
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getNickanme() {
        return nickanme;
    }

    public void setNickanme(String nickanme) {
        this.nickanme = nickanme;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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
        return "MsgConditionVo{" +
                "nickanme='" + nickanme + '\'' +
                ", type=" + type +
                ", currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                ", status=" + status +
                '}';
    }

}
