package com.bjz.jzappframe.widget.recycler.listener;

import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * ==================================
 * Created by 边江洲 on 2018/6/23.
 * 作    者：WY_BJZ
 * 创建时间：2018/6/23
 * ==================================
 */
/*
 类 说 明：

 滑动停止监听
 
 参数描述：
 
 
*/
public interface IJZRecyclerScrollStopListener {
    void stop(int fristVisiblePosition, LinearLayoutManager manager);
}
