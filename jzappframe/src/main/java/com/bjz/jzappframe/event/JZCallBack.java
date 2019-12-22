package com.bjz.jzappframe.event;

import java.util.HashMap;
import java.util.Map;

/* 使用方式：传入模块名称，然后设置监听即可 */
public class JZCallBack {

    private static Map<String, JZSingleCallBack> singleEvensMap = new HashMap<>();

    private JZCallBack() {
    }

    public static JZSingleCallBack single(String moduleName) {
        if (!singleEvensMap.containsKey(moduleName)) {
            singleEvensMap.put(moduleName, new JZSingleCallBack("fromJZEvents"));
        }
        return singleEvensMap.get(moduleName);
    }

}
