package com.example.jzframedemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.bjz.jzappframe.widget.recycler.item.JZBaseItemView_FromView;

public class MyListItemView extends JZBaseItemView_FromView<MyData> {
    public MyListItemView(Context context) {
        super(context);
    }

    public MyListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected View getView() {
        TextView textView = new TextView(context);
        textView.setText("你好");
        textView.setTextColor(getResources().getColor(R.color._d53002));
        return textView;
    }

    @Override
    protected void onCreate(Context context, AttributeSet attrs, int defStyleAttr) {

    }

    @Override
    protected void initView(MyData data) {

    }
}
