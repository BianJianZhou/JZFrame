package com.bjz.jzappframe.utils;

/**
 * ==================================
 * Created by WY_BIAN on 2018/12/7.
 * 作    者：WY_BJZ
 * 创建时间：2018/12/7
 * ==================================
 */
/*
 类 说 明：
 
 参数描述：
 
 
*/public class JZPageConfig {

    public static JZPageConfig getInstance() {
        JZPageConfig pageConfig = new JZPageConfig();
        return pageConfig;
    }

    private JZPageConfig() {
        isTouchHideKeyBroad = false;

        isAddTopView = true;

        isAddTitleView = true;

        isImersive = true;

        isImersiveDark = true;
    }

    /* 是否点击非输入区域软键盘消失 */
    private boolean isTouchHideKeyBroad;

    private boolean isAddTopView;

    private boolean isAddTitleView;

    private boolean isImersive;

    private boolean isImersiveDark;


    public JZPageConfig setTouchHideKeyBroad(boolean touchHideKeyBroad) {
        isTouchHideKeyBroad = touchHideKeyBroad;
        return this;
    }

    public JZPageConfig setAddTopView(boolean addTopView) {
        isAddTopView = addTopView;
        return this;
    }

    public JZPageConfig setAddTitleView(boolean addTitleView) {
        isAddTitleView = addTitleView;
        return this;
    }

    public JZPageConfig setImersive(boolean imersive) {
        isImersive = imersive;
        return this;
    }

    public JZPageConfig setImersiveDark(boolean isImersiveDark) {
        this.isImersiveDark = isImersiveDark;
        return this;
    }

    public boolean isTouchHideKeyBroad() {
        return isTouchHideKeyBroad;
    }

    public boolean isAddTopView() {
        return isAddTopView;
    }

    public boolean isAddTitleView() {
        return isAddTitleView;
    }

    public boolean isImersive() {
        return isImersive;
    }

    public boolean isImersiveDark() {
        return isImersiveDark;
    }
}
