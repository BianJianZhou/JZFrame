package com.bjz.jzappframe;

import android.content.Context;

/**
 * ==================================
 * Created by 边江洲 on 2018/5/12.
 * 作    者：WY_BJZ
 * 创建时间：2018/5/12
 * ==================================
 */
/*
 类 说 明：
 
 参数描述：
 

 此类处理网络连接判断，进行断网判断提示，网络状态改变提示，回调到 baseActivity 中进行显示，断网重连，当前页面 接口重新请求

 网络判断在这里做，每次请求进行判断，断网回调在Activity中做提示，

在调用request 前 先进行总的网络判断  是否有网 （ping） 无网络状态下，页面的友好提示

*/
public class JZBasePresenter<T extends IJZBaseView> {

    public T ijzBaseView;

    public Context context;

    public JZBasePresenter(T ijzBaseView, Context context) {
        this.ijzBaseView = ijzBaseView;
        this.context = context;

    }

    public void onDestory() {
        ijzBaseView = null;
    }

}
