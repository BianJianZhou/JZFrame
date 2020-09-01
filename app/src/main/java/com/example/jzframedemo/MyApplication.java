package com.example.jzframedemo;

import android.app.Application;
import android.graphics.BitmapFactory;

import com.bjz.jzappframe.JZUIFrameManager;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JZUIFrameManager.getInstance().getViewsConfigBuilder()
                .setTitleViewH((int) getResources().getDimension(R.dimen.dimens_66))
                .setBackBtnW((int) getResources().getDimension(R.dimen.dimens_20))
                .setBackBtnH((int) getResources().getDimension(R.dimen.dimens_20))
                .setBackBtnBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.tm_back_black_icon))
                .setBackBtnMarginLeft((int) getResources().getDimension(R.dimen.dimens_10))
                .setxIconW((int) getResources().getDimension(R.dimen.dimens_20))
                .setxIconH((int) getResources().getDimension(R.dimen.dimens_20))
                .setxIconMarginLeft((int) getResources().getDimension(R.dimen.dimens_10))
                .setxIconBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.tm_back_black_icon))
                .setTitleTextSize((int) getResources().getDimension(R.dimen.dimens_24))
                .setTitleTextColor((int) getResources().getColor(R.color.colorPrimary))
                .setTitleRightGroupItemSpace((int) getResources().getDimension(R.dimen.dimens_8))
                .setTitleRightGroupTextSize((int) getResources().getDimension(R.dimen.dimens_8))
                .setTitleRightGroupTextColor((int) getResources().getColor(R.color.colorAccent))
                .setTitleRightGroupMarginRight((int) getResources().getDimension(R.dimen.dimens_10))
                .setScrollBackLeftW((int) getResources().getDimension(R.dimen.dimens_20))
                .setTitleBottomHorLineViewH((int) getResources().getDimension(R.dimen.dimens_1))
                .setTitleBottomHorLineColor(getResources().getColor(R.color.colorAccent))
                .setCustomToastHeight((int) getResources().getDimension(R.dimen.dimens_50));
    }
}
