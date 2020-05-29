package com.bjz.jzappframe.utils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bjz.jzappframe.JZUIFrameManager;
import com.bjz.jzappframe.R;

import java.lang.reflect.Field;

/**
 * Created by 边江洲 on 2017/7/3.
 */

public class JZToast {
    public static Toast mToast;
    private static int textview_id;

    public static void showLongToast(Context context, String content) {
        if (mToast == null) {
            mToast = Toast.makeText(context, content, Toast.LENGTH_LONG);
        } else {
            mToast.setText(content);
            mToast.setDuration(Toast.LENGTH_LONG);
        }

        toastAnimSet(context);
        mToast.show();
    }


    public static void showShortToast(Context context, String content) {
        if (mToast == null) {
            mToast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(content);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }

        toastAnimSet(context);
        mToast.show();
    }

    private static void toastAnimSet(Context context) {
        try {
            if (textview_id == 0)
                textview_id = context.getResources().getSystem().getIdentifier("message", "id", "android");
            ((TextView) mToast.getView().findViewById(textview_id)).setGravity(Gravity.CENTER);

            mToast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, JZUIFrameManager.getInstance().getViewsConfigBuilder().getCustomToastHeight());

            Object mTN;
            mTN = getField(mToast, "mTN");
            if (mTN != null) {
                Object mParams = getField(mTN, "mParams");
                if (mParams != null
                        && mParams instanceof WindowManager.LayoutParams) {
                    WindowManager.LayoutParams params = (WindowManager.LayoutParams) mParams;
                    params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    params.format = PixelFormat.TRANSLUCENT;
                    params.windowAnimations = R.style.Base_Toast_Anim;
                    params.type = WindowManager.LayoutParams.TYPE_TOAST;
                    params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                            | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void cancleToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

    /**
     * 反射字段
     *
     * @param object    要反射的对象
     * @param fieldName 要反射的字段名称
     */
    private static Object getField(Object object, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        if (field != null) {
            field.setAccessible(true);
            return field.get(object);
        }
        return null;
    }
}
