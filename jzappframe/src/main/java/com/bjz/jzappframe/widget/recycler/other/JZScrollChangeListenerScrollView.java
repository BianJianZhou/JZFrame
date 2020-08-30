package com.bjz.jzappframe.widget.recycler.other;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by 边江洲 on 2017/8/24.
 */

public class JZScrollChangeListenerScrollView extends ScrollView {

    Context context;

    TMScrollViewScrollChangeListener scrollViewListener;

    public JZScrollChangeListenerScrollView(Context context) {
        this(context, null);
    }

    public JZScrollChangeListenerScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JZScrollChangeListenerScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }

    public interface TMScrollViewScrollChangeListener {
        void onScrollChanged(ScrollView scrollView, int x, int y, int oldx, int oldy);
    }

    public void setTMScrollViewScrollChangeListener(TMScrollViewScrollChangeListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }



}
