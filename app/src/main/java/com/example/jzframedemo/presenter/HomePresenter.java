package com.example.jzframedemo.presenter;

import android.content.Context;

import com.bjz.jzappframe.JZBasePresenter;
import com.example.jzframedemo.iview.IHomeView;

public class HomePresenter extends JZBasePresenter<IHomeView> {
    public HomePresenter(IHomeView iView, Context context) {
        super(iView, context);
    }
}
