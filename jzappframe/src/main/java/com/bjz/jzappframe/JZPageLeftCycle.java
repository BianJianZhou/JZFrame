package com.bjz.jzappframe;

import android.content.Context;

/**
 * Created by 边江洲 on 2017/10/9.
 */

/* 页面生命周期帮助类 */
public class JZPageLeftCycle {

    public JZPageLeftCycle() {
    }

    public void onStart(){

    }

    public void onCreate() {

    }

    public void onResume() {

    }

    public void onPause() {

    }

    public void onStop() {

    }

    public void onDestory(JZBasePresenter presenter, Context context) {
        if (presenter != null) {
            presenter.onDestory();
        }
    }

}
