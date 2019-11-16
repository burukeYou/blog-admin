package com.myblog.vo;

/**
 *      富文本编辑器文件上传后返回的json对象
 *
 */
public class FileVo {

    private int success;        //0 表示上传失败，1 表示上传成功
    private String message; //"提示的信息，上传成功或上传失败及错误信息等。",
    private String url;     //"图片地址",上传成功时才返回

    private String blogToken;


    //==================================
    public String getBlogToken() {
        return blogToken;
    }

    public void setBlogToken(String blogToken) {
        this.blogToken = blogToken;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "FileVo{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", url='" + url + '\'' +
                ", blogToken='" + blogToken + '\'' +
                '}';
    }
}
