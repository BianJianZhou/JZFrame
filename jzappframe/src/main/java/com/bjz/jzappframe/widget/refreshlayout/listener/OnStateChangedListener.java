package com.bjz.jzappframe.widget.refreshlayout.listener;

import com.bjz.jzappframe.widget.refreshlayout.api.RefreshLayout;
import com.bjz.jzappframe.widget.refreshlayout.constant.RefreshState;

/**
 * 刷新状态改变监听器
 * Created by SCWANG on 2017/5/26.
 */

public interface OnStateChangedListener {
    /**
     * 状态改变事件 {@link RefreshState}
     *
     * @param refreshLayout RefreshLayout
     * @param oldState      改变之前的状态
     * @param newState      改变之后的状态
     */
    void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState);
}
