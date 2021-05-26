package com.example.jzframedemo.page;

import android.content.Intent;

import com.bjz.jzappframe.ui.JZBaseActivity;
import com.bjz.jzappframe.utils.JZPageConfig;
import com.example.jzframedemo.R;
import com.example.jzframedemo.iview.ISplainView;
import com.example.jzframedemo.presenter.SplainPresenter;

public class SplainActivity extends JZBaseActivity<SplainPresenter> implements ISplainView {

    @Override
    public JZPageConfig getJzPageConfig() {
        return JZPageConfig.newInstance().setAddTitleView(false).setAddTopView(false);
    }

    @Override
    public int getResId() {
        return R.layout.activity_wy_splain;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        postDelayed(2000, result -> {
            startActivity(new Intent(this, HomeActivity.class));
            overridePendingTransition(0, 0);
            finish();
        });
    }

    @Override
    public void setListener() {

    }

    @Override
    public SplainPresenter getPresenter() {
        return new SplainPresenter(this, this);
    }
}
