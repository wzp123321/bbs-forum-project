package com.bbs.bbsadmin.response;

import lombok.Getter;

/**
 * @author zhcao
 * @version JDK 17
 * @date 2024/7/1
 * @description 统一响应数据格式
 */
@Getter
public class R<T> {
    /**
     * 响应码
     */
    private Integer code;
    /**
     * 响应信息
     */
    private String message;
    /**
     * 响应数据
     */
    private T data;

    private R(Integer code){
        this.code = code;
    }

    private R(Integer code, String message){
        this.code = code;
        this.message = message;
    }

    private R(Integer code, String message, T data){
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> R<T> success(){
        return new R<T>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> R<T> success(String message){
        return new R<T>(ResponseCode.SUCCESS.getCode(), message);
    }

    public static <T> R<T> data(T data){
        return new R<T>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), data);
    }

    public static <T> R<T> fail(){
        return new R<T>(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getMessage());
    }

    public static <T> R<T> fail(String message){
        return new R<T>(ResponseCode.ERROR.getCode(), message);
    }

    public static <T> R<T> fail(ResponseCode responseCode){
        return new R<T>(responseCode.getCode(), responseCode.getMessage());
    }

    public static <T> R<T> fail(Integer code, String message){
        return new R<T>(code, message);
    }
}