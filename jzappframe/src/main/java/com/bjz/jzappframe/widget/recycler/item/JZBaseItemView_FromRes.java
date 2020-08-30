package com.bjz.jzappframe.widget.recycler.item;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.bjz.jzappframe.widget.recycler.base.JZBaseView;

/**
 * Created by 边江洲 on 2017/8/29.
 */

/*
 * 注意事项：
 * 1：必须返回 布局id
 * 2：getType 前 必须先 setType 赋值
 * 3: 如果有组件赋值的过程必须 先调用 setData 传入 数据对象
  *
  * */
public abstract class JZBaseItemView_FromRes<T> extends JZBaseView<T> {

    View itemView;

    public JZBaseItemView_FromRes(Context context) {
        this(context, null);
    }

    public JZBaseItemView_FromRes(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JZBaseItemView_FromRes(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        itemView = LayoutInflater.from(context).inflate(getLayoutResId(), this, false);
        addView(itemView);
        bindView(itemView);
    }

    protected abstract int getLayoutResId();

    protected abstract void bindView(View itemView);

    public <viewT extends View> viewT bind(int id) {
        return (viewT) itemView.findViewById(id);
    }

}
