package com.bjz.jzappframe.widget.recycler.recycler;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.wy.viewFrame.R;
import com.wy.viewFrame.util.Diction;
import com.wy.viewFrame.wyRecycler.base.TMBaseView;
import com.wy.viewFrame.wyRecycler.api.ITMBaseRefreshRecycler;
import com.wy.viewFrame.wyRecycler.listener.ITMRequestListDataListener;
import com.wy.viewFrame.wyRecycler.listener.IWYScrollChangeListenerScrollView;
import com.wy.viewFrame.util.WYToast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by 边江洲 on 2017/9/1.
 */

/*
 setData 与 setDataRetart
 的区别在于 列表在刷新状态下 及时列表的长度为0 依然回去进行赋值
 （用来适应 tab 切换统一个数据的情况）
 但数据集合不可为空
  */

    /*
    适用于添加headView的 场景模式
    此应用场景下
    因为recyclerView 外层嵌套了 ScrollView 所以 recyclerView 的滑动监听不会生效所以 刷新控件的
      */
public abstract class JZRefreshRecycler_FromHeadExtend<T> extends JZBaseRecycler<T> {

    private RefreshLayout mRefreshLayout;
    private ClassicsHeader mClassicsHeader;
    ITMRequestListDataListener requestDataListener;

    IWYScrollChangeListenerScrollView
            scrollChangeView;

    private List<TMBaseView> headViews = new ArrayList<>();

    LinearLayout headGroupLl;

    /* 刷新加载动画持续的时间 */
    private long animTime = 300;

    /* 初始的时候是否开启了loadMore功能 */
    private boolean startIsOpenLoadMore = true;

    public JZRefreshRecycler_FromHeadExtend(Context context) {
        super(context);
    }

    public JZRefreshRecycler_FromHeadExtend(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JZRefreshRecycler_FromHeadExtend(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initView() {
        super.initView();
        mRefreshLayout = bind(R.id.base_recycler_view_refreshLayout);
        mClassicsHeader = (ClassicsHeader) mRefreshLayout.getRefreshHeader();
        recyclerView = bind(R.id.base_recycler_view_recycler);
        headGroupLl = bind(R.id.refresh_recycler_head_group_ll);

        scrollChangeView = bind(R.id.refresh_recycler_scroll_change_view);

        mRefreshLayout.setEnableLoadmore(true);
        mRefreshLayout.setEnableRefresh(true);
        mClassicsHeader.setSpinnerStyle(SpinnerStyle.Scale);

        /* 设置组件刷新加载监听 */
        mRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                adapterState = Diction.AdapterState.AdapterRefresh;
                if (requestDataListener != null) {
                    requestDataListener.requestData(page, Diction.AdapterState.AdapterRefresh);
                }
                handler.removeMessages(0);
                handler.sendEmptyMessageDelayed(0, 5000);
                if (headViews != null && headViews.size() != 0) {
                    for (int i = 0; i < headViews.size(); i++) {
                        headViews.get(i).setData(null);
                    }
                }
            }

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                page++;
                adapterState = Diction.AdapterState.AdapterLoadMore;
                if (requestDataListener != null) {
                    if (page == 1) {
                        page++;
                    }
                    requestDataListener.requestData(page, Diction.AdapterState.AdapterLoadMore);
                }
                handler.removeMessages(0);
                handler.sendEmptyMessageDelayed(0, 5000);
            }
        });

        /*
        无感知加载更多
        2018年 6 月 24日 添加此扩展 但
        此应用场景下
        因为recyclerView 外层嵌套了 ScrollView 所以 recyclerView 的滑动监听不会生效所以 将 无感知加载监听注销掉
        */
//        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
//            @Override
//            public void onLoadMore() {
//                page++;
//                adapterState = Diction.AdapterState.AdapterLoadMore;
//                if (requestDataListener != null) {
//                    if (page == 1) {
//                        page++;
//                    }
//                    requestDataListener.requestData(page, Diction.AdapterState.AdapterLoadMore);
//                }
//            }
//        });

    }

    private boolean isRequestSuccess = true;

    long scrollBeforeTime = 0;
    long scrollBeforeTime2 = 0;

    public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
        //用来标记是否正在向左滑动
        private boolean isScrollToBottom = false;

        /**
         * @param recyclerView
         * @param newState     拖动滑动：1
         *                     自动滑动：2
         *                     停止滑动：0
         */
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
            // 当不滑动时
