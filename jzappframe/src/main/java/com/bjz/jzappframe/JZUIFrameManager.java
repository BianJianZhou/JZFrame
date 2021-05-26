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
    private static JZViewsConfigBuilder viewsConfigBuilder;

    public static JZUIFrameManager getInstance() {
        if (jzuiFrameManager == null) {
            synchronized (JZUIFrameManager.class) {
                if (jzuiFrameManager == null) {
                    jzuiFrameManager = new JZUIFrameManager();
                    viewsConfigBuilder = new JZViewsConfigBuilder();
                }
            }
        }
        return jzuiFrameManager;
    }

    /*
    请配置 缺少对应参数会 抛异常：
    JZUIFrameManager.newInstance().getViewsConfigBuilder()
                .setTitleViewH((int) getResources().getDimension(R.dimen.dimens_66))
                .setBackBtnW((int) getResources().getDimension(R.dimen.dimens_20))
                .setBackBtnH((int) getResources().getDimension(R.dimen.dimens_20))
                .setBackBtnBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.tm_back_black_icon))
                .setBackBtnMarginLeft((int) getResources().getDimension(R.dimen.dimens_10))
                .setxIconW((int) getResources().getDimension(R.dimen.dimens_20))
                .setxIconH((int) getResources().getDimension(R.dimen.dimens_20))
                .setxIconMarginLeft((int) getResources().getDimension(R.dimen.dimens_10))
                .setxIconBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.tm_back_black_icon))
                .setTitleTextSize((int) getResources().getDimension(R.dimen.dimens_15))
                .setTitleTextColor((int) getResources().getColor(R.color.colorPrimary))
                .setTitleRightGroupItemSpace((int) getResources().getDimension(R.dimen.dimens_8))
                .setTitleRightGroupTextSize((int) getResources().getDimension(R.dimen.dimens_15))
                .setTitleRightGroupTextColor((int) getResources().getColor(R.color.colorAccent))
                .setTitleRightGroupMarginRight((int) getResources().getDimension(R.dimen.dimens_10))
                .setScrollBackLeftW((int) getResources().getDimension(R.dimen.dimens_20))
                .setTitleBottomHorLineViewH((int) getResources().getDimension(R.dimen.dimens_1))
                .setTitleBottomHorLineColor(getResources().getColor(R.color.colorAccent))
                .setCustomToastHeight((int) getResources().getDimension(R.dimen.dimens_50));
    * */
    public JZViewsConfigBuilder getViewsConfigBuilder() {
        return viewsConfigBuilder;
    }
}
