package com.bjz.jzappframe.ui;

import android.animation.ArgbEvaluator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bjz.jzappframe.IJZBaseView;
import com.bjz.jzappframe.JZBasePresenter;
import com.bjz.jzappframe.JZPageLeftCycle;
import com.bjz.jzappframe.R;
import com.bjz.jzappframe.bean.JZLoadingBean;
import com.bjz.jzappframe.bean.JZPageData;
import com.bjz.jzappframe.event.JZOneStatusCallBack;
import com.bjz.jzappframe.utils.AndroidBottomBarAdaptive;
import com.bjz.jzappframe.utils.JZLog;
import com.bjz.jzappframe.utils.JZPageAnimUtils;
import com.bjz.jzappframe.utils.JZPageConfig;
import com.bjz.jzappframe.utils.JZToast;
import com.bjz.jzappframe.utils.JZUtil;
import com.bjz.jzappframe.utils.stateBar.ImmersionBar;
import com.bjz.jzappframe.widget.JZTitleView;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * ==================================
 * Created by 边江洲 on 2018/9/7.
 * 作    者：WY_BJZ
 * 创建时间：2018/9/7
 * ==================================
 */
/*
 类 说 明：

 页面组件详细逻辑层

 参数描述：


*/
public abstract class JZBaseFragmentActivity<T extends JZBasePresenter> extends Activity implements IJZBaseView {

    /* 生命周期辅助类 */
    private JZPageLeftCycle pageLeftCycle;

    /* 页面配置 */
    public JZPageConfig
            jzPageConfig;

    public boolean
            isResume = false,
            isPause = false,
            isStop = false,
            isDestory = false;

    /* 是否第一次进入页面 */
    public boolean isFirstInto = true;

    /* 执行色值过度的类 */
    ArgbEvaluator dragArgbEvaluator = null;

    /* 屏幕的宽高 */
    int
            screenW,
            screenH;

    T presenter;

    /* 组件相关 */
    private View top1PxView = null;
    /* 最外层容器 */
    RelativeLayout bigGroupView = null;
    /* res 加载容器 */
    LinearLayout resViewGroupView = null;
    /* 左侧点击遮盖view */
    View leftClickCoverView = null;
    /* layout组件 */
    View view;
    /* 默认titleView */
    JZTitleView titleView;

