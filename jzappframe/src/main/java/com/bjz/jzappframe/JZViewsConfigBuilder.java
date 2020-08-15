package com.bjz.jzappframe;

import android.graphics.Bitmap;

import com.bjz.jzappframe.utils.JZRuntimeException;

public class JZViewsConfigBuilder {

    protected JZViewsConfigBuilder() {
    }

    /* titleView配置 */
    /* titleView高度 */
    private int titleViewH;

    /* 返回按键的宽 */
    private int backBtnW;

    /* 返回按键的高 */
    private int backBtnH;

    /* 返回按键的图片bitmap */
    private Bitmap backBtnBitmap;

    /* 返回按键距离左侧距离 */
    private int backBtnMarginLeft;

    /* 差号图标宽度 */
    private int xIconW;

    /* 差号图标高度 */
    private int xIconH;

    /* 差号图标据左侧距离 */
    private int xIconMarginLeft;

    /* 差号图标bitmap */
    private Bitmap xIconBitmap;

    /* 标题文字大小 */
    private int titleTextSize;

    /* 标题文字色值(x0ffffff) */
    private int titleTextColor;

    /* 右侧容器item间距 */
    private int titleRightGroupItemSpace;

    /* 右侧容器item文字大小 */
    private int titleRightGroupTextSize;

    /* 右侧容器item文字色值(x0ffffff) */
    private int titleRightGroupTextColor;

    /* 右测容器距右距离 */
    private int titleRightGroupMarginRight;

    /* title底部横线view高度 */
    private int titleBottomHorLineViewH;

    /* title底部横线view颜色 */
    private int titleBottomHorLineColor;

    /* 主界面配置 */
    /* 侧滑退出，手指点击左侧区域生效范围（宽度） */
    private int scrollBackLeftW;

    /* 自定义toast高度 */
    private int customToastHeight;

    public JZViewsConfigBuilder setCustomToastHeight(int customToastHeight) {
        this.customToastHeight = customToastHeight;
        return this;
    }

    public JZViewsConfigBuilder setScrollBackLeftW(int scrollBackLeftW) {
        this.scrollBackLeftW = scrollBackLeftW;
        return this;
    }

    public JZViewsConfigBuilder setTitleBottomHorLineViewH(int bottomHorLineView) {
        this.titleBottomHorLineViewH = bottomHorLineView;
        return this;
    }

    public JZViewsConfigBuilder setTitleBottomHorLineColor(int titleBottomHorLineColor) {
        this.titleBottomHorLineColor = titleBottomHorLineColor;
        return this;
    }

    public JZViewsConfigBuilder setTitleViewH(int titleViewH) {
        this.titleViewH = titleViewH;
        return this;
    }

    public JZViewsConfigBuilder setBackBtnW(int backBtnW) {
        this.backBtnW = backBtnW;
        return this;
    }

    public JZViewsConfigBuilder setBackBtnH(int backBtnH) {
        this.backBtnH = backBtnH;
        return this;
    }

    public JZViewsConfigBuilder setBackBtnBitmap(Bitmap btnBitmap) {
        this.backBtnBitmap = btnBitmap;
        return this;
    }

    public JZViewsConfigBuilder setBackBtnMarginLeft(int backBtnMarginLeft) {
        this.backBtnMarginLeft = backBtnMarginLeft;
        return this;
    }

    public JZViewsConfigBuilder setxIconW(int xIconW) {
        this.xIconW = xIconW;
        return this;
    }

    public JZViewsConfigBuilder setxIconH(int xIconH) {
        this.xIconH = xIconH;
        return this;
    }

    public JZViewsConfigBuilder setxIconMarginLeft(int xIconMarginLeft) {
        this.xIconMarginLeft = xIconMarginLeft;
        return this;
    }

    public JZViewsConfigBuilder setxIconBitmap(Bitmap xIconBitmap) {
        this.xIconBitmap = xIconBitmap;
        return this;
    }

    public JZViewsConfigBuilder setTitleTextSize(int titleTextSize) {
        this.titleTextSize = titleTextSize;
        return this;
    }

    public JZViewsConfigBuilder setTitleTextColor(int titleTextColor) {
        this.titleTextColor = titleTextColor;
        return this;
    }

    public JZViewsConfigBuilder setTitleRightGroupItemSpace(int rightGroupItemSpace) {
        this.titleRightGroupItemSpace = rightGroupItemSpace;
        return this;
    }

