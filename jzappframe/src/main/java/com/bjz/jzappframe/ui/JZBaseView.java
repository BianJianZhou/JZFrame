package com.bjz.jzappframe.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

public abstract class JZBaseView<T> extends RelativeLayout {

    public T data;

    public View view;

    public Context mContext;

    public JZBaseView(Context context) {
        this(context, null);
    }

    public JZBaseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JZBaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        if (getLayoutRes() != 0) {
            view = LayoutInflater.from(context).inflate(getLayoutRes(), this, false);
        } else if (getCustomView() != null) {
            view = getCustomView();
        } else {
            new RuntimeException("please resturn getLayoutRes() or getCustomView()");
        }
        addView(view);
        bindView();
    }

    public void setData(T data) {
        initView(data);
    }

    public <viewT extends View> viewT bind(int id) {
        return (viewT) view.findViewById(id);
    }

    public abstract int getLayoutRes();

    public View getCustomView() {
        return null;
    }

    public abstract void bindView();

    public abstract void initView(T data);

}
