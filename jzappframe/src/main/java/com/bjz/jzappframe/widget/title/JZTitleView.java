package com.bjz.jzappframe.widget.title;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.bjz.jzappframe.JZUIFrameManager;
import com.bjz.jzappframe.JZViewsConfigBuilder;
import com.bjz.jzappframe.utils.JZPageAnimUtils;

public class JZTitleView extends JZTitleDataView {

    private Context context;
    private JZViewsConfigBuilder viewsBuilder;

    /* x号按钮 */
    private View xIconView;

    /* 返回按钮 */
    private View backBtnView;

    /* 标题文字 */
    private TextView titleText;

    /* 底部横线 */
    private View bottomHorLineView;

    /* 关闭回调点击 */
    private TMTitleCloseListener closeListener;

    /* 返回点击回调 */
    private TMTitleBackListener backListener;

    public JZTitleView(Context context) {
        this(context, null);
    }

    public JZTitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JZTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        viewsBuilder = JZUIFrameManager.getInstance().getViewsConfigBuilder();
        initView();
        setListener();
    }

    /**
     * 初始化组件
     */
    private void initView() {
        //设置最外层容器的宽高
        LayoutParams groupParams = new LayoutParams(LayoutParams.MATCH_PARENT, viewsBuilder.getTitleViewH());
        setLayoutParams(groupParams);
        setGravity(Gravity.CENTER_VERTICAL);
        /* 返回按键 */
        backBtnView = new View(context);
        LayoutParams backBtnViewParams = new LayoutParams(viewsBuilder.getBackBtnW(), viewsBuilder.getBackBtnH());
        backBtnViewParams.setMargins(viewsBuilder.getBackBtnMarginLeft(), 0, 0, 0);
        backBtnViewParams.addRule(RelativeLayout.CENTER_VERTICAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            backBtnView.setBackground(new BitmapDrawable(viewsBuilder.getBackBtnBitmap()));
        }
        addView(backBtnView, backBtnViewParams);
        /* 差号按钮 */
        xIconView = new View(context);
        RelativeLayout.LayoutParams xIconViewParams = new LayoutParams(viewsBuilder.getxIconW(), viewsBuilder.getxIconH());
        xIconViewParams.setMargins(viewsBuilder.getxIconMarginLeft(), 0, 0, 0);
        xIconViewParams.addRule(RelativeLayout.CENTER_VERTICAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            xIconView.setBackground(new BitmapDrawable(viewsBuilder.getxIconBitmap()));
        }
        addView(xIconView, xIconViewParams);
        xIconView.setVisibility(View.GONE);
        /* titleText */
        titleText = new TextView(context);
        titleText.setTextSize(viewsBuilder.getTitleTextSize());
        titleText.setTextColor(viewsBuilder.getTitleTextColor());
        RelativeLayout.LayoutParams titleTextParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        titleTextParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(titleText, titleTextParams);
        /* 添加右侧容器 */
        rightGroupRl = new RelativeLayout(context);
        RelativeLayout.LayoutParams rightGrouRlParmas = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rightGrouRlParmas.setMargins(0, 0, viewsBuilder.getTitleRightGroupMarginRight(), 0);
        rightGrouRlParmas.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        addView(rightGroupRl, rightGrouRlParmas);
        /* 底部横线 */
        bottomHorLineView = new View(context);
        RelativeLayout.LayoutParams horLineViewParams = new LayoutParams(LayoutParams.MATCH_PARENT, viewsBuilder.getTitleBottomHorLineViewH());
        horLineViewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        addView(bottomHorLineView, horLineViewParams);
        bottomHorLineView.setBackgroundColor(viewsBuilder.getTitleBottomHorLineColor());
    }

    private void setListener() {
        backBtnView.setOnClickListener(view -> {
            if (backListener != null) {
                backListener.back();
            } else {
                if (context != null) {
                    if (context instanceof Activity) {
                        Activity activity = (Activity) context;
                        activity.finish();
                        JZPageAnimUtils.finishActivityAnim(activity);
                    } else if (context instanceof AppCompatActivity) {
                        AppCompatActivity activity = (AppCompatActivity) context;
                        activity.finish();
                        JZPageAnimUtils.finishActivityAnim(activity);
                    } else if (context instanceof FragmentActivity) {
                        FragmentActivity activity = (FragmentActivity) context;
                        activity.finish();
                        JZPageAnimUtils.finishActivityAnim(activity);
                    }
                }
            }
        });

        xIconView.setOnClickListener(v -> {
            if (closeListener != null) {
                closeListener.close();
            } else {
                if (context != null) {
                    if (context instanceof Activity) {
                        Activity activity = (Activity) context;
                        activity.finish();
                        JZPageAnimUtils.finishActivityAnim(activity);
                    } else if (context instanceof AppCompatActivity) {
                        AppCompatActivity activity = (AppCompatActivity) context;
                        activity.finish();
                        JZPageAnimUtils.finishActivityAnim(activity);
                    } else if (context instanceof FragmentActivity) {
                        FragmentActivity activity = (FragmentActivity) context;
                        activity.finish();
                        JZPageAnimUtils.finishActivityAnim(activity);
                    }
                }
            }
        });
    }

    /* 设置差号是否显示 */
    private void xIconIsShow(boolean isShow) {
        if (isShow) {
            xIconView.setVisibility(View.VISIBLE);
        } else {
            xIconView.setVisibility(View.GONE);
        }
    }

    /* 设置标题 */
    public void setTitle(String str) {
        if (str != null) {
            titleText.setText(str);
        }
    }

    /* 设置底部横线是否显示 */
    public JZTitleView setShowBottomHorView(boolean isShow) {
        if (isShow) {
            bottomHorLineView.setVisibility(VISIBLE);
        } else {
            bottomHorLineView.setVisibility(GONE);
        }
        return this;
    }

    /* 设置底部横线背景色 */
    public JZTitleView setBottomHorViewBackground(int color) {
        bottomHorLineView.setBackgroundColor(color);
        return this;
    }

    /* 设置关闭按钮点击监听
     */
    public JZTitleView setCloseListener(TMTitleCloseListener listener) {
        closeListener = listener;
        return this;
    }

    public void clear() {
        if (getRightTextGroup() != null) {
            getRightTextGroup().removeAllViews();
        }
    }

    public interface TMTitleCloseListener {
        void close();
    }

    /* 设置左侧返回按钮监听
        假如设置了左侧返回按钮点击监听那本组件的返回点击监听不生效
      */
    public JZTitleView setBackListener(TMTitleBackListener listener) {
        backListener = listener;
        return this;
    }

    public interface TMTitleBackListener {
        void back();
    }

    /* 获取右侧容器 */
    public RelativeLayout getRightGroupView() {
        return rightGroupRl;
    }
}
