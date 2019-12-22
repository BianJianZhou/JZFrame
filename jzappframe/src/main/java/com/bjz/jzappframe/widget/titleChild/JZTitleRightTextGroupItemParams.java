package com.bjz.jzappframe.widget.titleChild;

import android.content.Context;

import com.bjz.jzappframe.R;

/**
 * ==================================
 * Created by 边江洲 on 2018/6/25.
 * 作    者：WY_BJZ
 * 创建时间：2018/6/25
 * ==================================
 */
/*
 类 说 明：
 
 参数描述：
 
 
*/
public class JZTitleRightTextGroupItemParams {

    public JZTitleRightTextGroupItemParams(Context context) {
        size = (int) context.getResources().getDimension(R.dimen.dimens_28);
    }

    private String color = "#999999";
    private int size;

    public String getColor() {
        return color;
    }

    public JZTitleRightTextGroupItemParams setColor(String color) {
        this.color = color;
        return this;
    }

    public int getSize() {
        return size;
    }

    public JZTitleRightTextGroupItemParams setSize(int size) {
        this.size = size;
        return this;
    }
}
