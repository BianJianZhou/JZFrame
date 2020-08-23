package com.bjz.jzappframe.event;

import com.bjz.jzappframe.utils.JZLog;

import java.util.HashMap;
import java.util.Map;

/**
 * 一对多
 */
public class JZPolymorphismCallBack {
    private static final String TAG = "JZPolymorphismCallBack";

    private static Map<String, Map<String, JZOneStatusCallBack>> eventSaveMap = new HashMap<>();

    protected JZPolymorphismCallBack() {
    }


    /**
     * 这里的泛型是数据类型
     *
     * @param eventName           事件名称
     * @param callBackName        回调名称，用于过滤防止同一回调被添加多次
     * @param jzOneStatusCallBack 接收事件的回调
     * @param <T>                 数据类型
     * @return 不做实际意义
     */
    public <T> T regist(String eventName, String callBackName, JZOneStatusCallBack<JZCallBackDataVo<T>> jzOneStatusCallBack) {
        if (eventSaveMap.containsKey(eventName)) {
            eventSaveMap.get(eventName).put(callBackName, jzOneStatusCallBack);
        } else {
            Map<String, JZOneStatusCallBack> itemMap = new HashMap<>();
            itemMap.put(callBackName, jzOneStatusCallBack);
            eventSaveMap.put(eventName, itemMap);
        }
        return null;
    }

    public void send(String eventName, JZCallBackDataVo jzEventVo) {
        if (!eventSaveMap.containsKey(eventName)) {
            JZLog.e(TAG, "your send eventName: " + eventName + " not find");
            return;
        }
        Map<String, JZOneStatusCallBack> itemMap = eventSaveMap.get(eventName);
        for (String key : itemMap.keySet()) {
            if (itemMap.get(key) != null) {
                itemMap.get(key).result(jzEventVo);
            } else {
                throw new RuntimeException("your eventName corresponding callback have null,key: " + key);
            }
        }
    }

    /**
     * 移除事件
     *
     * @param eventName
     */
    public void removeEvent(String eventName) {
        if (eventSaveMap.containsKey(eventName)) {
            eventSaveMap.remove(eventName);
            JZLog.d(TAG, "remove() | remove success, eventName: " + eventName);
        }
    }

    /**
     * 移除某一事件对应的callback
     *
     * @param eventName
     * @param callbackName
     */
    public void removeEventCallBack(String eventName, String callbackName) {
        if (eventSaveMap.containsKey(eventName) && eventSaveMap.get(eventName).containsKey(callbackName)) {
            eventSaveMap.get(eventName).remove(callbackName);
            JZLog.d(TAG, "remove() | remove success, eventName: " + eventName + ", | callbackName: " + callbackName);
        }
    }
}
