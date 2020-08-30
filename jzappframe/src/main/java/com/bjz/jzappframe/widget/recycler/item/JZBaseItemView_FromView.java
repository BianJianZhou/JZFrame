package com.bjz.jzappframe.widget.recycler.item;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.bjz.jzappframe.widget.recycler.base.JZBaseView;

/**
 * Created by 边江洲 on 2017/9/2.
 */

public abstract class JZBaseItemView_FromView<T> extends JZBaseView<T> {
    public JZBaseItemView_FromView(Context context) {
        this(context, null);
    }

    public JZBaseItemView_FromView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JZBaseItemView_FromView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addView(getView());
    }

    protected abstract View getView();

}
