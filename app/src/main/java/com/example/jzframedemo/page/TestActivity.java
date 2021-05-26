package com.example.jzframedemo.page;

import com.bjz.jzappframe.ui.JZBaseActivity;
import com.example.jzframedemo.R;
import com.example.jzframedemo.iview.ITestView;
import com.example.jzframedemo.presenter.TestPresenter;

public class TestActivity extends JZBaseActivity<TestPresenter> implements ITestView {

    @Override
    public int getResId() {
        return R.layout.activity_wy_test;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        setTitle("测试页面");
    }

    @Override
    public void setListener() {

    }

    @Override
    public TestPresenter getPresenter() {
        return new TestPresenter(this, this);
    }
}
