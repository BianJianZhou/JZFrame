package com.bjz.jzappframe.bean;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JZPageData {

    private int intentFlag;

    public int getIntentFlag() {
        return intentFlag;
    }

    public void setIntentFlag(int intentFlag) {
        this.intentFlag = intentFlag;
    }

    Map<String, Object> map = new HashMap<>();

    public Set<String> getKeys() {
        if (map != null && map.size() > 0) {
            return map.keySet();
        }
        return null;
    }

    public JZPageData add(String k, Object v) {
        if (!TextUtils.isEmpty(k) && v != null) {
            map.put(k, v);
        }
        return this;
    }

    public Object getV(String k) {
        if (!TextUtils.isEmpty(k) && map.containsKey(k)) {
            return map.get(k);
        }
        return null;
    }


}
