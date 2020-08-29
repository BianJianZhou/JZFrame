package com.bjz.jzappframe.ui;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.bjz.jzappframe.R;
import com.bjz.jzappframe.utils.JZToast;

/**
 * Created by 边江洲 on 2017/10/10.
 */

public abstract class JZBasePopWindow extends PopupWindow {

    public Context context;

    /* 弹窗是否展示 */
    public boolean popIsCurrent = true;

    public View view;

    public JZBasePopWindow(Context context) {
        this(context, null);
    }

    public JZBasePopWindow(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JZBasePopWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        view = LayoutInflater.from(context).inflate(getLayoutResId(), null);
        setWidth(getPopWidth());
        if (getPopHeight() != 0) {
            setHeight(getPopHeight());
        } else {
            setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        setBackgroundDrawable(new ColorDrawable());
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        setAnimationStyle(R.style.Base_PopAnim_FromBottom);
        setContentView(view);
        initView();
        setClickListener();
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                popIsCurrent = false;
                if (popDissmissAfterListener != null) {
                    popDissmissAfterListener.dissmissAfter();
                }
            }
        });
    }

    public <viewT extends View> viewT bind(int id) {
        return (viewT) view.findViewById(id);
    }

    protected int getPopWidth() {
        return ViewGroup.LayoutParams.MATCH_PARENT;
    }

    protected abstract int getPopHeight();

    protected abstract int getLayoutResId();

    protected abstract void initView();

    protected abstract void setClickListener();

    PopDissmissAfterListener popDissmissAfterListener;

    public interface PopDissmissAfterListener {
        void dissmissAfter();
    }

    public JZBasePopWindow setPopDissmissAfterListener(PopDissmissAfterListener popDissmissAfterListener) {
        this.popDissmissAfterListener = popDissmissAfterListener;
        return this;
    }

    /**
     * 显示
     *
     * @param v 最外层组件
     */
    public void showPop(final View v) {
        showAtLocation(v, Gravity.NO_GRAVITY, 0, 0);
    }

    @Override
    public void showAsDropDown(View anchor) {
        if (Build.VERSION.SDK_INT >= 24) {
            Rect visibleFrame = new Rect();
            anchor.getGlobalVisibleRect(visibleFrame);
            int height = anchor.getResources().getDisplayMetrics().heightPixels - visibleFrame.bottom;
            setHeight(height);
        }
        super.showAsDropDown(anchor);
    }

    public TMBasePopAffrimClickListener listener;

    public JZBasePopWindow setTMBasePopAffrimClickListener(TMBasePopAffrimClickListener listener) {
        this.listener = listener;
        return this;
    }

    public interface TMBasePopAffrimClickListener {
        void affirm(String result1, String result2, String result3, String result4, String result5, String result6, String result7, JZBasePopWindow basePopWindow);
    }

    /**
     * 显示toast
     */
    public void showToast(String str) {
        JZToast.showShortToast(context, str);
    }

    public void hidePop() {
        if (isShowing()) {
            popIsCurrent = false;
            dismiss();
        }
    }

}
