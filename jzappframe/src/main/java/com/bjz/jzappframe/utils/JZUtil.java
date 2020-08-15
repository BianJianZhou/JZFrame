package com.bjz.jzappframe.utils;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class JZUtil {
    public static Application getApp() {
        try {
            /* 获取类 ActivityThread activityThread */
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            /* 调用 ActivityThread 中的静态方法 currentActivityThread() 获取实例 thread*/
            Object thread = activityThread.getMethod("currentActivityThread").invoke(null);
            /* 使用 ActivityThread 的实例 thread 调用 ActivityThread 类中的方法 getApplication() */
            Object app = activityThread.getMethod("getApplication").invoke(thread);
            return (Application) app;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获得屏幕宽度
     *
     * @return
     */
    public static int screenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获得屏幕高度
     *
     * @return
     */
    public static int screenHeigth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    /* 获取状态栏高度 */
    public static int getStatusBarHeight(Context context) {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            int dimensionPixelSize = context.getResources().getDimensionPixelSize(x);
            return dimensionPixelSize;

        } catch (Exception e) {
            e.printStackTrace();

        }
        return 0;

    }

    //获取虚拟按键的高度
    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        if (hasNavBar(context)) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    /**
     * 检查是否存在虚拟按键栏
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static boolean hasNavBar(Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            // check override flag
            String sNavBarOverride = getNavBarOverride();
            if ("1".equals(sNavBarOverride)) {
                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {
                hasNav = true;
            }
            return hasNav;
        } else { // fallback
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }

    /**
     * 判断虚拟按键栏是否重写
     *
     * @return
     */
    private static String getNavBarOverride() {
        String sNavBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
            }
        }
        return sNavBarOverride;
    }

    /* 资源id转化为bitmap */


}
