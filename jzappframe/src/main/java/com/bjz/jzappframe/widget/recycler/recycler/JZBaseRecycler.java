package com.bjz.jzappframe.widget.recycler.recycler;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.wy.viewFrame.R;
import com.wy.viewFrame.util.Diction;
import com.wy.viewFrame.wyRecycler.api.ITMBaseRefreshRecycler;
import com.wy.viewFrame.wyRecycler.base.TMBaseView;
import com.wy.viewFrame.wyRecycler.base.TMBaseViewHolderFromRecycler;
import com.wy.viewFrame.wyRecycler.other.TMBaseReccylerNoDataView;
import com.wy.viewFrame.wyRecycler.listener.ITMRecyclerItemClickListner;
import com.wy.viewFrame.wyRecycler.listener.ITMRecyclerNoDataBtnClickListener;
import com.wy.viewFrame.wyRecycler.listener.ITMRecyclerScrollFristChildChangeListener;
import com.wy.viewFrame.wyRecycler.listener.ITMRecyclerScrollOrientationListener;
import com.wy.viewFrame.wyRecycler.listener.ITMRecyclerScrollStopListener;
import com.wy.viewFrame.wyRecycler.listener.ITMRequestListDataListener;
import com.wy.viewFrame.util.LogUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by 边江洲 on 2017/12/22.
 */
/*

注意：---->>
 当点击tap切换 recycler重新赋值的时候记得调用
 .setAdapterState(TMCon.AdapterState.AdapterRefresh)
 .setPage(1)

  */
public abstract class JZBaseRecycler<T> extends RelativeLayout implements ITMBaseRefreshRecycler<T> {

    /* 无数据按钮点击监听 */
    ITMRecyclerNoDataBtnClickListener noDataBtnClickListener;
    /* 滑动中第一个显示的item的position和据地顶部位置改变监听 */
    public ITMRecyclerScrollFristChildChangeListener fristChildChangeListener;
    /* 滑动停止监听 */
    public ITMRecyclerScrollStopListener scrollStopListener;
    /* 滑动方向监听 */
    public ITMRecyclerScrollOrientationListener scrollListener;
    /* 一次请求数据的量 */
    public int maxOnceSetDataNum = 20;

    public Context context;
    View itemView;
    public RecyclerView recyclerView;
    public ITMRecyclerItemClickListner itemClickListner;
    private RelativeLayout bigRl;

    /* 当前点击的item 的postion */
    private int dataChangePosition = -1;

    private String listType;

    int page = 1;

    public MyAdapter adapter;

    public String adapterState = Diction.AdapterState.AdapterRefresh;

    /* 背景图view */
    private ImageView bkImg;

    private TMBaseReccylerNoDataView noDataView;

    public JZBaseRecycler(Context context) {
        this(context, null);
    }

