package com.bjz.jzappframe.widget.refreshlayout.impl;

import android.view.MotionEvent;
import android.view.View;

import com.bjz.jzappframe.widget.refreshlayout.api.RefreshScrollBoundary;

import static com.bjz.jzappframe.widget.refreshlayout.util.ScrollBoundaryUtil.canScrollDown;
import static com.bjz.jzappframe.widget.refreshlayout.util.ScrollBoundaryUtil.canScrollUp;

/**
 * 滚动边界
 * Created by SCWANG on 2017/7/8.
 */

public class RefreshScrollBoundaryAdapter implements RefreshScrollBoundary {

    //<editor-fold desc="Internal">
    MotionEvent mActionEvent;
    RefreshScrollBoundary boundary;

    void setRefreshScrollBoundary(RefreshScrollBoundary boundary) {
        this.boundary = boundary;
    }

    void setActionEvent(MotionEvent event) {
        mActionEvent = event;
    }
    //</editor-fold>

    //<editor-fold desc="RefreshScrollBoundary">
    @Override
    public boolean canPullDown(View content) {
        if (boundary != null) {
            return boundary.canPullDown(content);
        }
        return canScrollUp(content, mActionEvent);
    }

    @Override
    public boolean canPullUp(View content) {
        if (boundary != null) {
            return boundary.canPullUp(content);
        }
        return canScrollDown(content, mActionEvent);
    }
    //</editor-fold>
}
