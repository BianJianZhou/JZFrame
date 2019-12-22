package com.bjz.jzappframe.event;

public class JZCallBackDataVo<T> {

    T data;

    public T getResult() {
        return data;
    }

    public JZCallBackDataVo<T> setData(T data) {
        this.data = data;
        return this;
    }
}
