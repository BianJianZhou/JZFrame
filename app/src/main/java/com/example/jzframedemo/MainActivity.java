package com.example.jzframedemo;

import com.bjz.jzappframe.ui.JZBaseActivity;

public class MainActivity extends JZBaseActivity<MainPresenter> {

    @Override
    public int getResId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void setListener() {

    }

    @Override
    public MainPresenter getPresenter() {
        return null;
    }
}
