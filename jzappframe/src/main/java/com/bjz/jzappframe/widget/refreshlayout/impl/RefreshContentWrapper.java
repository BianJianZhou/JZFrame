package com.bjz.jzappframe.widget.refreshlayout.impl;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.PointF;
import android.os.Build;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Space;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.ScrollingView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.PagerAdapterWrapper;
import androidx.viewpager.widget.ViewPager;

import com.bjz.jzappframe.widget.refreshlayout.api.RefreshContent;
import com.bjz.jzappframe.widget.refreshlayout.api.RefreshKernel;
import com.bjz.jzappframe.widget.refreshlayout.api.RefreshLayout;
import com.bjz.jzappframe.widget.refreshlayout.api.RefreshScrollBoundary;
import com.bjz.jzappframe.widget.refreshlayout.constant.RefreshState;
import com.bjz.jzappframe.widget.refreshlayout.util.ScrollBoundaryUtil;

import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.bjz.jzappframe.widget.refreshlayout.util.ScrollBoundaryUtil.isTransformedTouchPointInView;

/**
 * 刷新内容包装
 * Created by SCWANG on 2017/5/26.
 */

public class RefreshContentWrapper implements RefreshContent {

    private int mHeaderHeight = Integer.MAX_VALUE;
    private int mFooterHeight = mHeaderHeight - 1;
    private View mContentView;
    private View mRealContentView;
    private View mScrollableView;
    private View mFixedHeader;
    private View mFixedFooter;
    private MotionEvent mMotionEvent;
    private RefreshScrollBoundaryAdapter mBoundaryAdapter = new RefreshScrollBoundaryAdapter();

    public RefreshContentWrapper(View view) {
        this.mContentView = mRealContentView = view;
        this.findScrollableView(view);
    }

    public RefreshContentWrapper(Context context) {
        this.mContentView = mRealContentView = new View(context);
        this.findScrollableView(mContentView);
    }

    //<editor-fold desc="findScrollableView">
    private void findScrollableView(View content) {
        mScrollableView = findScrollableViewInternal(content, true);
        if (mScrollableView instanceof NestedScrollingParent
                && !(mScrollableView instanceof NestedScrollingChild)) {
            mScrollableView = findScrollableViewInternal(mScrollableView, false);
        }
        if (mScrollableView instanceof ViewPager) {
            wrapperViewPager((ViewPager) this.mScrollableView);
        }
        if (mScrollableView == null) {
            mScrollableView = content;
        }
    }

    private void wrapperViewPager(final ViewPager viewPager) {
        wrapperViewPager(viewPager, null);
    }

    private void wrapperViewPager(final ViewPager viewPager, PagerPrimaryAdapter primaryAdapter) {
        viewPager.post(new Runnable() {
            int count = 0;
            PagerPrimaryAdapter mAdapter = primaryAdapter;
            @Override
            public void run() {
                count++;
                PagerAdapter adapter = viewPager.getAdapter();
                if (adapter != null) {
                    if (adapter instanceof PagerPrimaryAdapter) {
                        if (adapter == primaryAdapter) {
                            viewPager.postDelayed(this, 500);
                        }
                    } else {
                        if (mAdapter == null) {
                            mAdapter = new PagerPrimaryAdapter(adapter);
                        } else {
                            mAdapter.wrapper(adapter);
                        }
                        mAdapter.attachViewPager(viewPager);
                    }
                } else if (count < 10) {
                    viewPager.postDelayed(this, 500);
                }
            }
        });
    }

    private View findScrollableViewInternal(View content, boolean selfable) {
        View scrollableView = null;
        Queue<View> views = new LinkedBlockingQueue<>(Collections.singletonList(content));
        while (!views.isEmpty() && scrollableView == null) {
            View view = views.poll();
            if (view != null) {
                if ((selfable || view != content) && (view instanceof AbsListView
                        || view instanceof ScrollView
                        || view instanceof ScrollingView
                        || view instanceof NestedScrollingChild
                        || view instanceof NestedScrollingParent
                        || view instanceof WebView
                        || view instanceof ViewPager)) {
                    scrollableView = view;
                } else if (view instanceof ViewGroup) {
                    ViewGroup group = (ViewGroup) view;
                    for (int j = 0; j < group.getChildCount(); j++) {
                        views.add(group.getChildAt(j));
                    }
                }
            }
        }
        return scrollableView;
    }
    //</editor-fold>

    //<editor-fold desc="implements">
    @NonNull
    public View getView() {
        return mContentView;
    }

