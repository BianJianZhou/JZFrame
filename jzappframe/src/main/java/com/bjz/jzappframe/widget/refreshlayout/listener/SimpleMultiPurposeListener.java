package com.bjz.jzappframe.widget.refreshlayout.listener;

import com.bjz.jzappframe.widget.refreshlayout.api.RefreshFooter;
import com.bjz.jzappframe.widget.refreshlayout.api.RefreshHeader;
import com.bjz.jzappframe.widget.refreshlayout.api.RefreshLayout;
import com.bjz.jzappframe.widget.refreshlayout.constant.RefreshState;

/**
 * 多功能监听器
 * Created by SCWANG on 2017/5/26.
 */

public class SimpleMultiPurposeListener implements OnMultiPurposeListener {

    @Override
    public void onHeaderPulling(RefreshHeader header, float percent, int offset, int headerHeight, int extendHeight) {

    }

    @Override
    public void onHeaderReleasing(RefreshHeader header, float percent, int offset, int footerHeight, int extendHeight) {

    }

    @Override
    public void onHeaderStartAnimator(RefreshHeader header, int footerHeight, int extendHeight) {

    }

    @Override
    public void onHeaderFinish(RefreshHeader header, boolean success) {

    }

    @Override
    public void onFooterPulling(RefreshFooter footer, float percent, int offset, int footerHeight, int extendHeight) {

    }

    @Override
    public void onFooterReleasing(RefreshFooter footer, float percent, int offset, int footerHeight, int extendHeight) {

    }

    @Override
    public void onFooterStartAnimator(RefreshFooter footer, int headHeight, int extendHeight) {

    }

    @Override
    public void onFooterFinish(RefreshFooter footer, boolean success) {

    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {

    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {

    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {

    }
}
