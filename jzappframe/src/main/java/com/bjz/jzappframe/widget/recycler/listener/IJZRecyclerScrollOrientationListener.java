package com.bjz.jzappframe.widget.recycler.listener;

/**
 * ==================================
 * Created by 边江洲 on 2018/5/8.
 * 作    者：WY_BJZ
 * 创建时间：2018/5/8
 * ==================================
 */
/*
 类 说 明：

 滑动方向监听

 参数描述：
 
 
*/
public interface IJZRecyclerScrollOrientationListener {

    void top(int dy);

    void bottom(int dy);

    void scroll(int dx, int dy);

    void left(int dx);

    void right(int dx);

}