    @Override
    public boolean isNestedScrollingChild(MotionEvent e) {
        MotionEvent event = MotionEvent.obtain(e);
        event.offsetLocation(-mContentView.getLeft(), -mContentView.getTop() - mRealContentView.getTranslationY());
        boolean isNested = isNestedScrollingChild(mContentView, event);
        event.recycle();
        return isNested;
    }

    private boolean isNestedScrollingChild(View targetView, MotionEvent event) {
        if (targetView instanceof NestedScrollingChild
                || (Build.VERSION.SDK_INT >= 21 && targetView.isNestedScrollingEnabled())) {
            return true;
        }
        if (targetView instanceof ViewGroup && event != null) {
            ViewGroup viewGroup = (ViewGroup) targetView;
            final int childCount = viewGroup.getChildCount();
            PointF point = new PointF();
            for (int i = childCount; i > 0; i--) {
                View child = viewGroup.getChildAt(i - 1);
                if (isTransformedTouchPointInView(viewGroup,child, event.getX(), event.getY() , point)) {
                    event = MotionEvent.obtain(event);
                    event.offsetLocation(point.x, point.y);
                    return isNestedScrollingChild(child, event);
                }
            }
        }
        return false;
    }

    @Override
    public void moveSpinner(int spinner) {
        mRealContentView.setTranslationY(spinner);
        if (mFixedHeader != null) {
            mFixedHeader.setTranslationY(Math.max(0, spinner));
        }
        if (mFixedFooter != null) {
            mFixedFooter.setTranslationY(Math.min(0, spinner));
        }
    }

    @Override
    public boolean canScrollUp() {
        return mBoundaryAdapter.canPullDown(mContentView);
    }

    @Override
    public boolean canScrollDown() {
        return mBoundaryAdapter.canPullUp(mContentView);
    }

    @Override
    public void measure(int widthSpec, int heightSpec) {
        mContentView.measure(widthSpec, heightSpec);
    }

    @Override
    public ViewGroup.LayoutParams getLayoutParams() {
        return mContentView.getLayoutParams();
    }

    @Override
    public int getMeasuredWidth() {
        return mContentView.getMeasuredWidth();
    }

    @Override
    public int getMeasuredHeight() {
        return mContentView.getMeasuredHeight();
    }

    @Override
    public void layout(int left, int top, int right, int bottom) {
        mContentView.layout(left, top, right, bottom);
    }

    @Override
    public View getScrollableView() {
        return mScrollableView;
    }

    @Override
    public void onActionDown(MotionEvent e) {
        mMotionEvent = MotionEvent.obtain(e);
        mMotionEvent.offsetLocation(-mContentView.getLeft(), -mContentView.getTop());
        mBoundaryAdapter.setActionEvent(mMotionEvent);
    }

    @Override
    public void onActionUpOrCancel() {
        mMotionEvent = null;
        mBoundaryAdapter.setActionEvent(null);
    }

    @Override
    public void setupComponent(RefreshKernel kernel, View fixedHeader, View fixedFooter) {
        if (mScrollableView instanceof RecyclerView) {
            RecyclerViewScrollComponent component = new RecyclerViewScrollComponent(kernel);
            component.attach((RecyclerView) mScrollableView);
        } else if (mScrollableView instanceof AbsListView) {
            AbsListViewScrollComponent component = new AbsListViewScrollComponent(kernel);
            component.attach(((AbsListView) mScrollableView));
        } else if (Build.VERSION.SDK_INT >= 23 && mScrollableView != null) {
            mScrollableView.setOnScrollChangeListener(new Api23ViewScrollComponent(kernel));
        }
        if (Build.VERSION.SDK_INT >= 21
                && mScrollableView instanceof ListView
                && !(mScrollableView instanceof NestedScrollingChild)) {
            mScrollableView.setNestedScrollingEnabled(true);
        }
        if (fixedHeader != null || fixedFooter != null) {
            mFixedHeader = fixedHeader;
            mFixedFooter = fixedFooter;
            FrameLayout frameLayout = new FrameLayout(mContentView.getContext());
            kernel.getRefreshLayout().getLayout().removeView(mContentView);
            ViewGroup.LayoutParams layoutParams = mContentView.getLayoutParams();
            frameLayout.addView(mContentView, MATCH_PARENT, MATCH_PARENT);
            kernel.getRefreshLayout().getLayout().addView(frameLayout, layoutParams);
            mContentView = frameLayout;
            if (fixedHeader != null) {
                fixedHeader.setClickable(true);
                ViewGroup.LayoutParams lp = fixedHeader.getLayoutParams();
                ViewGroup parent = (ViewGroup) fixedHeader.getParent();
                int index = parent.indexOfChild(fixedHeader);
                parent.removeView(fixedHeader);
                lp.height = measureViewHeight(fixedHeader);
                parent.addView(new Space(mContentView.getContext()), index, lp);
                frameLayout.addView(fixedHeader);
            }
            if (fixedFooter != null) {
                fixedFooter.setClickable(true);
                ViewGroup.LayoutParams lp = fixedFooter.getLayoutParams();
                ViewGroup parent = (ViewGroup) fixedFooter.getParent();
                int index = parent.indexOfChild(fixedFooter);
                parent.removeView(fixedFooter);
                FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(lp);
                lp.height = measureViewHeight(fixedFooter);
                parent.addView(new Space(mContentView.getContext()), index, lp);
                flp.gravity = Gravity.BOTTOM;
                frameLayout.addView(fixedFooter, flp);
            }
        }
    }

