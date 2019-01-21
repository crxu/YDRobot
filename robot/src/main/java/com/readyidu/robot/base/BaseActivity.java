package com.readyidu.robot.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.readyidu.basic.utils.AppManager;
import com.readyidu.robot.R;
import com.readyidu.robot.component.rxbus.RxBus;
import com.readyidu.robot.event.ExitEvent;
import com.readyidu.robot.ui.menu.activity.MenuDetailActivity;
import com.readyidu.robot.ui.music.activity.MusicPlayActivity;
import com.readyidu.robot.ui.news.activity.NewsDetailsActivity;
import com.readyidu.robot.ui.tv.activity.TvPlayActivity;
import com.readyidu.robot.ui.weather.activity.WeatherActivity;
import com.readyidu.robot.ui.widgets.CustomTopBar;
import com.readyidu.robot.utils.log.LogUtils;
import com.readyidu.robot.utils.view.MeasureUtil;
import com.readyidu.robot.utils.view.ToastUtil;
import com.readyidu.robot.utils.JLog;


import java.lang.reflect.Field;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @Autour: wlq
 * @Description: Activity基类
 * @Date: 2017/10/9 14:45
 * @Update: 2017/10/9 14:45
 * @UpdateRemark:
 * @Version: V1.0
 */
public abstract class BaseActivity extends AppCompatActivity {

    public Activity mContext;

    public CustomTopBar mTopBar;

    public View mStatusBar;

    private CompositeDisposable mCompositeDisposable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTranslucentStatusBar();
        setContentView(getContentViewLayoutID());
        AppManager.getInstance().addActivity(this);
        initTopBar();
        mContext = this;
        bindViews();
        bindEvents();
        JLog.e(this.getClass().getName());
        registerRxBus(ExitEvent.class, new Consumer<ExitEvent>() {
            @Override
            public void accept(ExitEvent exitEvent) throws Exception {
                finish();
            }
        });

        try {
            findViewById(R.id.title).setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JLog.e(this.getClass().getName());
    }

    /**
     * 设置透明状态栏
     */
    private void setTranslucentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4 全透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0 全透明实现
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        AppManager.getInstance().removeActivity(this);
        RxBus.getInstance().unSubscribe(mContext);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            clearDisposable();
        }
        return super.onKeyDown(keyCode, event);
    }

    protected abstract int getContentViewLayoutID();

    //点击标题栏左边布局事件
    protected void clickBack() {
        fixInputMethodManagerLeak(this);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        fixInputMethodManagerLeak(this);
    }

    protected void clickRight() {
    }   //点击标题栏右边布局事件

    protected abstract void bindViews();

    protected abstract void bindEvents();

    //初始化标题栏
    private void initTopBar() {
        mTopBar = (CustomTopBar) findViewById(R.id.top_bar);
        if (null != mTopBar) {
            mTopBar.setClickTitleListener(new CustomTopBar.ClickTitleListener() {
                @Override
                public void onClickBack() {
                    clickBack();
                }

                @Override
                public void onClickRight() {
                    clickRight();
                }
            });
        }
    }

    public void setStatusBarVisibility(boolean needShow) {
        if (needShow) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mStatusBar.setVisibility(View.VISIBLE);
                //动态设置高度
                mStatusBar.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        MeasureUtil.getStatusBarHeight2(this)));
            } else {
                mStatusBar.setVisibility(View.GONE);
            }
        } else {
            mStatusBar.setVisibility(View.GONE);
        }
    }

    /**
     * 将请求添加至CompositeDisposable中
     *
     * @param disposable
     */
    public void addDisposable(Disposable disposable) {
        if (null == disposable) {
            return;
        }
        if (null == mCompositeDisposable) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }

    /**
     * 切断所有请求
     */
    public void clearDisposable() {
        try {
            if (null != mCompositeDisposable) {
                mCompositeDisposable.clear();
            }

        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    /**
     * 注册订阅事件
     *
     * @param eventType
     * @param action
     * @param <T>
     */
    public <T> void registerRxBus(Class<T> eventType, Consumer<T> action) {
        RxBus rxBus = RxBus.getInstance();
        Disposable disposable = rxBus.doSubscribe(eventType, action, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
        rxBus.addRxBusDisposable(mContext, disposable);
    }

    /**
     * 取消注册订阅事件
     */
    public void unregisterRxBus() {
        RxBus.getInstance().unSubscribe(mContext);
    }

    public void finish() {
        super.finish();
//        KeyBoardUtils.forceCloseKayBoard(mContext);
        AppManager.getInstance().removeActivity(mContext);
    }

    public void showToast(String msg) {
        ToastUtil.showToast(mContext, msg);
    }

    public String getResource(int resId) {
        return getResources().getString(resId);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if ((this instanceof MusicPlayActivity) || (this instanceof WeatherActivity)
                || (this instanceof NewsDetailsActivity) || (this instanceof MenuDetailActivity)
                || (this instanceof TvPlayActivity)) {
            if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                View v = getCurrentFocus();
                if (isShouldHideInput(v, ev)) {
                    InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
                return super.dispatchTouchEvent(ev);
            }
        }

        // 必不可少，否则所有的组件都不会有TouchEvent了
        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            return !(event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }

    public void fixInputMethodManagerLeak(Context context) {
        if (context == null) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        String[] arr = new String[]{"mCurRootView", "mServedView", "mNextServedView"};
        Field f = null;
        Object obj = null;
        for (int i = 0; i < arr.length; i++) {
            String param = arr[i];
            try {
                f = imm.getClass().getDeclaredField(param);
                if (f.isAccessible() == false) {
                    f.setAccessible(true);
                }
                obj = f.get(imm);
                if (obj != null && obj instanceof View) {
                    View vGet = (View) obj;
                    if (vGet.getContext() == context) {
                        f.set(imm, null);
                    } else {
                        break;
                    }
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
