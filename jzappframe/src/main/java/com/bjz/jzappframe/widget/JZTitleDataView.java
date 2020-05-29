package com.bjz.jzappframe.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

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
public class JZTitleDataView extends RelativeLayout {

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