    @Override
    public void onInitialHeaderAndFooter(int headerHeight, int footerHeight) {
        mHeaderHeight = headerHeight;
        mFooterHeight = footerHeight;
    }

    @Override
    public void setRefreshScrollBoundary(RefreshScrollBoundary boundary) {
        if (boundary instanceof RefreshScrollBoundaryAdapter) {
            mBoundaryAdapter = ((RefreshScrollBoundaryAdapter) boundary);
        } else {
            mBoundaryAdapter.setRefreshScrollBoundary(boundary);
        }
    }

    @Override
    public AnimatorUpdateListener onLoadingFinish(RefreshKernel kernel, int footerHeight, int startDelay, Interpolator interpolator, int duration) {
        if (mScrollableView != null && kernel.getRefreshLayout().isEnableScrollContentWhenLoaded()) {
            if (mScrollableView instanceof AbsListView && Build.VERSION.SDK_INT < 19) {
                if (startDelay > 0) {
                    kernel.getRefreshLayout().getLayout().postDelayed(() -> ((AbsListView) mScrollableView).smoothScrollBy(footerHeight, duration), startDelay);
                } else {
                    ((AbsListView) mScrollableView).smoothScrollBy(footerHeight, duration);
                }
                return null;
            }
            if (!ScrollBoundaryUtil.canScrollDown(mScrollableView)) {
                return null;
            }
            return new AnimatorUpdateListener() {
                int lastValue = kernel.getSpinner();
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int thisValue = (int) animation.getAnimatedValue();
                    if (mScrollableView instanceof AbsListView && Build.VERSION.SDK_INT >= 19) {
                        ((AbsListView) mScrollableView).scrollListBy(thisValue - lastValue);
                    } else {
                        mScrollableView.scrollBy(0, thisValue - lastValue);
                    }
                    lastValue = thisValue;
                }
            };
//                    if (mScrollableView instanceof RecyclerView) ((RecyclerView) mScrollableView).smoothScrollBy(0, footerHeight, interpolator);
//                    else if (mScrollableView instanceof ScrollView) ((ScrollView) mScrollableView).smoothScrollBy(0, footerHeight);
//                    else if (mScrollableView instanceof AbsListView) ((AbsListView) mScrollableView).smoothScrollBy(footerHeight, duration);
//                    else {
//                        try {
//                            Method method = mScrollableView.getClass().getDeclaredMethod("smoothScrollBy", Integer.class, Integer.class);
//                            method.invoke(mScrollableView, 0, footerHeight);
//                        } catch (Exception ignored) {
//                        }
//                    }
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold desc="滚动组件">
    @RequiresApi(api = Build.VERSION_CODES.M)
    private class Api23ViewScrollComponent implements View.OnScrollChangeListener {
        long lastTime = 0;
        long lastTimeOld = 0;
        int lastScrollY = 0;
        int lastOldScrollY = 0;
        RefreshKernel kernel;

        Api23ViewScrollComponent(RefreshKernel kernel) {
            this.kernel = kernel;
        }

        @Override
        public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            if (lastScrollY == scrollY && lastOldScrollY == oldScrollY) {
                return;
            }
//            System.out.printf("%d,%d,%d,%d\n", scrollX, scrollY, oldScrollX, oldScrollY);
            RefreshLayout layout = kernel.getRefreshLayout();
            boolean overScroll = layout.isEnableOverScrollBounce() || layout.isRefreshing() || layout.isLoading();
            if (scrollY <= 0 && oldScrollY > 0 && mMotionEvent == null && lastTime - lastTimeOld > 1000 && overScroll && layout.isEnableRefresh()) {
                //time:16000000 value:160
                final int velocity = (lastOldScrollY - oldScrollY) * 16000 / (int)((lastTime - lastTimeOld)/1000f);
//                    System.out.println("ValueAnimator - " + (lastTime - lastTimeOld) + " - " + velocity+"("+(lastOldScrollY - oldScrollY)+")");
                kernel.animSpinnerBounce(Math.min(velocity, mHeaderHeight));
            } else if (oldScrollY < scrollY && mMotionEvent == null && overScroll && layout.isEnableLoadmore()) {
                if (lastTime - lastTimeOld > 1000 && !ScrollBoundaryUtil.canScrollDown(mScrollableView)) {
                    final int velocity = (lastOldScrollY - oldScrollY) * 16000 / (int)((lastTime - lastTimeOld)/1000f);
//                    System.out.println("ValueAnimator - " + (lastTime - lastTimeOld) + " - " + velocity+"("+(lastOldScrollY - oldScrollY)+")");
                    kernel.animSpinnerBounce(Math.max(velocity, -mFooterHeight));
                }
            }
            lastScrollY = scrollY;
            lastOldScrollY = oldScrollY;
            lastTimeOld = lastTime;
            lastTime = System.nanoTime();
        }
    }

