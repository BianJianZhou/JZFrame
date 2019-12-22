package com.bjz.jzappframe.utils;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

/* 适配底部虚拟按键，待测试 */
public class AndroidVisualSpaceAdaptive {

    View
            dectorView;

    Context
            context;

    int beforeHeight;
    ViewGroup.LayoutParams params;

    public void AndroidVisualSpaceAdaptive(View dectorView, Context context) {
        this.dectorView = dectorView;
        this.context = context;
        registLayoutHeightChange();
    }

    private void registLayoutHeightChange() {
        params = dectorView.getLayoutParams();
        dectorView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
    }

    ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            AndroidVisualSpaceAdaptive.this.updateDectorView();
        }
    };

    private void updateDectorView() {
        int tempHeight = getShowViewBottm();
        if (beforeHeight != tempHeight) {
            params.height = tempHeight;
            dectorView.requestLayout();
            beforeHeight = tempHeight;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                dectorView.getViewTreeObserver().removeOnGlobalLayoutListener(globalLayoutListener);
            }
        }
    }

    /**
     * 计算视图可视高度
     *
     * @return
     */
    private int getShowViewBottm() {
        Rect r = new Rect();
        dectorView.getWindowVisibleDisplayFrame(r);
        return r.bottom;
    }

}
