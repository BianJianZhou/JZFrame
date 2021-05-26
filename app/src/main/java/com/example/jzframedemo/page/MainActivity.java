package com.example.jzframedemo.page;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bjz.jzappframe.JZButterKnif.JZBindView;
import com.bjz.jzappframe.JZButterKnif.JZButterKnif;
import com.bjz.jzappframe.JZButterKnif.JZOnClick;
import com.bjz.jzappframe.ui.JZBaseActivity;
import com.bjz.jzappframe.widget.title.titleChild.JZTitleRightTextGroup;
import com.bumptech.glide.Glide;
import com.example.jzframedemo.bean.MyData;
import com.example.jzframedemo.view.MyListView;
import com.example.jzframedemo.R;
import com.example.jzframedemo.presenter.MainPresenter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends JZBaseActivity<MainPresenter> {
    
    private final String TAG = "MainActivity";

    @JZBindView(R.id.pause)
    TextView pauseText;

    @JZBindView(R.id.listview)
    MyListView listview;

    @JZOnClick({R.id.pause, R.id.resume})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pause:
                Toast.makeText(this, "pause", Toast.LENGTH_SHORT).show();
                break;
            case R.id.resume:
                Toast.makeText(this, "resume", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @JZOnClick(R.id.hellow)
    public void onClickxxx() {
        Toast.makeText(this, "hellow", Toast.LENGTH_SHORT).show();
    }


    @Override
    public int getResId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        setTitle("添加客户");
        Glide.with(this).pauseRequests();
        JZButterKnif.bind(this);
        pauseText.setText("asfd");
        List<MyData> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            MyData myData = new MyData();
            list.add(myData);
        }
        listview.setData(list);
    }

    @Override
    public void initData() {
        titleView.setRightTextGroup(new JZTitleRightTextGroup(this).addItem_TextView("完成", v -> {
            showToast("完成点击");
        }).addItem_TextView("你好",v -> {

        }));
    }

    @Override
    public void setListener() {

    }

    @Override
    public MainPresenter getPresenter() {
        return null;
    }
}