    private class AbsListViewScrollComponent implements AbsListView.OnScrollListener {

        int scrollY;
        int scrollDy;
        int lastScrolly;
        int lastScrollDy;
        RefreshKernel kernel;
        SparseArray<ItemRecod> recordSp = new SparseArray<>(0);

        AbsListViewScrollComponent(RefreshKernel kernel) {
            this.kernel = kernel;
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            lastScrolly = scrollY;
            lastScrollDy = scrollDy;
            scrollY = getScrollY(absListView, firstVisibleItem);
            scrollDy = lastScrolly - scrollY;

            final int dy =lastScrollDy + scrollDy;
            if (totalItemCount > 0) {
                RefreshLayout layout = kernel.getRefreshLayout();
                boolean overScroll = (layout.isEnableOverScrollBounce() || layout.isRefreshing() || layout.isLoading());
                if (mMotionEvent == null && dy > 0 && firstVisibleItem == 0) {
                    if (overScroll
                            && layout.isEnableRefresh()
                            && !ScrollBoundaryUtil.canScrollUp(absListView)) {
                        kernel.animSpinnerBounce(Math.min(dy, mHeaderHeight));
                    }
                } else if (dy < 0) {
                    int lastVisiblePosition = absListView.getLastVisiblePosition();
                    if (lastVisiblePosition == totalItemCount - 1 && lastVisiblePosition > 0) {
                        if (layout.isEnableLoadmore() && !layout.isLoadmoreFinished() && layout.isEnableAutoLoadmore()
                                && layout.getState() == RefreshState.None
                                && !ScrollBoundaryUtil.canScrollDown(absListView)) {
                            kernel.getRefreshLayout().autoLoadmore(0, 1);
                        } else if (mMotionEvent == null && overScroll && !ScrollBoundaryUtil.canScrollDown(absListView)) {
                            kernel.animSpinnerBounce(Math.max(dy, -mFooterHeight));
                        }
                    }
                }
            }

        }

        void attach(AbsListView listView) {
            listView.setOnScrollListener(this);
        }

        private int getScrollY(AbsListView view, int firstVisibleItem) {
            View firstView = view.getChildAt(0);
            if (null != firstView) {
                ItemRecod itemRecord = recordSp.get(firstVisibleItem);
                if (null == itemRecord) {
                    itemRecord = new ItemRecod();
                }
                itemRecord.height = firstView.getHeight();
                itemRecord.top = firstView.getTop();
                recordSp.append(firstVisibleItem, itemRecord);

                int height = 0,lastheight = 0;
                for (int i = 0; i < firstVisibleItem; i++) {
                    ItemRecod itemRecod = recordSp.get(i);
                    if (itemRecod != null) {
                        height += itemRecod.height;
                        lastheight = itemRecod.height;
                    } else {
                        height += lastheight;
                    }
                }
                ItemRecod itemRecod = recordSp.get(firstVisibleItem);
                if (null == itemRecod) {
                    itemRecod = new ItemRecod();
                }
                return height - itemRecod.top;
            }
            return 0;
        }