    public JZViewsConfigBuilder setTitleRightGroupTextSize(int rightGroupTextSize) {
        this.titleRightGroupTextSize = rightGroupTextSize;
        return this;
    }

    public JZViewsConfigBuilder setTitleRightGroupTextColor(int rightGroupTextColor) {
        this.titleRightGroupTextColor = rightGroupTextColor;
        return this;
    }

    public JZViewsConfigBuilder setTitleRightGroupMarginRight(int rightGroupMarginRight) {
        this.titleRightGroupMarginRight = rightGroupMarginRight;
        return this;
    }

    public int getTitleViewH() {
        if (titleViewH == 0) {
            throw new JZRuntimeException("please set titleViewH");
        }
        return titleViewH;
    }

    public int getBackBtnW() {
        if (backBtnW == 0) {
            throw new JZRuntimeException("please set backBtnW");
        }
        return backBtnW;
    }

    public int getBackBtnH() {
        if (backBtnH == 0) {
            throw new JZRuntimeException("please set backBtnH");
        }
        return backBtnH;
    }

    public Bitmap getBackBtnBitmap() {
        if (backBtnBitmap == null) {
            throw new JZRuntimeException("please set backBtnBitmap");
        }
        return backBtnBitmap;
    }

    public int getBackBtnMarginLeft() {
        if (backBtnMarginLeft == 0) {
            throw new JZRuntimeException("please set backBtnMarginLeft");
        }
        return backBtnMarginLeft;
    }

    public int getxIconW() {
        if (xIconW == 0) {
            throw new JZRuntimeException("please set xIconW");
        }
        return xIconW;
    }

    public int getxIconH() {
        if (xIconH == 0) {
            throw new JZRuntimeException("please set xIconH");
        }
        return xIconH;
    }

    public int getxIconMarginLeft() {
        if (xIconMarginLeft == 0) {
            throw new JZRuntimeException("please set xIconMarginLeft");
        }
        return xIconMarginLeft;
    }

    public Bitmap getxIconBitmap() {
        if (xIconBitmap == null) {
            throw new JZRuntimeException("please set xIconBitmap");
        }
        return xIconBitmap;
    }

    public int getTitleTextSize() {
        if (titleTextSize == 0) {
            throw new JZRuntimeException("please set titleTextSize");
        }
        return titleTextSize;
    }

    public int getTitleTextColor() {
        if (titleTextColor == 0) {
            throw new JZRuntimeException("please set titleTextColor");
        }
        return titleTextColor;
    }

    public int getTitleRightGroupItemSpace() {
        if (titleRightGroupItemSpace == 0) {
            throw new JZRuntimeException("please set titleRightGroupItemSpace");
        }
        return titleRightGroupItemSpace;
    }

    public int getTitleRightGroupTextSize() {
        if (titleRightGroupTextSize == 0) {
            throw new JZRuntimeException("please set titleRightGroupTextSize");
        }
        return titleRightGroupTextSize;
    }

    public int getTitleRightGroupTextColor() {
        if (titleRightGroupTextColor == 0) {
            throw new JZRuntimeException("please set titleRightGroupTextColor");
        }
        return titleRightGroupTextColor;
    }

    public int getTitleRightGroupMarginRight() {
        if (titleRightGroupMarginRight == 0) {
            throw new JZRuntimeException("please set titleRightGroupMarginRight");
        }
        return titleRightGroupMarginRight;
    }

    public int getScrollBackLeftW() {
        if (scrollBackLeftW == 0) {
            throw new JZRuntimeException("please set scrollBackLeftW");
        }
        return scrollBackLeftW;
    }

    public int getTitleBottomHorLineViewH() {
        if (titleBottomHorLineViewH == 0) {
            throw new JZRuntimeException("please set titleBottomHorLineViewH");
        }
        return titleBottomHorLineViewH;
    }

    public int getTitleBottomHorLineColor() {
        if (titleBottomHorLineColor == 0) {
            throw new JZRuntimeException("please set getTitleBottomHorLineColor");
        }
        return titleBottomHorLineColor;
    }

    public int getCustomToastHeight() {
        if (customToastHeight == 0) {
            throw new JZRuntimeException("please set customToastHeight");
        }
        return customToastHeight;
    }
}
