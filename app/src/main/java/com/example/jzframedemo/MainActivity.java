package com.example.jzframedemo;

import com.bjz.jzappframe.ui.JZBaseActivity;

public class MainActivity extends JZBaseActivity<MainPresenter> {
    private final String TAG = "MainActivity";

    @Override
    public int getResId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        setTitle("MainActivity");
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
