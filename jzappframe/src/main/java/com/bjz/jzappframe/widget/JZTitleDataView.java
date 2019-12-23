package com.bjz.jzappframe.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.bjz.jzappframe.ui.JZBaseView;
import com.bjz.jzappframe.widget.titleChild.JZTitleRightIconGroup;
import com.bjz.jzappframe.widget.titleChild.JZTitleRightTextGroup;

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
public abstract class JZTitleDataView extends JZBaseView<String> {

    public static final String App_Title_Theme_Color_Drak = "App_Title_Theme_Color_Drak";
    public static final String App_Title_Theme_Color_Tint = "App_Title_Theme_Color_Tint";
    public static final String App_Title_Theme_Color_All = "App_Title_Theme_Color_All";

    private JZTitleRightIconGroup
            rightJiaoHuGroup;
    private JZTitleRightTextGroup
            rightTextGroup;

    RelativeLayout
            rightGroupRl;

    public JZTitleDataView(Context context) {
        super(context);
    }

    public JZTitleDataView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JZTitleDataView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public JZTitleRightIconGroup getRightJiaoHuGroup() {
        return rightJiaoHuGroup;
    }

    public JZTitleDataView setRightJiaoHuGroup(JZTitleRightIconGroup rightJiaoHuGroup) {
        if (rightJiaoHuGroup != null) {
            rightGroupRl.removeView(rightJiaoHuGroup);
        }
        rightGroupRl.addView(rightJiaoHuGroup);
        this.rightJiaoHuGroup = rightJiaoHuGroup;
        return this;
    }

    public JZTitleRightTextGroup getRightTextGroup() {
        return rightTextGroup;
    }

    public JZTitleDataView setRightTextGroup(JZTitleRightTextGroup rightTextGroup) {
        if (rightTextGroup != null) {
            rightGroupRl.removeView(rightGroupRl);
        }
        rightGroupRl.addView(rightTextGroup);
        this.rightTextGroup = rightTextGroup;
        return this;
    }
}
