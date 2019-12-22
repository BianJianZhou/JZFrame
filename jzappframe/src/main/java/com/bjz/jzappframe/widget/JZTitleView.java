package com.bjz.jzappframe.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bjz.jzappframe.R;
import com.bjz.jzappframe.utils.JZPageAnimUtils;

/**
 * ==================================
 * Created by 边江洲 on 2018/9/15.
 * 作    者：WY_BJZ
 * 创建时间：2018/9/15
 * ==================================
 */
/*
 类 说 明：
 
 参数描述：
 
 
*/
public class JZTitleView extends JZTitleDataView {
    /* 底部分割线是否显示 */
    boolean
            isShowBottomHorView = true;

    /* title文案 */
    String
            titleStr = "";

    TextView
            titleText,
            backText;

    RelativeLayout
            bigRl,
            backRl;

    View
            bottomHorView;

    RelativeLayout
            closeText;

    public JZTitleView(Context context) {
        super(context);
    }

    public JZTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JZTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Base_TMTitleView);
        if (ta != null) {
            titleStr = ta.getString(R.styleable.Base_TMTitleView_titleText);
            isShowBottomHorView = ta.getBoolean(R.styleable.Base_TMTitleView_bottomHorViewIsShow, false);
            ta.recycle();
        }
    }

    @Override
    public int getLayoutRes() {
        return R.layout.base_title_view;
    }

    @Override
    public void bindView() {
        backRl = bind(R.id.base_title_back_rl);
        titleText = bind(R.id.base_title_text);
        bigRl = bind(R.id.base_title_view_big_rl);
        backText = bind(R.id.base_title_view_back_text);
        closeText = bind(R.id.base_title_view_close_rl);

        rightGroupRl = bind(R.id.base_title_view_right_group);
        bottomHorView = bind(R.id.base_title_view_bottom_hor_view);

        titleText.setText(titleStr);

        if (isShowBottomHorView) {
            bottomHorView.setVisibility(VISIBLE);
        } else {
            bottomHorView.setVisibility(GONE);
        }

        backRl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (backListener != null) {
                    backListener.back();
                } else {
                    if (mContext != null) {
                        Activity activity = (Activity) mContext;
                        activity.finish();
                        JZPageAnimUtils.finishActivityAnim(activity);
                    }
                }
            }
        });

        closeText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (closeListener != null) {
                    closeListener.close();
                } else {
                    if (mContext != null) {
                        Activity activity = (Activity) mContext;
                        activity.finish();
                        JZPageAnimUtils.finishActivityAnim(activity);
                    }
                }
            }
        });
    }

    public void setBackIsShow(boolean isShow) {
        if (isShow) {
            backRl.setVisibility(VISIBLE);
        } else {
            backRl.setVisibility(GONE);
        }
    }

    @Override
    public void setBackgroundColor(int color) {
        bigRl.setBackgroundColor(color);
    }

    /* 这里设置的主题之后 相当于设置 bottomHorView 是否显示 属性不生效了 */
    /* 设置当前title的主题模式 */
    public JZTitleView setTheme(String theme) {
        switch (theme) {
            case App_Title_Theme_Color_Drak:
                bigRl.setBackgroundColor(getResources().getColor(R.color._39414d));
                backText.setBackgroundResource(R.mipmap.tm_back_write_icon);
                titleText.setTextColor(getResources().getColor(R.color._ffffff));
                bottomHorView.setBackgroundColor(getResources().getColor(R.color._39414d));
                break;
            case App_Title_Theme_Color_Tint:
                bigRl.setBackgroundColor(getResources().getColor(R.color._ffffff));
                backText.setBackgroundResource(R.mipmap.tm_back_black_icon);
                titleText.setTextColor(getResources().getColor(R.color._39414d));
                bottomHorView.setBackgroundColor(getResources().getColor(R.color._f4f4f4));
                break;
            case App_Title_Theme_Color_All:
                break;
            default:
                break;
        }
        return this;
    }

    /* 设置当前title的主题模式 */
    public JZTitleView setContentTheme(String theme) {
        switch (theme) {
            case App_Title_Theme_Color_Drak:
                backText.setBackgroundResource(R.mipmap.tm_back_write_icon);
                titleText.setTextColor(getResources().getColor(R.color._ffffff));
                bottomHorView.setBackgroundColor(getResources().getColor(R.color._39414d));
                break;
            case App_Title_Theme_Color_Tint:
                backText.setBackgroundResource(R.mipmap.tm_back_black_icon);
                titleText.setTextColor(getResources().getColor(R.color._39414d));
                bottomHorView.setBackgroundColor(getResources().getColor(R.color._f4f4f4));
                break;
            case App_Title_Theme_Color_All:
                break;
            default:
                break;
        }
        return this;
    }

    public JZTitleView setCloseTextVisible() {
        closeText.setVisibility(VISIBLE);
        return this;
    }

    /* 调用 setData() 方法赋值 title 字符*/
    @Override
    public void initView(String data) {
        titleText.setText(data);
    }


    /* 获取右侧布局 */
    public RelativeLayout getRightView() {
        return rightGroupRl;
    }

    /* 设置底部横线是否显示 */
    public JZTitleView setShowBottomHorView(boolean showBottomHorView) {
        isShowBottomHorView = showBottomHorView;
        if (showBottomHorView) {
            bottomHorView.setVisibility(VISIBLE);
        } else {
            bottomHorView.setVisibility(GONE);
        }
        return this;
    }

    /* 设置底部横线背景色 */
    public JZTitleView setBottomHorViewBackground(int color) {
        bottomHorView.setBackgroundColor(color);
        return this;
    }

    /* 设置关闭按钮点击监听
     */
    public JZTitleView setCloseListener(TMTitleCloseListener listener) {
        closeListener = listener;
        return this;
    }

    public void cancleAllRightContent() {
        if (getRightJiaoHuGroup() != null) {
            getRightJiaoHuGroup().removeAllViews();
        }
        if (getRightTextGroup() != null) {
            getRightTextGroup().removeAllViews();
        }
    }

    TMTitleCloseListener closeListener;

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

    TMTitleBackListener backListener;

    public interface TMTitleBackListener {
        void back();
    }
}
