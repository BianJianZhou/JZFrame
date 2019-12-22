package com.bjz.jzappframe.widget.titleChild;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.bjz.jzappframe.R;
import com.bjz.jzappframe.utils.JZLog;
import com.bjz.jzappframe.widget.JZTextView;

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
public class JZTitleRightTextGroup extends LinearLayout {

    Context context;

    int
            itemTextPaddingLeft, itemTextPaddingRight,
            itemViewPaddintLeft, itemViewPaddingRight;

    Map<String, JZTextView> itemMap = new HashMap<>();

    public JZTitleRightTextGroup(Context context) {
        this(context, null);
    }

    public JZTitleRightTextGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JZTitleRightTextGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        setLayoutParams(params);
        setOrientation(HORIZONTAL);
        this.context = context;
        itemTextPaddingLeft = (int) getResources().getDimension(R.dimen.dimens_12);
        itemTextPaddingRight = (int) getResources().getDimension(R.dimen.dimens_12);
        itemViewPaddintLeft = (int) getResources().getDimension(R.dimen.dimens_6);
        itemViewPaddingRight = (int) getResources().getDimension(R.dimen.dimens_6);
        setPadding(0, 0, (int) (getResources().getDimension(R.dimen.dimens_20) - itemTextPaddingRight), 0);
    }

    /*
     可变数组
     第一个参数代表 文字颜色
     第二个参数代表 文字大小
      */
    public JZTitleRightTextGroup addItem_TextView(String itemContent, OnClickListener itemClickListener, JZTitleRightTextGroupItemParams... itemParam) {
        if (!itemMap.containsKey(itemContent)) {
            JZTitleRightTextGroupItemParams textViewParams = new JZTitleRightTextGroupItemParams(context);
            if (itemParam != null && itemParam.length != 0) {
                textViewParams = itemParam[0];
            }
            JZTextView itemText = new JZTextView(context);
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            params.gravity = Gravity.CENTER_VERTICAL;
            itemText.setLayoutParams(params);
            itemText.setTextSize(textViewParams.getSize());
            itemText.setTextColor(Color.parseColor(textViewParams.getColor()));
            itemText.setText(itemContent);
            itemText.setPadding(itemTextPaddingLeft, 0, itemTextPaddingRight, 0);
            itemMap.put(itemContent, itemText);
            addView(itemText);
            if (itemClickListener != null) {
                itemText.setOnClickListener(itemClickListener);
            }
        }
        return this;
    }

    public JZTitleRightTextGroup addItem_View(String itemColor) {
        View itemText = new JZTextView(context);
        LayoutParams params = new LayoutParams((int) getResources().getDimension(R.dimen.dimens_1), (int) getResources().getDimension(R.dimen.dimens_24));
        itemText.setLayoutParams(params);
        itemText.setPadding(itemViewPaddintLeft, 0, itemViewPaddingRight, 0);
        itemText.setBackgroundColor(Color.parseColor(itemColor));
        addView(itemText);
        return this;
    }

    public JZTitleRightTextGroup updateItem_TextView(String itemContent, JZTitleRightTextGroupItemParams itemParams) {
        if (itemMap.containsKey(itemContent)) {
            itemMap.get(itemContent).setTextSize(itemParams.getSize());
            itemMap.get(itemContent).setTextColor(Color.parseColor(itemParams.getColor()));
        } else {
            JZLog.i("异常", getClass().getSimpleName() + " - updateItem_TextView - 不存在 -->" + itemContent);
        }
        return this;
    }

    public JZTitleRightTextGroup removeItem_TextView(String itemContent) {
        if (itemMap.containsKey(itemContent)) {
            removeView(itemMap.get(itemContent));
            itemMap.remove(itemContent);
        } else {
            JZLog.i("异常", getClass().getSimpleName() + " - removeItem_TextView - 不存在 -->" + itemContent);
        }
        return this;
    }

    @Override
    public void removeAllViews() {
        super.removeAllViews();
        itemMap.clear();
    }
}
