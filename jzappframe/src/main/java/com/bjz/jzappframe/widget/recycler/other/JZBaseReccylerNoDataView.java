package com.bjz.jzappframe.widget.recycler.other;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bjz.jzappframe.R;
import com.bjz.jzappframe.utils.JZUtil;
import com.bjz.jzappframe.widget.recycler.item.JZBaseItemView_FromRes;
import com.bjz.jzappframe.widget.recycler.listener.IJZRecyclerNoDataBtnClickListener;

/**
 * ==================================
 * Created by 边江洲 on 2018/6/24.
 * 作    者：WY_BJZ
 * 创建时间：2018/6/24
 * ==================================
 */
/*
 类 说 明：
 
 参数描述：
 
 
*/
public class JZBaseReccylerNoDataView extends JZBaseItemView_FromRes<String> {

    /* 无数据按钮点击监听 */
    IJZRecyclerNoDataBtnClickListener noDataBtnClickListener;

    private TextView
            noDataExplanText,
            noDataBtnText,
            noDataIconText;
    private String noDataTopStr = "", noDataBottomStr = "";

    LinearLayout bigLl;

    public JZBaseReccylerNoDataView(Context context) {
        super(context);
    }

    public JZBaseReccylerNoDataView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JZBaseReccylerNoDataView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.jz_list_nodata_view;
    }

    @Override
    protected void bindView(View itemView) {
        noDataExplanText = bind(R.id.list_no_data_view_fail_text);
        noDataBtnText = bind(R.id.list_no_data_view_btn_text);
        noDataIconText = bind(R.id.list_no_data_fial_icon_text);

        bigLl = bind(R.id.list_nodata_big_ll);
    }

    @Override
    protected void onCreate(Context context, AttributeSet attrs, int defStyleAttr) {

    }

    @Override
    protected void initView(String data) {
        noDataBtnText.setOnClickListener(view -> {
            if (noDataBtnClickListener != null) {
                noDataBtnClickListener.click();
            }
            JZUtil.viewClickBigAnim(context, noDataBtnText);
        });
    }

    /* 数据判断 无数据提示页面 显示状态 */
    public JZBaseReccylerNoDataView dataJudgeNoDataViewShowState() {
        if (!"".equals(noDataExplanText)) {
            noDataExplanText.setText(noDataTopStr);
        }
        return this;
    }

    public JZBaseReccylerNoDataView setNoDataStr(String noDataTopStr, String noDataBottomStr) {
        if (noDataTopStr != null && !"".equals(noDataTopStr)) {
            this.noDataTopStr = noDataTopStr;
        }
        if (noDataBottomStr != null && !"".equals(noDataBottomStr)) {
            this.noDataBottomStr = noDataBottomStr;
        }
        return this;
    }

    public JZBaseReccylerNoDataView setNoDataBtnContentStr(String btnStr) {
        if (btnStr != null && !"".equals(btnStr)) {
            noDataBtnText.setVisibility(VISIBLE);
            noDataBtnText.setText(btnStr);
        } else {
            noDataBtnText.setVisibility(GONE);
        }
        return this;
    }

    public JZBaseReccylerNoDataView setNoDataBkColor(int bkColor) {
        bigLl.setBackgroundColor(bkColor);
        return this;
    }

    public JZBaseReccylerNoDataView setNoDataBkResId(int resId) {
        bigLl.setBackgroundResource(resId);
        return this;
    }

    public JZBaseReccylerNoDataView setNoDataIconRel(int resId) {
        noDataIconText.setBackgroundResource(resId);
        return this;
    }

    public JZBaseReccylerNoDataView setNoDataBtnClickListener(IJZRecyclerNoDataBtnClickListener noDataBtnClickListener) {
        this.noDataBtnClickListener = noDataBtnClickListener;
        return this;
    }
}
