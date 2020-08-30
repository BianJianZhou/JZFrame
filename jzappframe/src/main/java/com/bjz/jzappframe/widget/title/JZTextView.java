package com.bjz.jzappframe.widget.title;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * ==================================
 * Created by 边江洲 on 2018/4/20.
 * 作    者：WY_BJZ
 * 创建时间：2018/4/20
 * ==================================
 */
/*
 类 说 明：
 
 参数描述：
 
 
*/
@SuppressLint({"AppCompatCustomView"})
public class JZTextView extends TextView {

    public Context context;

    public JZTextView(Context context) {
        super(context);
        init(context);
    }

    public JZTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public JZTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        this.context = context;
    }


    @Override
    public void setTextSize(float size) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    public boolean isDestory = false;

    public void onDestory() {
        isDestory = true;
    }
}
