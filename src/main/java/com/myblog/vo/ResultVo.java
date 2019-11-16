package com.myblog.vo;

import com.myblog.exception.BaseErrorCode;

/**
 *      异步请求返回的结果

 */
public class ResultVo<T> {

    private Integer code;
    private String message;
    private T data;

    public static ResultVo okOf(Integer code,String message) {
        ResultVo resultDTO = new ResultVo();
        resultDTO.setCode(code);
        resultDTO.setMessage(message);
        return resultDTO;
    }

    public static ResultVo errorOf(Integer code, String message) {
        ResultVo resultDTO = new ResultVo();
        resultDTO.setCode(code);
        resultDTO.setMessage(message);
        return resultDTO;
    }

    public static ResultVo resultOf(BaseErrorCode baseErrorCode) {
        ResultVo resultDTO = new ResultVo();
        resultDTO.setCode(baseErrorCode.getCode());
        resultDTO.setMessage(baseErrorCode.getMessage());
        return resultDTO;
    }

    public static <T> ResultVo resultOf(T t,BaseErrorCode baseErrorCode) {
        ResultVo resultDTO = new ResultVo();
        resultDTO.setCode(baseErrorCode.getCode());
        resultDTO.setMessage(baseErrorCode.getMessage());
        resultDTO.setData(t);
        return resultDTO;
    }



    //
    public static ResultVo okOf() {
        ResultVo resultDTO = new ResultVo();
        resultDTO.setCode(200);
        resultDTO.setMessage("请求成功");
        return resultDTO;
    }



    public static <T> ResultVo okOf(T t) {
        ResultVo resultDTO = new ResultVo();
        resultDTO.setCode(200);
        resultDTO.setMessage("请求成功");
        resultDTO.setData(t);
        return resultDTO;
    }



    //==============================================================


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResultVo{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