    public JZBaseRecycler(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JZBaseRecycler(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        onCreate();
        initView();
    }

    public ITMBaseRefreshRecycler setMaxOnceSetDataNum(int maxOnceSetDataNum) {
        this.maxOnceSetDataNum = maxOnceSetDataNum;
        return null;
    }

    /* 初始化 */
    public void initView() {
        if (getLayoutResId() == 0) {
            LogUtils.d("检查", "recyclerBase_id为空");
            return;
        }
        itemView = LayoutInflater.from(context).inflate(getLayoutResId(), this, false);
        addView(itemView);
        adapterState = Diction.AdapterState.AdapterRefresh;
        recyclerView = bind(R.id.base_recycler_view_recycler);
        noDataView = bind(R.id.base_recycler_no_data_view);

        bigRl = bind(R.id.tm_base_recycler_view_big_rl);
        bkImg = bind(R.id.base_recycler_view_bk_img);
        recyclerView.setNestedScrollingEnabled(false);

        noDataView
                .setNoDataBtnClickListener(() -> {
                    if (noDataBtnClickListener != null) {
                        noDataBtnClickListener.click();
                    }
                })
                .setOnClickListener(view -> {

                });
        noDataView.setData(null);

        if (getLayoutManager() != null) {
            recyclerView.setLayoutManager(getLayoutManager());
        } else {
            throw new RuntimeException("recycler--layoutManager为空\n" + getClass().getSimpleName() + "\n子类 getLayoutManager()");
        }
    }

    /* recycler的适配器 */
    public class MyAdapter extends RecyclerView.Adapter<TMBaseViewHolderFromRecycler> {

        List<T> datas;

        public MyAdapter(Collection<T> collection) {
            datas = new ArrayList<>(collection);
        }

        public void refresh(Collection<T> collection) {
            datas.clear();
            datas.addAll(collection);
            notifyDataSetChanged();
        }

        public void refreshItem(T data, int position) {
            if (position != -1 && data != null) {
                datas.remove(position);
                datas.add(position, data);
                dataChangePosition = -1;
                notifyDataSetChanged();
            }
        }

        public void loadmore(Collection<T> collection) {
            datas.addAll(collection);
            notifyDataSetChanged();
        }

        public void removeItem(int position) {
            datas.remove(position);
            notifyDataSetChanged();
        }

        public void addItem(int position, T data) {
            datas.add(position, data);
            notifyDataSetChanged();
        }

        public List<T> getDatas() {
            return datas;
        }

        @Override
        public int getItemViewType(int position) {
            if (getBaseRecyclerItemViewType(position) == 0)
                return super.getItemViewType(position);
            else return getBaseRecyclerItemViewType(position);
        }

        @Override
        public TMBaseViewHolderFromRecycler onCreateViewHolder(ViewGroup parent, int viewType) {
            return getViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(TMBaseViewHolderFromRecycler holder, int position) {
            initItemView(holder, position, datas, getItemViewType(position));
            if (holder != null && holder.baseItemView != null)
                holder.baseItemView.setOnClickListener(view -> {
                    dataChangePosition = position;
                    if (itemClickListner != null) {
                        itemClickListner.click(holder.baseItemView, getData(), position);
                    }
                });
        }

        @Override
        public int getItemCount() {
            return getBaseRecyclerItemCount();
        }

    }


    /* *****************************************************************基础配置************************************************************* */

    public abstract RecyclerView.LayoutManager getLayoutManager();

    public abstract int getLayoutResId();

    public <viewT extends View> viewT bind(int id) {
        return (viewT) itemView.findViewById(id);
    }

    @Override
    public ITMBaseRefreshRecycler setLayoutManager(RecyclerView.LayoutManager manager) {
        recyclerView.setLayoutManager(manager);
        return this;
    }

    @Override
    public ITMBaseRefreshRecycler setListType(String type) {
        this.listType = type;
        return this;
    }

    public String getListType() {
        return listType;
    }

    @Override
    public void setItemClickListener(ITMRecyclerItemClickListner<TMBaseView, T> listener) {
        this.itemClickListner = listener;
    }

    @Override
    public ITMBaseRefreshRecycler setBaseRecyclerLayoutParams(ViewGroup.LayoutParams params) {
        bigRl.setLayoutParams(params);
        return this;
    }

    /* *****************************************************************基础配置数据相关************************************************************* */
    /* 当点击tap切换 recycler重新赋值的时候记得调用设置初始值为1
     * 并且同时调用 setAdapterState(TMCon.AdapterState.AdapterRefresh)
      * */
    @Override
    public ITMBaseRefreshRecycler setPage(int page) {
        this.page = page;
        return this;
    }

    @Override
    public ITMBaseRefreshRecycler onlyRefresh() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
        return this;
    }

    @Override
    public ITMBaseRefreshRecycler setAdapterState(String adapterState) {
        this.adapterState = adapterState;
        return this;
    }

    @Override
    public ITMBaseRefreshRecycler refresh(Collection<T> datas) {
        if (adapter != null) adapter.refresh(datas);
        return this;
    }

    /* 刷新单条数据 */
    @Override
    public ITMBaseRefreshRecycler refreshItem(T data, int position) {
        if (adapter != null) adapter.refreshItem(data, position);
        return this;
    }

    @Override
    public int getDataChangePotion() {
        return dataChangePosition;
    }

    @Override
    public ITMBaseRefreshRecycler setDataChangePosition(int position) {
        this.dataChangePosition = position;
        return this;
    }

    @Override
    public ITMBaseRefreshRecycler loadMore(Collection<T> datas) {
        if (adapter != null) adapter.loadmore(datas);
        return this;
    }

    @Override
    public ITMBaseRefreshRecycler addItem(int position, T data) {
        if (adapter != null) adapter.addItem(position, data);
        return this;
    }

    @Override
    public ITMBaseRefreshRecycler removeItem(int position) {
        if (adapter != null) adapter.removeItem(position);
        return this;
    }

    @Override
    public List<T> getData() {
        if (adapter == null) return new ArrayList<T>();
        else return adapter.getDatas();
    }

    /* *****************************************************************基础配ui相关************************************************************* */
    /* 设置列表背景图片 resId */
    @Override
    public ITMBaseRefreshRecycler setBkImgUrl(int imgResId) {
        bkImg.setBackgroundResource(imgResId);
        return this;
    }

    /* 设置最外层背景色 */
    @Override
    public ITMBaseRefreshRecycler setOuterMostLayerBkColor(@ColorRes int colorRes) {
        bigRl.setBackgroundResource(colorRes);
        return this;
    }

    /* *****************************************************************设置无数据页面相关************************************************************* */

    private boolean noDataViewIsShow = true;

    /* 数据判断 无数据提示页面 显示状态 */
    protected void dataJudgeNoDataViewShowState(Collection<T> datas) {
        if (noDataViewIsShow) {
            if (datas.size() == 0) {
                noDataView.setVisibility(View.VISIBLE);
                noDataView.dataJudgeNoDataViewShowState();
            } else {
                noDataView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public ITMBaseRefreshRecycler setNoDataStr(String noDataTopStr, String noDataBottomStr) {
        noDataView.setNoDataStr(noDataTopStr, noDataBottomStr);
        return this;
    }

    @Override
    public ITMBaseRefreshRecycler setNoDataViewIsShow(boolean noDataViewIsShow) {
        this.noDataViewIsShow = noDataViewIsShow;
        return this;
    }

    @Override
    public ITMBaseRefreshRecycler setNoDataBtnContentStr(String btnStr) {
        noDataView.setNoDataBtnContentStr(btnStr);
        return this;
    }

    @Override
    public ITMBaseRefreshRecycler setNoDataBkColor(int bkColor) {
        noDataView.setNoDataBkColor(bkColor);
        return this;
    }

    @Override
    public ITMBaseRefreshRecycler setNoDataBkResId(int resId) {
        noDataView.setNoDataBkResId(resId);
        return this;
    }

    @Override
    public ITMBaseRefreshRecycler setNoDataIconRes(int resId) {
        noDataView.setNoDataIconRel(resId);
        return this;
    }

    @Override
    public ITMBaseRefreshRecycler setNoDataBtnClickListener(ITMRecyclerNoDataBtnClickListener noDataBtnClickListener) {
        this.noDataBtnClickListener = noDataBtnClickListener;
        return this;
    }

    /* *****************************************************************设置滑动相关监听************************************************************* */

    @Override
    public ITMBaseRefreshRecycler setScrollFristChildChangeListener(ITMRecyclerScrollFristChildChangeListener fristChildChangeListener) {
        this.fristChildChangeListener = fristChildChangeListener;
        return this;
    }

    @Override
    public ITMBaseRefreshRecycler setScrollStopListener(ITMRecyclerScrollStopListener scrollStopListener) {
        this.scrollStopListener = scrollStopListener;
        return this;
    }

    @Override
    public ITMBaseRefreshRecycler setScrollListener(ITMRecyclerScrollOrientationListener scrollListener) {
        this.scrollListener = scrollListener;
        return this;
    }

    /* *****************************************************************刷新Recycler才会用到的方法调用************************************************************* */

    @Override
    public ITMBaseRefreshRecycler setRefreshHeadBkColor(int color) {
        throw new RuntimeException("baseRecycler 方法调用异常 setRefreshHeadContentColor");
    }

    @Override
    public ITMBaseRefreshRecycler setRefreshHeadContentColor(int color) {
        throw new RuntimeException("baseRecycler 方法调用异常 setRefreshHeadContentColor");
    }

    @Override
    public ITMBaseRefreshRecycler setRequestListDataListener(ITMRequestListDataListener listener) {
        throw new RuntimeException("baseRecycler 方法调用异常 setRequestListDataListener");
    }

    @Override
    public ITMBaseRefreshRecycler setEnableLoadMore(boolean isLoadMore) {
        throw new RuntimeException("baseRecycler 方法调用异常 setEnableLoadMore");
    }

    @Override
    public ITMBaseRefreshRecycler setEnableRefresh(boolean isRefresh) {
        throw new RuntimeException("baseRecycler 方法调用异常 setEnableRefresh");
    }

    @Override
    public ITMBaseRefreshRecycler setHeadViewRefreshStyle(SpinnerStyle type) {
        throw new RuntimeException("baseRecycler 方法调用异常 setHeadViewRefreshStyle");
    }

    @Override
    public ITMBaseRefreshRecycler autoRefresh() {
        throw new RuntimeException("baseRecycler 方法调用异常 autoRefresh");
    }

    @Override
    public ITMBaseRefreshRecycler autoLoadMore() {
        throw new RuntimeException("baseRecycler 方法调用异常 autoLoadMore");
    }

    public void onDestory() {

    }

}
