package com.example.jzframedemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bjz.jzappframe.widget.recycler.JZRefreshRecycler;
import com.bjz.jzappframe.widget.recycler.base.JZBaseViewHolderFromRecycler;

import java.util.List;

public class MyListView extends JZRefreshRecycler<MyData> {
    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(context);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public int getBaseRecyclerItemViewType(int position) {
        return 0;
    }

    @Override
    public int getBaseRecyclerItemCount() {
        return getData().size();
    }

    @Override
    public JZBaseViewHolderFromRecycler<MyData> getViewHolder(ViewGroup parent, int viewType) {
        return new JZBaseViewHolderFromRecycler<MyData>(new MyListItemView(context));
    }

    @Override
    public void initItemView(JZBaseViewHolderFromRecycler holder, int position, List<MyData> data, int viewType) {
        holder.baseItemView.setData(data.get(position));
    }
}
