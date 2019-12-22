package com.bjz.jzappframe.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Administrator on 2017/3/15.
 * 只输出等级大于等于LEVEL的日志
 * 所以在开发和产品发布后通过修改LEVEL来选择性输出日志.
 * 当LEVEL=NOTHING则屏蔽了所有的日志.
 */

public class JZLog {
    /* 显示logv以上的包括v */
    public static final int VERBOSE = 1;
    /* 显示logd以上的包括d */
    public static final int DEBUG = 2;
    /* 显示logi以上的包括i */
    public static final int INFO = 3;
    /* 显示logw以上的包括w */
    public static final int WARN = 4;
    /* 显示loge以上的包括e */
    public static final int ERROR = 5;
    /* 什么也不显示 */
    public static final int NOTHING = 6;
    /*标记*/
    private static final String TAG = "JZ:";

    /* 输出控制赋值 */
    public static int LEVEL = VERBOSE;

    /**
     * 冗余消息
     *
     * @param tag
     * @param message
     */
    public static void v(String tag, String message) {
        if (LEVEL <= VERBOSE) {
            Log.v(TAG + getTag(tag), getMsg(message));
        }
    }

    /**
     * 调试消息
     *
     * @param tag
     * @param message
     */
    public static void d(String tag, String message) {
        if (LEVEL <= DEBUG) {
            Log.i(TAG + getTag(tag), getMsg(message));
        }
    }

    /**
     * 普通消息
     *
     * @param tag
     * @param message
     */
    public static void i(String tag, String message) {
        if (LEVEL <= INFO) {
            Log.i(TAG + getTag(tag), getMsg(message));
        }
    }

    /**
     * 警告消息
     *
     * @param tag
     * @param message
     */
    public static void w(String tag, String message) {
        if (LEVEL <= WARN) {
            Log.w(TAG + getTag(tag), getMsg(message));
        }
    }

    /**
     * 错误消息
     *
     * @param tag
     * @param message
     */
    public static void e(String tag, String message) {
        if (LEVEL <= ERROR) {
            Log.e(TAG + getTag(tag), getMsg(message));
        }
    }

    /**
     * 错误消息
     *
     * @param tag
     * @param message
     */
    public static void e(String tag, String message, Throwable e) {
        if (LEVEL <= ERROR) {
            Log.e(TAG + getTag(tag), "错误描述:" + getMsg(message) + "堆栈信息: \n" + Log.getStackTraceString(e));
        }
    }

    /**
     * 顶级Log
     *
     * @param tag
     * @param message
     */
    public static void x(String tag, String message) {
        Log.i(TAG + getTag(tag), getMsg(message));
    }

    /**
     * 自定义打印错误信息
     */
    public static void stackTraceLog(String tag, String failStr, Throwable e) {
        w(TAG + tag + "custom warning: ", "错误描述:" + failStr + "堆栈信息: \n" + Log.getStackTraceString(e));
    }

    /**
     * 自定义打印错误信息
     */
    public static void stackTraceLog(String tag, String waringStr) {
        w(TAG + tag + "custom warning: ", getCustomWarningLog(waringStr));
    }

    /* 拼接字符串 打印出当前线程的 堆栈信息 */
    private static String getCustomWarningLog(String temp) {

        /**
         stackTraceElement.getClassName(),     //类    名： 
         stackTraceElement.getFileName(),      //文 件 名： 
         stackTraceElement.getMethodName(),    //方 法 名： 
         stackTraceElement.getLineNumber(),    //代码位置： 
         */

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("错误描述: ").append(temp)
                .append("\n")
                .append("堆栈信息: \n");
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            stringBuilder.append("\tat ").append(stackTraceElement.getClassName()).append(" | ").append(stackTraceElement.getMethodName()).append("() \n")
                    .append(" <").append(stackTraceElement.getFileName()).append(" :第 ").append(stackTraceElement.getLineNumber() + " 行>\n");
        }
        return stringBuilder.toString();
    }

    private static String getTag(String tag) {
        if (TextUtils.isEmpty(tag)) {
            return "";
        }
        return tag;
    }

    private static String getMsg(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return "";
        }
        return msg;
    }

}
