package com.bjz.jzappframe.JZButterKnif;

import android.app.Activity;
import android.view.View;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* http://www.51gjie.com/java/795.html */
/* JAVAschool */

/**
 * 运行时注解，只支持遍历本类方法
 * 只支持本类配置
 * 添加 JZOnClick 注解的方法支持没有参数或者一个参数传 View
 */
public class JZButterKnif {

    /* 绑定控件 */
    public static void bind(Activity activity) {
        bind(activity, activity.getWindow().getDecorView());
    }

    /* 绑定 */
    public static void bind(Object object, View groupParentView) {
        Class parentClass = object.getClass();
        /* 绑定控件 */
        /* 获取本类的所有变量 */
        Field[] fields = parentClass.getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (final Field field : fields) {
                try {
                    field.setAccessible(true);
                    /* 取得对象的Field属性值 */
                    /* 如果不为空说明已经被赋值过，则跳过此次循环 */
                    if (field.get(object) != null) {
                        continue;
                    }
                    /* 获取该元素上存在的指定类型的注解 */
                    JZBindView bindViewAnnotation = field.getAnnotation(JZBindView.class);
                    /* 判断是否为空 */
                    if (bindViewAnnotation != null) {
                        /* 获取元素上注解的值 */
                        int viewId = bindViewAnnotation.value();
                        /* 添加注解的View 绑定控件 */
                        field.set(object, groupParentView.findViewById(viewId));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        /* 配置点击事件 */
        /* 获取本类所有方法 */
        Method[] methods = parentClass.getDeclaredMethods();
        /* 过滤添加 JZOnClick 注解的方法 */
        if (methods != null && methods.length > 0) {
            for (Method method : methods) {
                /* 获取该方法上存在的指定类型的注解 */
                JZOnClick onClickMethodAnnotation = method.getAnnotation(JZOnClick.class);
                /* 判断 方法指定注解是否存在及 值长度不为0 */
                if (onClickMethodAnnotation != null && onClickMethodAnnotation.value() != null && onClickMethodAnnotation.value().length != 0) {
                    /* 为添加了Onclick注解的方法配置点击事件 */
                    for (int id : onClickMethodAnnotation.value()) {
                        /* 绑定控件 */
                        View view = groupParentView.findViewById(id);
                        /* 设置点击事件 */
                        view.setOnClickListener(v -> {
                            /* view点击调用回添加OnClick注解的方法 */
                            /* 传入操作的实例及参数 */
                            try {
                                /* 获取方法的所有参数 */
                                Class<?>[] paramsClass = method.getParameterTypes();
                                /* 没有参数 */
                                if (paramsClass == null || paramsClass.length == 0) {
                                    method.invoke(object);
                                } else if (paramsClass.length == 1) {
                                    /* 拥有一个参数 */
                                    method.invoke(object, v);
                                }
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
            }
        }
    }
}
