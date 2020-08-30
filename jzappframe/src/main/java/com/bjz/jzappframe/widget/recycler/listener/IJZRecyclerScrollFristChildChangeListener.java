package com.bjz.jzappframe.widget.recycler.listener;

/**
 * ==================================
 * Created by 边江洲 on 2018/6/23.
 * 作    者：WY_BJZ
 * 创建时间：2018/6/23
 * ==================================
 */
/*
 类 说 明：

 滑动中第一个显示的item的position和据地顶部位置改变监听

 参数描述：
 
 
*/
public interface IJZRecyclerScrollFristChildChangeListener {
    void fristPositionChange(int fristVisiblePosition, int fristViewTop);
}
