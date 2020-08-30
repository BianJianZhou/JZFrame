package com.bjz.jzappframe.widget.recycler.api;

import android.view.ViewGroup;

import androidx.annotation.ColorRes;
import androidx.recyclerview.widget.RecyclerView;

import com.bjz.jzappframe.widget.recycler.base.JZBaseView;
import com.bjz.jzappframe.widget.recycler.base.JZBaseViewHolderFromRecycler;
import com.bjz.jzappframe.widget.recycler.listener.IJZRecyclerItemClickListner;
import com.bjz.jzappframe.widget.recycler.listener.IJZRecyclerNoDataBtnClickListener;
import com.bjz.jzappframe.widget.recycler.listener.IJZRecyclerScrollFristChildChangeListener;
import com.bjz.jzappframe.widget.recycler.listener.IJZRecyclerScrollOrientationListener;
import com.bjz.jzappframe.widget.recycler.listener.IJZRecyclerScrollStopListener;
import com.bjz.jzappframe.widget.recycler.listener.IJZRequestListDataListener;
import com.bjz.jzappframe.widget.refreshlayout.constant.SpinnerStyle;

import java.util.Collection;
import java.util.List;

/**
 * Created by 边江洲 on 2017/9/1.
 */
public interface IJZBaseRefreshRecycler<T> {

    /* 是否开启加载（默认开启） */
    IJZBaseRefreshRecycler setEnableLoadMore(boolean isLoadMore);

    /* 是否开启刷新（默认开启） */
    IJZBaseRefreshRecycler setEnableRefresh(boolean isRefresh);

    /* 设置刷新方式(默认拉伸变形) */
    IJZBaseRefreshRecycler setHeadViewRefreshStyle(SpinnerStyle type);

    /* 触发下拉刷新 */
    IJZBaseRefreshRecycler autoRefresh();

    /* 触发上拉加载 */
    IJZBaseRefreshRecycler autoLoadMore();

    /* 设置数据 */
    void setData(Collection<T> datas);

    /* 设置数据(与setData 不同的是 datas 如果为空则new一个空集合) */
    void setDataRetart(Collection<T> datas);

    /* 刷新数据 */
    IJZBaseRefreshRecycler refresh(Collection<T> datas);

    /* 刷新单条数据 */
    IJZBaseRefreshRecycler refreshItem(T data, int position);

    /* 获取数据需要改变的position */
    int getDataChangePotion();

    /* 设置数据改变的position */
    IJZBaseRefreshRecycler setDataChangePosition(int position);

    /* 加载数据 */
    IJZBaseRefreshRecycler loadMore(Collection<T> datas);

    /* 添加一项 */
    IJZBaseRefreshRecycler addItem(int position, T data);

    /* 删除一项 */
    IJZBaseRefreshRecycler removeItem(int position);

    /* 获取所有数据 */
    List<T> getData();

    /* 其他初始化设置监听等 */
    void onCreate();

    /* 设置LayoutManager */
    IJZBaseRefreshRecycler setLayoutManager(RecyclerView.LayoutManager manager);

    /* 设置适配器item类型 */
    int getBaseRecyclerItemViewType(int position);

    /* 获取适配器item个数 */
    int getBaseRecyclerItemCount();

    /* 获取适配器viewHolder */
    JZBaseViewHolderFromRecycler<T> getViewHolder(ViewGroup parent, int viewType);

    /* 初始化item布局 */
    void initItemView(JZBaseViewHolderFromRecycler holder, int position, List<T> data, int viewType);

    /* 设置item点击监听 */
    void setItemClickListener(IJZRecyclerItemClickListner<JZBaseView, T> listener);

    /* 设置列表类型 */
    IJZBaseRefreshRecycler setListType(String type);

    /* 仅刷新适配器 */
    IJZBaseRefreshRecycler onlyRefresh();

    /* 设置适配器刷新加载状态 */
    IJZBaseRefreshRecycler setAdapterState(String adapterState);

    /* 设置最外层组件默认背景色 */
    IJZBaseRefreshRecycler setOuterMostLayerBkColor(@ColorRes int colorRes);

    /* 设置页数 */
    IJZBaseRefreshRecycler setPage(int page);

    /* 设置刷新刷新列表的背景图 url */
    @SuppressWarnings("ResourceType")
    IJZBaseRefreshRecycler setBkImgUrl(int resId);

    /* 设置外层组件 params */
    IJZBaseRefreshRecycler setBaseRecyclerLayoutParams(ViewGroup.LayoutParams params);

    /* ********************************************************无数据内容相关********************************************** */
    /* 设置错误文字 */
    IJZBaseRefreshRecycler setNoDataStr(String failTopStr, String failBottomStr);

    /* 设置Fail 布局是否显示 */
    IJZBaseRefreshRecycler setNoDataViewIsShow(boolean failViewIsShow);

    /* 设置错误页面背景颜色 */
    IJZBaseRefreshRecycler setNoDataBkColor(int bkColor);

    /* 设置错误页面背景资源 */
    IJZBaseRefreshRecycler setNoDataBkResId(int resId);

    /* 设置错误页面图标资源 */
    IJZBaseRefreshRecycler setNoDataIconRes(int resId);

    /* 设置列表无内容页面按钮文字 */
    IJZBaseRefreshRecycler setNoDataBtnContentStr(String noDataBtnStr);

    /* 设置无内容按钮点击事件 */
    IJZBaseRefreshRecycler setNoDataBtnClickListener(IJZRecyclerNoDataBtnClickListener noDataBtnClickListener);


    /* ********************************************************滑动相关监听********************************************** */
    /* 滑动方向监听 */
    IJZBaseRefreshRecycler setScrollListener(IJZRecyclerScrollOrientationListener scrollListener);

    /* 滑动停止监听 */
    IJZBaseRefreshRecycler setScrollStopListener(IJZRecyclerScrollStopListener scrollStopListener);

    /* 滑动中第一个显示的item的position和据地顶部位置改变监听 */
    IJZBaseRefreshRecycler setScrollFristChildChangeListener(IJZRecyclerScrollFristChildChangeListener fristChildChangeListener);

    /* ********************************************************刷新recycler中调用的方法********************************************** */

    /* 设置刷新背景色 */
    IJZBaseRefreshRecycler setRefreshHeadBkColor(int color);

    /* 设置刷新剪头字体颜色 */
    IJZBaseRefreshRecycler setRefreshHeadContentColor(int color);

    /* 请求数据监听 */
    IJZBaseRefreshRecycler setRequestListDataListener(IJZRequestListDataListener listener);

}
