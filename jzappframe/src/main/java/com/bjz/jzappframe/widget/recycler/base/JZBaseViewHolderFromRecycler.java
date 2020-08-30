package com.bjz.jzappframe.widget.recycler.base;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by 边江洲 on 2017/9/1.
 */

public class JZBaseViewHolderFromRecycler<T> extends RecyclerView.ViewHolder {

    public JZBaseView<T> baseItemView;
    public View itemView;
    public TextView text;
    public ImageView img;
    public RelativeLayout rl;
    public LinearLayout ll;

    public JZBaseViewHolderFromRecycler(JZBaseView itemView) {
        super(itemView);
        this.baseItemView = itemView;
    }

    public JZBaseViewHolderFromRecycler(View itemView) {
        super(itemView);
        this.itemView = itemView;
    }

    public JZBaseViewHolderFromRecycler(TextView itemView) {
        super(itemView);
        text = itemView;
    }

    public JZBaseViewHolderFromRecycler(ImageView itemView) {
        super(itemView);
        img = itemView;
    }

    public JZBaseViewHolderFromRecycler(RelativeLayout itemView) {
        super(itemView);
        rl = itemView;
    }

    public JZBaseViewHolderFromRecycler(LinearLayout itemView) {
        super(itemView);
        ll = itemView;
    }
}
