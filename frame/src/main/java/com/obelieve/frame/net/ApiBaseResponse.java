package com.obelieve.frame.net;


import com.obelieve.frame.utils.proguard.UnProguard;

/**
 * 网络返回基类 支持泛型
 */
public class ApiBaseResponse<T> implements UnProguard {

    private int toast;
    private int window;
    private int code;
    private String msg;
    private String data;
    private T entity;

    public ApiBaseResponse() {
    }

    public int getToast() {
        return toast;
    }

    public void setToast(int toast) {
        this.toast = toast;
    }

    public int getWindow() {
        return window;
    }

    public void setWindow(int window) {
        this.window = window;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }
}