//            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            // 获取最后一个完全显示的itemPosition
            int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
            int itemCount = manager.getItemCount();
//            TMLogUtils.d("检查", "itemCount --》 " + itemCount + "\nlastItemPosition --》 " + lastItemPosition);
            if (lastItemPosition >= (itemCount - 4) && lastItemPosition != itemCount && isScrollToBottom && getData().size() == maxOnceSetDataNum * page && isRequestSuccess && newState == 1) {
//                TMLogUtils.d("检查", "监听生效");
                // 加载更多
                onLoadMore();
                isRequestSuccess = false;
            }
            /* 当滑动停止的时候 */
            if (newState == 0) {
                if (scrollStopListener != null) {
                    int fristVisiblePosition = manager.findFirstVisibleItemPosition();
                    scrollStopListener.stop(fristVisiblePosition, manager);
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (fristChildChangeListener != null && (System.currentTimeMillis() - scrollBeforeTime2) > 50) {
                scrollBeforeTime2 = System.currentTimeMillis();
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int position = manager.findFirstVisibleItemPosition();
                View firstVisiableChildView = manager.findViewByPosition(position);
                fristChildChangeListener.fristPositionChange(position, firstVisiableChildView.getTop());
            }
            // dx值大于0表示正在向左滑动，小于或等于0表示向右滑动或停止
            // dy值大于0表示正在向下滑动(手向上拖动)，小于或等于0表示向上滑动或停止
            isScrollToBottom = dy > 0;
            if ((System.currentTimeMillis() - scrollBeforeTime) > 500) {
                if (scrollListener != null) {
                    scrollListener.scroll(dx, dy);
                    scrollBeforeTime = System.currentTimeMillis();
                    if (dy > 0) {
                        scrollListener.bottom(dy);
                    }
                    if (dy < 0) {
                        scrollListener.top(dy);
                    }
                }
            }
        }

        /* 加载更多回调 */
        public abstract void onLoadMore();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mRefreshLayout.getLayout().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            WYToast.showShortToast(context, "暂时没有新的内容呦");
                            if (adapterState == Diction.AdapterState.AdapterRefresh) {
                                mRefreshLayout.getLayout().post(() -> {
                                    mRefreshLayout.finishRefresh();
                                });
                            } else if (adapterState == Diction.AdapterState.AdapterLoadMore) {
                                mRefreshLayout.getLayout().post(() -> {
                                    mRefreshLayout.finishLoadmore();
                                });
                            }
                        }
                    }, animTime);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public int getLayoutResId() {
        return R.layout.tm_base_refresh_recycler_view_from_head_extend;
    }

    @Override
    public void setData(Collection<T> datas) {
        recyclerStateRestart();
        if (datas != null) {
            if (startIsOpenLoadMore) {
                mRefreshLayout.setEnableLoadmore(startIsOpenLoadMore);
            }
            if (adapter == null) {
                dataJudgeNoDataViewShowState(datas);
                adapter = new MyAdapter(datas);
                recyclerView.setAdapter(adapter);
                if (startIsOpenLoadMore) {
                    if (datas.size() < maxOnceSetDataNum) {
                        mRefreshLayout.setEnableLoadmore(false);
                    } else {
                        mRefreshLayout.setEnableLoadmore(true);
                    }
                }
            } else {
                switch (adapterState) {
                    case Diction.AdapterState.AdapterRefresh:
                        if (datas != null && datas.size() > 0) {
                            adapter.refresh(datas);
                        }
                        dataJudgeNoDataViewShowState(datas);
                        if (startIsOpenLoadMore) {
                            if (datas.size() < maxOnceSetDataNum) {
                                mRefreshLayout.setEnableLoadmore(false);
                            } else {
                                mRefreshLayout.setEnableLoadmore(true);
                            }
                        }
                        break;
                    case Diction.AdapterState.AdapterLoadMore:
                        adapter.loadmore(datas);
                        if (datas.size() == 0) {
                            WYToast.showShortToast(context, "亲，没有更多数据了");
                            if (startIsOpenLoadMore) {
                                if (datas.size() < maxOnceSetDataNum) {
                                    mRefreshLayout.setEnableLoadmore(false);
                                } else {
                                    mRefreshLayout.setEnableLoadmore(true);
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }
                if (datas.size() == 0 && page > 1 && Diction.AdapterState.AdapterLoadMore.equals(adapterState)) {
                    page--;
                }
            }
        }
    }

    @Override
    public void setDataRetart(Collection<T> datas) {
        recyclerStateRestart();
        if (datas != null) {
            if (startIsOpenLoadMore) {
                mRefreshLayout.setEnableLoadmore(startIsOpenLoadMore);
            }
            if (adapter == null) {
                dataJudgeNoDataViewShowState(datas);
                adapter = new MyAdapter(datas);
                recyclerView.setAdapter(adapter);
                if (startIsOpenLoadMore) {
                    if (datas.size() < maxOnceSetDataNum) {
                        mRefreshLayout.setEnableLoadmore(false);
                    } else {
                        mRefreshLayout.setEnableLoadmore(true);
                    }
                }
            } else {
                switch (adapterState) {
                    case Diction.AdapterState.AdapterRefresh:
                        if (datas != null) {
                            adapter.refresh(datas);
                        }
                        dataJudgeNoDataViewShowState(datas);
                        if (startIsOpenLoadMore) {
                            if (datas.size() < maxOnceSetDataNum) {
                                mRefreshLayout.setEnableLoadmore(false);
                            } else {
                                mRefreshLayout.setEnableLoadmore(true);
                            }
                        }
                        break;
                    case Diction.AdapterState.AdapterLoadMore:
                        adapter.loadmore(datas);
                        if (datas.size() == 0) {
                            WYToast.showShortToast(context, "亲，没有更多数据了");
                            if (startIsOpenLoadMore) {
                                if (datas.size() < maxOnceSetDataNum) {
                                    mRefreshLayout.setEnableLoadmore(false);
                                } else {
                                    mRefreshLayout.setEnableLoadmore(true);
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }
                if (datas.size() == 0 && page > 1 && Diction.AdapterState.AdapterLoadMore.equals(adapterState)) {
                    page--;
                }
            }
        }
    }

    /* 如果需要头布局随着列表刷新而刷新 则调用此方法 */
    public ITMBaseRefreshRecycler addHeadView(TMBaseView headView) {
        headViews.add(headView);
        headView.setData(null);
        headGroupLl.addView(headView);
        return this;
    }

    /* 如果需要头布局随着列表刷新而刷新 则调用此方法 */
    public ITMBaseRefreshRecycler addHeadView(View headView) {
        headGroupLl.addView(headView);
        return this;
    }

    public ITMBaseRefreshRecycler removeHeadView() {
        if (headViews != null && headViews.size() != 0) {
            headViews.clear();
        }
        headGroupLl.removeAllViews();
        return this;
    }

    /* 列表状态初始化 */
    /* 刷新 recycler 专属 */
    private void recyclerStateRestart() {
        if (adapterState == Diction.AdapterState.AdapterRefresh) {
            mRefreshLayout.finishRefresh();
        } else if (adapterState == Diction.AdapterState.AdapterLoadMore) {
            mRefreshLayout.finishLoadmore();
        }
        isRequestSuccess = true;
        handler.removeMessages(0);
    }

    @Override
    public ITMBaseRefreshRecycler setRequestListDataListener(ITMRequestListDataListener listener) {
        this.requestDataListener = listener;
        return this;
    }

    @Override
    public ITMBaseRefreshRecycler setRefreshHeadBkColor(int color) {
        mClassicsHeader.setBackgroundColor(color);
        return this;
    }

    @Override
    public ITMBaseRefreshRecycler setRefreshHeadContentColor(int color) {
        mClassicsHeader.setAccentColor(color);
        return this;
    }

    @Override
    public ITMBaseRefreshRecycler setEnableLoadMore(boolean isLoadMore) {
        startIsOpenLoadMore = isLoadMore;
        return this;
    }

    @Override
    public ITMBaseRefreshRecycler setEnableRefresh(boolean isRefresh) {
        mRefreshLayout.setEnableRefresh(isRefresh);
        return this;
    }

    @Override
    public ITMBaseRefreshRecycler setHeadViewRefreshStyle(SpinnerStyle type) {
        mClassicsHeader.setSpinnerStyle(SpinnerStyle.Scale);
        return this;
    }

    @Override
    public ITMBaseRefreshRecycler autoRefresh() {
        mRefreshLayout.autoRefresh();
        return this;
    }

    @Override
    public ITMBaseRefreshRecycler autoLoadMore() {
        mRefreshLayout.autoLoadmore();
        return this;
    }

    public ITMBaseRefreshRecycler setRefreshAndLoadMoreAnimTime(long time) {
        this.animTime = time;
        return this;
    }

}
