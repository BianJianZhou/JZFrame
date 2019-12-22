package com.bjz.jzappframe.bean;

import android.view.View;

import com.bjz.jzappframe.event.JZOneStatusCallBack;

public class JZLoadingBean {

    /*
    学习一下枚举
    默认
    提供的几种类型... 等等
    加载定义view
    ...
    后面再说
    * */
    String loadType;
    String msg;
    String clickButtonContent;
    JZOneStatusCallBack buttonClickListener;
    View view;

    public JZLoadingBean setClickButtonContent(String clickButtonContent) {
        this.clickButtonContent = clickButtonContent;
        return this;
    }

    public JZLoadingBean setLoadType(String loadType) {
        this.loadType = loadType;
        return this;
    }

    public JZLoadingBean setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    /* 泛型为回调的数据类型 */
    public <T> T registButtonClickListener(JZOneStatusCallBack<T> buttonClickListener) {
        this.buttonClickListener = buttonClickListener;
        return null;
    }

    /* 返回是否注册 */
    public boolean notifyButtonClick(Object result) {
        if (buttonClickListener == null) {
            return false;
        } else {
            buttonClickListener.result(result);
        }
        return true;
    }

    public JZLoadingBean setView(View view) {
        this.view = view;
        return this;
    }

    public String getLoadType() {
        return loadType;
    }

    public String getMsg() {
        return msg;
    }

    public String getClickButtonContent() {
        return clickButtonContent;
    }

    public View getView() {
        return view;
    }
}