        class ItemRecod {
            int height = 0;
            int top = 0;
        }
    }

    private class RecyclerViewScrollComponent extends RecyclerView.OnScrollListener {
        int lastDy;
        long lastFlingTime;
        RefreshKernel kernel;

        RecyclerViewScrollComponent(RefreshKernel kernel) {
            this.kernel = kernel;
        }
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            RefreshLayout layout = kernel.getRefreshLayout();
            if (newState == RecyclerView.SCROLL_STATE_IDLE && mMotionEvent == null) {
                boolean intime = System.currentTimeMillis() - lastFlingTime < 1000;
                boolean overScroll = layout.isEnableOverScrollBounce() || layout.isRefreshing() || layout.isLoading();
                if (lastDy < -1 && intime && overScroll && layout.isEnableRefresh()) {
                    kernel.animSpinnerBounce(Math.min(-lastDy * 2, mHeaderHeight));
                } else if (layout.isEnableLoadmore()
                        && !layout.isLoadmoreFinished()
                        && layout.isEnableAutoLoadmore()
                        && layout.getState() == RefreshState.None) {
//                    RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
//                    if (manager instanceof LinearLayoutManager) {
//                        LinearLayoutManager linearManager = ((LinearLayoutManager) manager);
//                        int lastVisiblePosition = linearManager.findLastVisibleItemPosition();
//                        if(lastVisiblePosition >= linearManager.getItemCount() - 1){
//                            kernel.getRefreshLayout().autoLoadmore(0,1);
//                        }
//                    }
                } else if (lastDy > 1 && intime && overScroll && layout.isEnableLoadmore()) {
                    kernel.animSpinnerBounce(Math.max(-lastDy * 2, -mFooterHeight));
                }
                lastDy = 0;
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            lastDy = dy;
            RefreshLayout layout = kernel.getRefreshLayout();
            if (dy > 0
                    && layout.isEnableLoadmore()
                    && !layout.isLoadmoreFinished()
                    && layout.isEnableAutoLoadmore()
                    && layout.getState() == RefreshState.None ){
                RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
                if (manager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = ((LinearLayoutManager) manager);
                    int lastVisiblePosition = linearManager.findLastVisibleItemPosition();
                    if(lastVisiblePosition >= linearManager.getItemCount() - 1
                            && lastVisiblePosition > 0
                            && !ScrollBoundaryUtil.canScrollDown(recyclerView)){
                        kernel.getRefreshLayout().autoLoadmore(0,1);
                    }
                }
            }
        }

        void attach(RecyclerView recyclerView) {
            recyclerView.addOnScrollListener(this);
            recyclerView.setOnFlingListener(new RecyclerView.OnFlingListener() {
                @Override
                public boolean onFling(int velocityX, int velocityY) {
                    lastFlingTime = System.currentTimeMillis();
                    return false;
                }
            });
        }
    }
    //</editor-fold>

    //<editor-fold desc="private">
    private static int measureViewHeight(View view) {
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(MATCH_PARENT,WRAP_CONTENT);
        }
        int childHeightSpec;
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        if (p.height > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(p.height, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        view.measure(childWidthSpec, childHeightSpec);
        return view.getMeasuredHeight();
    }
    //</editor-fold>

    private class PagerPrimaryAdapter extends PagerAdapterWrapper {
        private ViewPager mViewPager;

        PagerPrimaryAdapter(PagerAdapter wrapped) {
            super(wrapped);
        }

        void wrapper(PagerAdapter adapter) {
            wrapped = adapter;
        }

        @Override
        public void attachViewPager(ViewPager viewPager) {
            mViewPager = viewPager;
            super.attachViewPager(viewPager);
        }

        @Override
        public void setViewPagerObserver(DataSetObserver observer) {
            super.setViewPagerObserver(observer);
            if (observer == null) {
                wrapperViewPager(mViewPager, this);
            }
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            if (object instanceof View) {
                mScrollableView = ((View) object);
            } else if (object instanceof Fragment) {
                mScrollableView = ((Fragment) object).getView();
            }
            if (mScrollableView != null) {
                mScrollableView = findScrollableViewInternal(mScrollableView, true);
                if (mScrollableView instanceof NestedScrollingParent
                        && !(mScrollableView instanceof NestedScrollingChild)) {
                    mScrollableView = findScrollableViewInternal(mScrollableView, false);
                }
            }
        }
    }
}
