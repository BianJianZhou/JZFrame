package com.bjz.jzappframe.widget.recycler.base;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.wy.viewFrame.util.Utils;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by 边江洲 on 2017/9/2.
 */
/*
 类 说 明：
 适用于组件
 参数描述：


*/
public abstract class JZBaseView<T> extends RelativeLayout implements Observer {
    public T data;

    String type = "";

    public Context context;

    public int screenWidth, screenHeight;

    public JZBaseView(Context context) {
        this(context, null);
    }

    public JZBaseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JZBaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        screenWidth = Utils.screenWidth(context);
        screenHeight = Utils.screenHeigth(context);
        onCreate(context, attrs, defStyleAttr);
    }

    protected abstract void onCreate(Context context, AttributeSet attrs, int defStyleAttr);

    public JZBaseView setData(T data) {
        this.data = data;
        initView(data);
        return this;
    }

    public JZBaseView setData(T data, List<T> datas, int position) {
        this.data = data;
        initView(data, datas, position);
        return this;
    }

    protected abstract void initView(T data);

    protected void initView(T data, List<T> datas, int position) {
    }

    public String getViewType() {
        return type;
    }

    public JZBaseView setViewType(String type) {
        this.type = type;
        return this;
    }

    /* 数据的刷新 */
    @Override
    public void update(Observable observable, Object o) {

    }

    public boolean isDestory = false;

    public void onDestory() {
        isDestory = true;
    }

    /**
     * 获取状态栏高度
     *
     * @return 状态栏高度
     */
    public int getStatusBarHeight() {
        int result = -1;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