    /* 设置title */
    public void setTitle(String titleStr) {
        titleView.setData(titleStr);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        /* 生命周期辅助类 */
        pageLeftCycle = new JZPageLeftCycle();
        presenter = getPresenter();
        JZPageConfig pageConfigTemp = getJzPageConfig();
        if (pageConfigTemp != null) {
            jzPageConfig = pageConfigTemp;
        } else {
            jzPageConfig = JZPageConfig.getInstance();
        }
        isResume = false;
        isPause = false;
        isStop = false;
        isDestory = false;
        super.onCreate(savedInstanceState);
        /* 限制只能竖屏显示 */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        dragArgbEvaluator = new ArgbEvaluator();
        screenW = JZUtil.screenWidth(this);
        screenH = JZUtil.screenHeigth(this);

        bigGroupView = new RelativeLayout(this);
//        bigGroupView.setBackgroundResource(setBigGroupBk());
        bigGroupView.setBackgroundColor(Color.parseColor("#ffffff"));
        /* 添加顶部1px横线 */
        top1PxView = new View(this);
        bigGroupView.addView(top1PxView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.dimens_1)));
        /* 添加加载展示组件的逻辑 */
        resViewGroupView = new LinearLayout(this);
        resViewGroupView.setOrientation(LinearLayout.VERTICAL);
        resViewGroupView.setBackgroundColor(Color.parseColor("#ffffff"));
        bigGroupView.addView(resViewGroupView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        /* ****************************************************** 加载展示组件逻辑 ****************************************************** */
        /* 判断添加topView */
        if (jzPageConfig.isAddTopView()) {
            View titleTopView = new View(this);
            resViewGroupView.addView(titleTopView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, JZUtil.getStatusBarHeight(this)));
            titleTopView.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        /* 判断添加默认titleView */
        if (jzPageConfig.isAddTitleView()) {
            titleView = new JZTitleView(this);
            RelativeLayout.LayoutParams titleViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            if (jzPageConfig.isAddTopView() && !jzPageConfig.isAddTitleView()) {
                titleViewParams.setMargins(0, JZUtil.getStatusBarHeight(this), 0, 0);
            }
            resViewGroupView.addView(titleView, titleViewParams);
        }
        /* 页面展示组件 */
        if (getResId() != 0) {
            view = LayoutInflater.from(this).inflate(getResId(), null);
        } else if (getCustomView() != null) {
            view = getCustomView();
        }
        resViewGroupView.addView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        /* 添加左侧点击遮盖布局 -- 用来做拖动页面退出操作 */
        leftClickCoverView = new View(this);
        /* 用来拦截此区域的点击事件 */
        leftClickCoverView.setOnClickListener(v -> {
        });
        bigGroupView.addView(leftClickCoverView, new RelativeLayout.LayoutParams((int) getResources().getDimension(R.dimen.dimens_20), RelativeLayout.LayoutParams.MATCH_PARENT));
        /* 容器加载到 页面中 */
        setContentView(bigGroupView);
        /* 用来做虚拟物理按键的适配 */
        AndroidBottomBarAdaptive.assistActivity(this, bigGroupView);

        /* 设置沉浸式状态栏 */
        if (jzPageConfig.isImersive()) {
            ImmersionBar
                    .with(this)
                    .init();
        }
        /* 状态栏内容是否深色 */
        if (jzPageConfig.isImersiveDark()) {
            ImmersionBar.with(this)
                    .statusBarDarkFont(true)
                    .init();
        } else {
            ImmersionBar.with(this)
                    .statusBarDarkFont(false)
                    .init();
        }

        initView();
        initData();
        setListener();
    }

    public abstract int getResId();

    public View getCustomView() {
        return null;
    }

    public abstract void initView();

    public abstract void initData();

    public abstract void setListener();

    public JZPageConfig getJzPageConfig() {
        return null;
    }

    public abstract T getPresenter();

    /* 请求线程列表 */
    /* k：标识不同的接口 */
    /* v：存储请求接口所用线程 */
    public Map<String, Runnable> requestRunMap = new HashMap<>();

    public Runnable testRun = () -> JZBaseFragmentActivity.this.runOnUiThread(() -> JZLog.d("检查", "线程请求模拟1"));

    /* 页面跳转 */
    public void jumpNextPage(JZPageData pageData, Class activity) {
        Intent intent = new Intent();
        intent.setClass(this, activity);
        if (pageData != null && pageData.getKeys() != null && pageData.getKeys().size() > 0) {
            for (String k : pageData.getKeys()) {
                Object v = pageData.getV(k);
                if (v instanceof Integer) {
                    intent.putExtra(k, (int) v);
                } else if (v instanceof Double) {
                    intent.putExtra(k, (double) v);
                } else if (v instanceof Float) {
                    intent.putExtra(k, (float) v);
                } else if (v instanceof Long) {
                    intent.putExtra(k, (long) v);
                } else if (v instanceof String) {
                    intent.putExtra(k, (String) v);
                } else if (v instanceof Boolean) {
                    intent.putExtra(k, (boolean) v);
                } else {
                    intent.putExtra(k, (Serializable) v);
                }
            }
        }
        if (pageData != null && pageData.getIntentFlag() != 0) {
            intent.setFlags(pageData.getIntentFlag());
        }
        startActivity(intent);
        JZPageAnimUtils.skipActivityAnim(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            JZPageAnimUtils.finishActivityAnim(this);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /* 用来处理当存在虚拟物理按键布局异常问题 */
    boolean isBottomBarDispose = false;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!isBottomBarDispose) {
            isBottomBarDispose = true;
            AndroidBottomBarAdaptive.assistActivity(this, bigGroupView);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResume = true;
        isPause = false;
        isStop = false;
        isDestory = false;
        pageLeftCycle.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResume = false;
        isPause = true;
        isStop = false;
        isDestory = false;
        pageLeftCycle.onPause();
        postDelayed(300, result -> {
            if (isFirstInto) {
                isFirstInto = false;
            }
        });
    }

    /* 延时方法调用 */
    public void postDelayed(long time, final JZOneStatusCallBack oneStatusCallBack) {
        bigGroupView.postDelayed(() -> {
            if (oneStatusCallBack != null) {
                oneStatusCallBack.result("");
            }
        }, time);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isResume = false;
        isPause = false;
        isStop = true;
        isDestory = false;
        pageLeftCycle.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isResume = false;
        isPause = false;
        isStop = false;
        isDestory = true;
        pageLeftCycle.onDestory(presenter, this);
        ImmersionBar.with(this).destroy(); //必须调用该方法，防止内存泄漏
    }

    public <viewT extends View> viewT bind(int id) {
        return (viewT) view.findViewById(id);
    }

    public int setBigGroupBk() {
        return R.color._ffffff;
    }

    /* 显示加载圈 */
    @Override
    public void showLoad(JZLoadingBean loadingBean) {

    }

    /* 关闭加载圈 */
    @Override
    public void hideLoad() {

    }

    public void showToast(String str) {
        JZToast.showShortToast(this, str);
    }

    /* ****************************************************** 页面拖动退出逻辑 ****************************************************** */

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        /* ****************************************************** 页面点击其他地方输入框隐藏逻辑 ****************************************************** */
        if (jzPageConfig.isTouchHideKeyBroad()) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                View v = getCurrentFocus();
                if (isShouldHideInput(v, ev)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
                return super.dispatchTouchEvent(ev);
            }
            // 必不可少，否则所有的组件都不会有TouchEvent了
            if (getWindow().superDispatchTouchEvent(ev)) {
                return true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
}
