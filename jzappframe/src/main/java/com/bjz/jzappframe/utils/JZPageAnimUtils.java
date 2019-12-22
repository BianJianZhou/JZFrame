package com.bjz.jzappframe.utils;

import android.app.Activity;

import com.bjz.jzappframe.R;

/**
 * Created by Administrator on 2017/5/26.
 */

public class JZPageAnimUtils {

    //打开页面切换动画
    public static void skipActivityAnim(Activity context) {
        context.overridePendingTransition(R.anim.base_activity_anim_right_to_0, R.anim.base_activity_anim_0_to_left);
    }

    //关闭页面切换动画
    public static void finishActivityAnim(Activity context) {
        context.overridePendingTransition(R.anim.base_activity_anim_left_to_0, R.anim.base_activity_anim_0_to_right);
    }

}
