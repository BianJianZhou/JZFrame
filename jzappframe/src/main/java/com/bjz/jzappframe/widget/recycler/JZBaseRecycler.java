package com.bjz.jzappframe.widget.recycler;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.ColorRes;
import androidx.recyclerview.widget.RecyclerView;

import com.bjz.jzappframe.R;
import com.bjz.jzappframe.utils.JZLog;
import com.bjz.jzappframe.widget.recycler.api.IJZBaseRefreshRecycler;
import com.bjz.jzappframe.widget.recycler.base.JZBaseView;
import com.bjz.jzappframe.widget.recycler.base.JZBaseViewHolderFromRecycler;
import com.bjz.jzappframe.widget.recycler.listener.IJZRecyclerItemClickListner;
import com.bjz.jzappframe.widget.recycler.listener.IJZRecyclerNoDataBtnClickListener;
import com.bjz.jzappframe.widget.recycler.listener.IJZRecyclerScrollFristChildChangeListener;
import com.bjz.jzappframe.widget.recycler.listener.IJZRecyclerScrollOrientationListener;
import com.bjz.jzappframe.widget.recycler.listener.IJZRecyclerScrollStopListener;
import com.bjz.jzappframe.widget.recycler.listener.IJZRequestListDataListener;
import com.bjz.jzappframe.widget.recycler.other.JZBaseReccylerNoDataView;
import com.bjz.jzappframe.widget.recycler.util.JZDiction;
import com.bjz.jzappframe.widget.refreshlayout.constant.SpinnerStyle;

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
public abstract class JZBaseRecycler<T> extends RelativeLayout implements IJZBaseRefreshRecycler<T> {

    /* 无数据按钮点击监听 */
    IJZRecyclerNoDataBtnClickListener noDataBtnClickListener;
    /* 滑动中第一个显示的item的position和据地顶部位置改变监听 */
    public IJZRecyclerScrollFristChildChangeListener fristChildChangeListener;
    /* 滑动停止监听 */
    public IJZRecyclerScrollStopListener scrollStopListener;
    /* 滑动方向监听 */
    public IJZRecyclerScrollOrientationListener scrollListener;
    /* 一次请求数据的量 */
    public int maxOnceSetDataNum = 20;

    public Context context;
    View itemView;
    public RecyclerView recyclerView;
    public IJZRecyclerItemClickListner itemClickListner;
    private RelativeLayout bigRl;

    /* 当前点击的item 的postion */
    private int dataChangePosition = -1;

    private String listType;

    int page = 1;

    public MyAdapter adapter;

    public String adapterState = JZDiction.AdapterState.AdapterRefresh;

    /* 背景图view */
    private ImageView bkImg;

    private JZBaseReccylerNoDataView noDataView;

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

    public IJZBaseRefreshRecycler setMaxOnceSetDataNum(int maxOnceSetDataNum) {
        this.maxOnceSetDataNum = maxOnceSetDataNum;
        return null;
    }

