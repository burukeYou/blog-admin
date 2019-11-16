package com.myblog.exception;

/**
 *  通用错误类型枚举
 *
 */

public enum GeneralErrorCode implements BaseErrorCode {

    BLOG_NOT_FOUND(800,"博客不存在，已被删除"),
    SYSTEM_ERROR(801,"服务器繁忙，请稍后再试"),
    LOGIN_LIMIT(802,"登陆失败次数过多，已被限制登陆，剩余时间为：");


    private Integer code;
    private String message;

    GeneralErrorCode(Integer code, String message) {
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
