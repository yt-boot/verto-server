package com.verto.common.api.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 统一返回结果类
 * @author verto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> {
    
    /**
     * 成功标志
     */
    private boolean success = true;
    
    /**
     * 返回处理消息
     */
    private String message = "操作成功！";
    
    /**
     * 返回代码
     */
    private Integer code = 0;
    
    /**
     * 返回数据对象 data
     */
    private T result;
    
    /**
     * 时间戳
     */
    private long timestamp = System.currentTimeMillis();
    
    public Result() {
    }
    
    public Result<T> success(String message) {
        this.message = message;
        this.code = 200;
        this.success = true;
        return this;
    }
    
    public static <T> Result<T> OK() {
        Result<T> r = new Result<T>();
        r.setSuccess(true);
        r.setCode(200);
        r.setMessage("成功");
        return r;
    }
    
    public static <T> Result<T> OK(T data) {
        Result<T> r = new Result<T>();
        r.setSuccess(true);
        r.setCode(200);
        r.setResult(data);
        r.setMessage("成功");
        return r;
    }
    
    public static <T> Result<T> OK(String msg, T data) {
        Result<T> r = new Result<T>();
        r.setSuccess(true);
        r.setCode(200);
        r.setResult(data);
        r.setMessage(msg);
        return r;
    }
    
    public static <T> Result<T> error(String msg, T data) {
        Result<T> r = new Result<T>();
        r.setSuccess(false);
        r.setCode(500);
        r.setResult(data);
        r.setMessage(msg);
        return r;
    }
    
    public static <T> Result<T> error(String msg) {
        return error(500, msg);
    }
    
    public static <T> Result<T> error(int code, String msg) {
        Result<T> r = new Result<T>();
        r.setCode(code);
        r.setMessage(msg);
        r.setSuccess(false);
        return r;
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    public T getResult() {
        return result;
    }
    
    public void setResult(T result) {
        this.result = result;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}