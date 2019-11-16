package com.myblog.exception;

/**
 *     异步请求处理结果错误类型
 *
 */

public enum AjaxErrorCode implements BaseErrorCode{

    SAVE_SUCCESS(200,"保存成功"),
    DELETE_SUCCESS(201,"删除成功"),
    PUBLIC_BLOG_SUCCESS(202,"发表博客成功"),
    ADD_CATEGORY_SUCCESS(203,"保存分类成功"),
    PRAISE_SUCCESS(204,"点赞成功"),


    DELETE_FAILURE(400,"删除失败"),
    SAVE_FAILURE(401,"保存失败"),
    PUBLIC_BLOG_FAILURE(402," 发表博客失败"),

    ADD_CATEGORY_FAILURE(403,"分类已存在! 不能重复添加"),
    PRAISE_FAILURE(404,"你已经点赞过了");

    private Integer code;
    private String message;

    AjaxErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
