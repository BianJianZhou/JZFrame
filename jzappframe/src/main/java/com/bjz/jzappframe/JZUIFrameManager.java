package com.bjz.jzappframe;

/**
 * 拥有初始化 uiFrame 参数
 * 单例 案例：
 * https://www.jianshu.com/p/829a523c32aa
 * 双重判空
 * https://www.jianshu.com/p/1f9873a0e03a
 */
public class JZUIFrameManager {

    /* 本类 */
    private volatile static JZUIFrameManager jzuiFrameManager;

    /* 组件尺寸配置 */
    private JZViewsConfigBuilder viewsConfigBuilder;

    public static JZUIFrameManager getInstance() {
        if (jzuiFrameManager == null) {
            synchronized (JZUIFrameManager.class) {
                if (jzuiFrameManager == null) {
                    jzuiFrameManager = new JZUIFrameManager();
                }
            }
        }
        return jzuiFrameManager;
    }

    /*
    请配置 缺少对应参数会 抛异常：
    JZUIFrameManager.getInstance().getViewsConfigBuilder()
                .setTitleViewH()
                .setBackBtnW()
                .setBackBtnH()
                .setBackBtnBitmap()
                .setBackBtnMarginLeft()
                .setxIconW()
                .setxIconH()
                .setxIconMarginLeft()
                .setxIconBitmap()
                .setTitleTextSize()
                .setTitleTextColor()
                .setRightGroupItemSpace()
                .setRightGroupTextSize()
                .setRightGroupTextColor()
                .setRightGroupMarginRight()
                .setScrollBackLeftW()
                .setStatusBarH()
                .setBottomHorLineView()
                .setCustomToastHeight()
    * */
    public JZViewsConfigBuilder getViewsConfigBuilder() {
        return viewsConfigBuilder;
    }

    public void setViewsConfigBuilder(JZViewsConfigBuilder viewsConfigBuilder) {
        this.viewsConfigBuilder = viewsConfigBuilder;
    }
}
