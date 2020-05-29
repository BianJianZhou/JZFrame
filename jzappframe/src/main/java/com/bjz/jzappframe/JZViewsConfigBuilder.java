package com.bjz.jzappframe;

import android.graphics.Bitmap;

import com.bjz.jzappframe.utils.JZCons;
import com.bjz.jzappframe.utils.JZRuntimeException;

public class JZViewsConfigBuilder {

    public JZViewsConfigBuilder(String verifTag) {
        if (!verifTag.equals(JZCons.VERIF_STR)) {
            throw new JZRuntimeException("please use JZUIFrameManager.getInstance().getViewsConfigBuilder()");
        }
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
    private int rightGroupItemSpace;

    /* 右侧容器item文字大小 */
    private int rightGroupTextSize;

    /* 右侧容器item文字色值(x0ffffff) */
    private int rightGroupTextColor;

    /* 右测容器距右距离 */
    private int rightGroupMarginRight;

    /* title底部横线view */
    private int bottomHorLineView;

    /* 主界面配置 */
    /* 侧滑退出，手指点击左侧区域生效范围（宽度） */
    private int scrollBackLeftW;

    /* 状态栏高度 */
    private int statusBarH;

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

    public JZViewsConfigBuilder setBottomHorLineView(int bottomHorLineView) {
        this.bottomHorLineView = bottomHorLineView;
        return this;
    }

    public JZViewsConfigBuilder setStatusBarH(int statusBarH) {
        this.statusBarH = statusBarH;
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

    public JZViewsConfigBuilder setRightGroupItemSpace(int rightGroupItemSpace) {
        this.rightGroupItemSpace = rightGroupItemSpace;
        return this;
    }

    public JZViewsConfigBuilder setRightGroupTextSize(int rightGroupTextSize) {
        this.rightGroupTextSize = rightGroupTextSize;
        return this;
    }

    public JZViewsConfigBuilder setRightGroupTextColor(int rightGroupTextColor) {
        this.rightGroupTextColor = rightGroupTextColor;
        return this;
    }

    public JZViewsConfigBuilder setRightGroupMarginRight(int rightGroupMarginRight) {
        this.rightGroupMarginRight = rightGroupMarginRight;
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

    public int getRightGroupItemSpace() {
        if (rightGroupItemSpace == 0) {
            throw new JZRuntimeException("please set rightGroupItemSpace");
        }
        return rightGroupItemSpace;
    }

    public int getRightGroupTextSize() {
        if (rightGroupTextSize == 0) {
            throw new JZRuntimeException("please set rightGroupTextSize");
        }
        return rightGroupTextSize;
    }

    public int getRightGroupTextColor() {
        if (rightGroupTextColor == 0) {
            throw new JZRuntimeException("please set rightGroupTextColor");
        }
        return rightGroupTextColor;
    }

    public int getRightGroupMarginRight() {
        if (rightGroupMarginRight == 0) {
            throw new JZRuntimeException("please set rightGroupMarginRight");
        }
        return rightGroupMarginRight;
    }

    public int getScrollBackLeftW() {
        if (scrollBackLeftW == 0) {
            throw new JZRuntimeException("please set scrollBackLeftW");
        }
        return scrollBackLeftW;
    }

    public int getStatusBarH() {
        if (statusBarH == 0) {
            throw new JZRuntimeException("please set statusBarH");
        }
        return statusBarH;
    }

    public int getBottomHorLineView() {
        if (bottomHorLineView == 0) {
            throw new JZRuntimeException("please set bottomHorLineView");
        }
        return bottomHorLineView;
    }

    public int getCustomToastHeight() {
        if (customToastHeight == 0) {
            throw new JZRuntimeException("please set customToastHeight");
        }
        return customToastHeight;
    }
}
