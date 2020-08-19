package com.example.jzframedemo;

import com.bjz.jzappframe.ControllableThreadPoolExecutor.MyRunnable;
import com.bjz.jzappframe.ControllableThreadPoolExecutor.ThreadPoolUtil;
import com.bjz.jzappframe.ui.JZBaseActivity;
import com.bjz.jzappframe.utils.JZLog;
import com.bumptech.glide.Glide;

public class MainActivity extends JZBaseActivity<MainPresenter> {
    private final String TAG = "MainActivity";

    @Override
    public int getResId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        setTitle("MainActivity");
        Glide.with(this).pauseRequests();
    }

    @Override
    public void initData() {
        for (int i = 0; i < 120; i++) {
            ThreadPoolUtil.submit(new MyRunnable(i));
        }
        /* 暂停点击 */
        findViewById(R.id.pause).setOnClickListener(v -> {
            JZLog.d("检查", "暂停点击");
            ThreadPoolUtil.pause();
        });
        /* 恢复点击 */
        findViewById(R.id.resume).setOnClickListener(v -> {
            JZLog.d("检查", "恢复点击");
            ThreadPoolUtil.resume();
        });
    }

    @Override
    public void setListener() {

    }

    @Override
    public MainPresenter getPresenter() {
        return null;
    }
}
