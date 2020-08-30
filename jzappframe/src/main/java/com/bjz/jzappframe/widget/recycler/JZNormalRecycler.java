package com.bjz.jzappframe.widget.recycler;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bjz.jzappframe.R;
import com.bjz.jzappframe.utils.JZToast;
import com.bjz.jzappframe.widget.recycler.util.JZDiction;

import java.util.Collection;

/**
 * Created by 边江洲 on 2017/11/22.
 */
/*
 适用于普通列表场景模式
  */
public abstract class JZNormalRecycler<T> extends JZBaseRecycler<T> {

    public JZNormalRecycler(Context context) {
        super(context);
    }

    public JZNormalRecycler(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JZNormalRecycler(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void initView() {
        super.initView();
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener());
    }

    public class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

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
            /* 当滑动停止的时候 */
            if (newState == 0) {
                int fristVisiblePosition = manager.findFirstVisibleItemPosition();
                if (scrollStopListener != null) {
                    scrollStopListener.stop(fristVisiblePosition, manager);
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    }

    @Override
    public int getLayoutResId() {
        return R.layout.jz_base_recycler_view;
    }

    @Override
    public void setData(Collection<T> datas) {
        if (datas != null) {
            if (adapter == null) {
                dataJudgeNoDataViewShowState(datas);
                adapter = new MyAdapter(datas);
                recyclerView.setAdapter(adapter);
            } else {
                switch (adapterState) {
                    case JZDiction.AdapterState.AdapterRefresh:
                        if (datas != null && datas.size() > 0) {
                            adapter.refresh(datas);
                        }
                        dataJudgeNoDataViewShowState(datas);
                        break;
                    case JZDiction.AdapterState.AdapterLoadMore:
                        adapter.loadmore(datas);
                        if (datas.size() == 0) {
                            JZToast.showShortToast(context, "亲，没有更多数据了");
                        }
                        break;
                    default:
                        break;
                }
                if (datas.size() == 0 && page > 1 && JZDiction.AdapterState.AdapterLoadMore.equals(adapterState)) {
                    page--;
                }
            }
        }
    }

    @Override
    public void setDataRetart(Collection<T> datas) {
        if (datas != null) {
            if (adapter == null) {
                dataJudgeNoDataViewShowState(datas);
                adapter = new MyAdapter(datas);
                recyclerView.setAdapter(adapter);
            } else {
                switch (adapterState) {
                    case JZDiction.AdapterState.AdapterRefresh:
                        if (datas != null) {
                            adapter.refresh(datas);
                        }
                        dataJudgeNoDataViewShowState(datas);
                        break;
                    case JZDiction.AdapterState.AdapterLoadMore:
                        adapter.loadmore(datas);
                        if (datas.size() == 0) {
                            JZToast.showShortToast(context, "亲，没有更多数据了");
                        }
                        break;
                    default:
                        break;
                }
                if (datas.size() == 0 && page > 1 && JZDiction.AdapterState.AdapterLoadMore.equals(adapterState)) {
                    page--;
                }
            }
        }
    }

}
