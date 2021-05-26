package com.example.jzframedemo.page;

import android.content.Intent;
import android.widget.TextView;

import com.bjz.jzappframe.ui.JZBaseActivity;
import com.bjz.jzappframe.utils.JZPageAnimUtils;
import com.bjz.jzappframe.utils.JZPageConfig;
import com.example.jzframedemo.R;
import com.example.jzframedemo.iview.IHomeView;
import com.example.jzframedemo.presenter.HomePresenter;

public class HomeActivity extends JZBaseActivity<HomePresenter> implements IHomeView {

    TextView skipTestText;

    @Override
    public JZPageConfig getJzPageConfig() {
        return JZPageConfig.newInstance().setAddTitleBackIcon(false);
    }

    @Override
    public int getResId() {
        return R.layout.activity_wy_home;
    }

    @Override
    public void initView() {
        skipTestText = findViewById(R.id.home_skip_test_text);
    }

    @Override
    public void initData() {
        setTitle("首页");
    }

    @Override
    public void setListener() {
        /* 设置点击事件 */
        skipTestText.setOnClickListener(v -> {
            startActivity(new Intent(this, TestActivity.class));
            JZPageAnimUtils.skipActivityAnim(this);
        });
    }

    @Override
    public HomePresenter getPresenter() {
        return new HomePresenter(this, this);
    }
}
