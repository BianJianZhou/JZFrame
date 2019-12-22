package com.bjz.jzappframe.widget.titleChild;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.bjz.jzappframe.R;

import java.util.HashMap;
import java.util.Map;

/**
 * ==================================
 * Created by 边江洲 on 2018/6/25.
 * 作    者：WY_BJZ
 * 创建时间：2018/6/25
 * ==================================
 */
/*
 类 说 明：
 
 参数描述：
 
 
*/
public class JZTitleRightIconGroup extends LinearLayout {

    Context context;

    /* 交互类型 */
    public static final String JiaoHuType_Prise = "0";
    public static final String JiaoHuType_Attention_hui = "1-1";
    public static final String JiaoHuType_Attention_write = "1-2";
    public static final String JiaoHuType_Collect = "2";
    public static final String JiaoHuType_Share_Hui = "3-1";
    public static final String JiaoHuType_Share_Write = "3-2";
    public static final String JiaoHuType_Report = "4";
    public static final String JiaoHuType_Modification = "5";
    public static final String JiaoHuType_More = "6";
    public static final String JiaoHuType_More_Write = "6-1";  //more_hor_sandian_write
    public static final String JiaoHuType_Sao_Yi_Sao_Write = "7";

    int itemW, itemH;
    int viewLeftMargins, viewRightMargins;

    Map<String, View> viewMap = new HashMap<>();

    public JZTitleRightIconGroup(Context context) {
        this(context, null);
    }

    public JZTitleRightIconGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JZTitleRightIconGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        setLayoutParams(params);
        setOrientation(HORIZONTAL);
        this.context = context;
        itemW = (int) getResources().getDimension(R.dimen.dimens_40);
        itemH = (int) getResources().getDimension(R.dimen.dimens_40);
        viewLeftMargins = (int) getResources().getDimension(R.dimen.dimens_15);
        viewRightMargins = (int) getResources().getDimension(R.dimen.dimens_15);
        setPadding(0, 0, (int) (getResources().getDimension(R.dimen.dimens_20) - viewRightMargins), 0);
    }

    public JZTitleRightIconGroup addItem(String jiaoHuType, OnClickListener itemClickListener) {
        if (jiaoHuType == null || "".equals(jiaoHuType)) {
            throw new RuntimeException("TMTitleView 交互组件-" + getClass().getSimpleName() + "-类型设置为空");
        } else if (
                !JiaoHuType_Prise.equals(jiaoHuType) &&
                        !JiaoHuType_Attention_hui.equals(jiaoHuType) &&
                        !JiaoHuType_Attention_write.equals(jiaoHuType) &&
                        !JiaoHuType_Collect.equals(jiaoHuType) &&
                        !JiaoHuType_Share_Hui.equals(jiaoHuType) &&
                        !JiaoHuType_Share_Write.equals(jiaoHuType) &&
                        !JiaoHuType_Report.equals(jiaoHuType) &&
                        !JiaoHuType_Modification.equals(jiaoHuType) &&
                        !JiaoHuType_Sao_Yi_Sao_Write.equals(jiaoHuType) &&
                        !JiaoHuType_More.equals(jiaoHuType) &&
                        !JiaoHuType_More_Write.equals(jiaoHuType)) {
            throw new RuntimeException("TMTitleView 交互组件-" + getClass().getSimpleName() + "-类型设置不匹配");
        }
        if (!viewMap.containsKey(jiaoHuType)) {
            View view = new View(context);
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.setMargins(viewLeftMargins, 0, viewRightMargins, 0);
        /* 设置 params */
//        switch (jiaoHuType) {
//            case JiaoHuType_More:
//                params.width = itemW;
//                params.height = itemH;
//                view.setLayoutParams(params);
//                break;
//            case JiaoHuType_Prise:
//            case JiaoHuType_Attention_hui:
//            case JiaoHuType_Collect:
//            case JiaoHuType_Share_Hui:
//            case JiaoHuType_Report:
//            case JiaoHuType_Modification:
//            default:
//                break;
//        }
            params.width = itemW;
            params.height = itemH;
            view.setLayoutParams(params);
        /* 设置资源文件 */
            switch (jiaoHuType) {
                case JiaoHuType_More:
                    break;
                case JiaoHuType_More_Write:
                    break;
                case JiaoHuType_Prise:
                    break;
                case JiaoHuType_Attention_hui:
                    break;
                case JiaoHuType_Attention_write:
                    break;
                case JiaoHuType_Collect:
                    break;
                case JiaoHuType_Share_Hui:
                    break;
                case JiaoHuType_Share_Write:
                    break;
                case JiaoHuType_Report:
                    break;
                case JiaoHuType_Modification:
                    break;
                case JiaoHuType_Sao_Yi_Sao_Write:
                    break;
                default:
                    break;
            }
            viewMap.put(jiaoHuType, view);
            addView(view);
            if (itemClickListener != null) {
                view.setOnClickListener(itemClickListener);
            }
        }
        return this;
    }

    @Override
    public void removeAllViews() {
        super.removeAllViews();
        viewMap.clear();
    }
}
