package com.example.jzframedemo.presenter;

import android.content.Context;

import com.bjz.jzappframe.JZBasePresenter;
import com.example.jzframedemo.iview.ITestView;

public class TestPresenter extends JZBasePresenter<ITestView> {
    public TestPresenter(ITestView iView, Context context) {
        super(iView, context);
    }
}
