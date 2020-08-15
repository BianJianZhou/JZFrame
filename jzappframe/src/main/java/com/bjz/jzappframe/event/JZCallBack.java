package com.bjz.jzappframe.event;

import com.bjz.jzappframe.utils.JZLog;

/**
 * 仅适用于 同进程间通信
 * 使用方式：调用，然后设置监听即可
 */
public class JZCallBack {

    private final String TAG = "JZCallBack";
    private static JZSingleCallBack jzSingleCallBack = new JZSingleCallBack();
    private static JZPolymorphismCallBack jzPolymorphismCallBack = new JZPolymorphismCallBack();

    private JZCallBack() {
    }

    /* 调用案例 */
    private void forExample() {
        /* 单对单用法 */
        /* 注册 */
        JZCallBack.single().regist("eventName", (JZOneStatusCallBack<JZCallBackDataVo<String>>) result -> {
            String str = result.getResult();
            JZLog.d(TAG, "single test: " + str);
        });
        /* 发送 */
        JZCallBack.single().send("eventName", new JZCallBackDataVo().setData("singleTestContent"));

        /* 但对多用法 */
        /* 注册1 */
        JZCallBack.polmorphism().regist("eventName", "callbackName1", (JZOneStatusCallBack<JZCallBackDataVo<String>>) result -> {
            String str = result.getResult();
            JZLog.d(TAG, "polmorphism test1: " + str);
        });
        /* 注册2 */
        JZCallBack.polmorphism().regist("eventName", "callbackName2", (JZOneStatusCallBack<JZCallBackDataVo<String>>) result -> {
            String str = result.getResult();
            JZLog.d(TAG, "polmorphism test2: " + str);
        });
        /* 发送 */
        JZCallBack.polmorphism().send("eventName", new JZCallBackDataVo().setData("polmorphismTestContent"));
    }

    public static JZSingleCallBack single() {
        return jzSingleCallBack;
    }

    public static JZPolymorphismCallBack polmorphism() {
        return jzPolymorphismCallBack;
    }
}
