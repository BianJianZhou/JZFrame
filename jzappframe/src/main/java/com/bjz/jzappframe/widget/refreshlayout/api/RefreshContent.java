package com.bjz.jzappframe.widget.refreshlayout.api;

import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

/**
 * 刷新内容组件
 * Created by SCWANG on 2017/5/26.
 */

public interface RefreshContent {
    void moveSpinner(int spinner);
    boolean canScrollUp();
    boolean canScrollDown();
    int getMeasuredWidth();
    int getMeasuredHeight();
    void measure(int widthSpec, int heightSpec);
    void layout(int left, int top, int right, int bottom);

    View getView();
    View getScrollableView();
    ViewGroup.LayoutParams getLayoutParams();

    void onActionDown(MotionEvent e);
    void onActionUpOrCancel();

    void setupComponent(RefreshKernel kernel, View fixedHeader, View fixedFooter);
    void onInitialHeaderAndFooter(int headerHeight, int footerHeight);
    void setRefreshScrollBoundary(RefreshScrollBoundary boundary);

    boolean isNestedScrollingChild(MotionEvent e);

    AnimatorUpdateListener onLoadingFinish(RefreshKernel layout, int footerHeight, int startDelay, Interpolator reboundInterpolator, int reboundDuration);

}
