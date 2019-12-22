package com.bjz.jzappframe.event;

import com.bjz.jzappframe.utils.JZLog;

import java.util.HashMap;
import java.util.Map;

/* 单对单 */
public class JZSingleCallBack {

    private static final String TAG = "JZSingleCallBack";

    private static Map<String, JZOneStatusCallBack> eventSaveMap = new HashMap<>();

    public JZSingleCallBack(String verifyCode) {
        if (!verifyCode.equals("fromJZEvents")) {
            new RuntimeException("please use JZCallBack , Private use of this class is not allowed");
        }
    }

    /* 这里的泛型是数据类型 */
    public <T> T regist(String eventName, JZOneStatusCallBack<JZCallBackDataVo<T>> jzOneStatusCallBack) {
        if (eventSaveMap.containsKey(eventName)) {
            JZLog.e(TAG, "the eventName: " + eventName + " Has been registered");
            return null;
        }
        eventSaveMap.put(eventName, jzOneStatusCallBack);
        return null;
    }

    public void send(String eventName, JZCallBackDataVo jzEventVo) {
        if (!eventSaveMap.containsKey(eventName)) {
            JZLog.e(TAG, "your send eventName: " + eventName + " not find");
            return;
        }
        eventSaveMap.get(eventName).result(jzEventVo);
    }

    public void remove(String eventName) {
        if (eventSaveMap.containsKey(eventName)) {
            eventSaveMap.remove(eventName);
            JZLog.d(TAG, "remove() | remove success, eventName: " + eventName);
        }
    }
}