    /* 初始化 */
    public void initView() {
        if (getLayoutResId() == 0) {
            JZLog.d("检查", "recyclerBase_id为空");
            return;
        }
        itemView = LayoutInflater.from(context).inflate(getLayoutResId(), this, false);
        addView(itemView);
        adapterState = JZDiction.AdapterState.AdapterRefresh;
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
    public class MyAdapter extends RecyclerView.Adapter<JZBaseViewHolderFromRecycler> {

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
        public JZBaseViewHolderFromRecycler onCreateViewHolder(ViewGroup parent, int viewType) {
            return getViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(JZBaseViewHolderFromRecycler holder, int position) {
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
    public IJZBaseRefreshRecycler setLayoutManager(RecyclerView.LayoutManager manager) {
        recyclerView.setLayoutManager(manager);
        return this;
    }

    @Override
    public IJZBaseRefreshRecycler setListType(String type) {
        this.listType = type;
        return this;
    }

    public String getListType() {
        return listType;
    }

    @Override
    public void setItemClickListener(IJZRecyclerItemClickListner<JZBaseView, T> listener) {
        this.itemClickListner = listener;
    }

    @Override
    public IJZBaseRefreshRecycler setBaseRecyclerLayoutParams(ViewGroup.LayoutParams params) {
        bigRl.setLayoutParams(params);
        return this;
    }

    /* *****************************************************************基础配置数据相关************************************************************* */
    /* 当点击tap切换 recycler重新赋值的时候记得调用设置初始值为1
     * 并且同时调用 setAdapterState(TMCon.AdapterState.AdapterRefresh)
      * */
    @Override
    public IJZBaseRefreshRecycler setPage(int page) {
        this.page = page;
        return this;
    }

    @Override
    public IJZBaseRefreshRecycler onlyRefresh() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
        return this;
    }

    @Override
    public IJZBaseRefreshRecycler setAdapterState(String adapterState) {
        this.adapterState = adapterState;
        return this;
    }

    @Override
    public IJZBaseRefreshRecycler refresh(Collection<T> datas) {
        if (adapter != null) adapter.refresh(datas);
        return this;
    }

    /* 刷新单条数据 */
    @Override
    public IJZBaseRefreshRecycler refreshItem(T data, int position) {
        if (adapter != null) adapter.refreshItem(data, position);
        return this;
    }

    @Override
    public int getDataChangePotion() {
        return dataChangePosition;
    }

    @Override
    public IJZBaseRefreshRecycler setDataChangePosition(int position) {
        this.dataChangePosition = position;
        return this;
    }

    @Override
    public IJZBaseRefreshRecycler loadMore(Collection<T> datas) {
        if (adapter != null) adapter.loadmore(datas);
        return this;
    }

    @Override
    public IJZBaseRefreshRecycler addItem(int position, T data) {
        if (adapter != null) adapter.addItem(position, data);
        return this;
    }

    @Override
    public IJZBaseRefreshRecycler removeItem(int position) {
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
    public IJZBaseRefreshRecycler setBkImgUrl(int imgResId) {
        bkImg.setBackgroundResource(imgResId);
        return this;
    }

    /* 设置最外层背景色 */
    @Override
    public IJZBaseRefreshRecycler setOuterMostLayerBkColor(@ColorRes int colorRes) {
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
    public IJZBaseRefreshRecycler setNoDataStr(String noDataTopStr, String noDataBottomStr) {
        noDataView.setNoDataStr(noDataTopStr, noDataBottomStr);
        return this;
    }

    @Override
    public IJZBaseRefreshRecycler setNoDataViewIsShow(boolean noDataViewIsShow) {
        this.noDataViewIsShow = noDataViewIsShow;
        return this;
    }

    @Override
    public IJZBaseRefreshRecycler setNoDataBtnContentStr(String btnStr) {
        noDataView.setNoDataBtnContentStr(btnStr);
        return this;
    }

    @Override
    public IJZBaseRefreshRecycler setNoDataBkColor(int bkColor) {
        noDataView.setNoDataBkColor(bkColor);
        return this;
    }

    @Override
    public IJZBaseRefreshRecycler setNoDataBkResId(int resId) {
        noDataView.setNoDataBkResId(resId);
        return this;
    }

    @Override
    public IJZBaseRefreshRecycler setNoDataIconRes(int resId) {
        noDataView.setNoDataIconRel(resId);
        return this;
    }

    @Override
    public IJZBaseRefreshRecycler setNoDataBtnClickListener(IJZRecyclerNoDataBtnClickListener noDataBtnClickListener) {
        this.noDataBtnClickListener = noDataBtnClickListener;
        return this;
    }

    /* *****************************************************************设置滑动相关监听************************************************************* */

    @Override
    public IJZBaseRefreshRecycler setScrollFristChildChangeListener(IJZRecyclerScrollFristChildChangeListener fristChildChangeListener) {
        this.fristChildChangeListener = fristChildChangeListener;
        return this;
    }

    @Override
    public IJZBaseRefreshRecycler setScrollStopListener(IJZRecyclerScrollStopListener scrollStopListener) {
        this.scrollStopListener = scrollStopListener;
        return this;
    }

    @Override
    public IJZBaseRefreshRecycler setScrollListener(IJZRecyclerScrollOrientationListener scrollListener) {
        this.scrollListener = scrollListener;
        return this;
    }

    /* *****************************************************************刷新Recycler才会用到的方法调用************************************************************* */

    @Override
    public IJZBaseRefreshRecycler setRefreshHeadBkColor(int color) {
        throw new RuntimeException("baseRecycler 方法调用异常 setRefreshHeadContentColor");
    }

    @Override
    public IJZBaseRefreshRecycler setRefreshHeadContentColor(int color) {
        throw new RuntimeException("baseRecycler 方法调用异常 setRefreshHeadContentColor");
    }

    @Override
    public IJZBaseRefreshRecycler setRequestListDataListener(IJZRequestListDataListener listener) {
        throw new RuntimeException("baseRecycler 方法调用异常 setRequestListDataListener");
    }

    @Override
    public IJZBaseRefreshRecycler setEnableLoadMore(boolean isLoadMore) {
        throw new RuntimeException("baseRecycler 方法调用异常 setEnableLoadMore");
    }

    @Override
    public IJZBaseRefreshRecycler setEnableRefresh(boolean isRefresh) {
        throw new RuntimeException("baseRecycler 方法调用异常 setEnableRefresh");
    }

    @Override
    public IJZBaseRefreshRecycler setHeadViewRefreshStyle(SpinnerStyle type) {
        throw new RuntimeException("baseRecycler 方法调用异常 setHeadViewRefreshStyle");
    }

    @Override
    public IJZBaseRefreshRecycler autoRefresh() {
        throw new RuntimeException("baseRecycler 方法调用异常 autoRefresh");
    }

    @Override
    public IJZBaseRefreshRecycler autoLoadMore() {
        throw new RuntimeException("baseRecycler 方法调用异常 autoLoadMore");
    }

    public void onDestory() {

    }

}
