package com.example.jzframedemo.presenter;

import android.content.Context;

import com.bjz.jzappframe.JZBasePresenter;
import com.example.jzframedemo.iview.ISplainView;

public class SplainPresenter extends JZBasePresenter<ISplainView> {
    public SplainPresenter(ISplainView iView, Context context) {
        super(iView, context);
    }
}
