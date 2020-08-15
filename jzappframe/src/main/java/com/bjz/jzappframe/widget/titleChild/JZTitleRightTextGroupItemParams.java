package com.bjz.jzappframe.widget.titleChild;

import com.bjz.jzappframe.JZUIFrameManager;

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

    private int color = JZUIFrameManager.getInstance().getViewsConfigBuilder().getTitleRightGroupTextColor();
    private int size = JZUIFrameManager.getInstance().getViewsConfigBuilder().getTitleRightGroupTextSize();
    private int itemTextPaddingLeft = JZUIFrameManager.getInstance().getViewsConfigBuilder().getTitleRightGroupItemSpace() / 2;
    private int itemTextPaddingTop = 0;
    private int itemTextPaddingRight = JZUIFrameManager.getInstance().getViewsConfigBuilder().getTitleRightGroupItemSpace() / 2;
    private int itemTextPaddingBottom = 0;

    public JZTitleRightTextGroupItemParams setItemTextPadding(int left, int top, int right, int bottom) {
        this.itemTextPaddingLeft = left;
        this.itemTextPaddingTop = top;
        this.itemTextPaddingRight = right;
        this.itemTextPaddingBottom = bottom;
        return this;
    }

    public int getItemTextPaddingLeft() {
        return itemTextPaddingLeft;
    }

    public int getItemTextPaddingTop() {
        return itemTextPaddingTop;
    }

    public int getItemTextPaddingRight() {
        return itemTextPaddingRight;
    }

    public int getItemTextPaddingBottom() {
        return itemTextPaddingBottom;
    }

    public int getColor() {
        return color;
    }

    public JZTitleRightTextGroupItemParams setColor(int color) {
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
