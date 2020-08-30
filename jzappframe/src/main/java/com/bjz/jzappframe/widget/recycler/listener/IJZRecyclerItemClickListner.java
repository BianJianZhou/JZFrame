package com.bjz.jzappframe.widget.recycler.listener;

import java.util.List;

/**
 * Created by 边江洲 on 2017/9/1.
 */

/**
 * 列表item点击监听
 * @param <viewTx>
 * @param <T>
 */

public interface IJZRecyclerItemClickListner<viewTx, T> extends IJZBaseListener {

    void click(viewTx view, List<T> datas, int position);

}
