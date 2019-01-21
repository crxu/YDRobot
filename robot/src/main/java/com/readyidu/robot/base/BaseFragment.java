package com.readyidu.robot.base;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.readyidu.robot.component.rxbus.RxBus;
import com.readyidu.robot.utils.view.ToastUtil;
import com.readyidu.robot.utils.JLog;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @Autour: wlq
 * @Description: 基类Fragment
 * @Date: 2017/10/14 18:04
 * @Update: 2017/10/14 18:04
 * @UpdateRemark: 基类Fragment
 * @Version:
*/
public abstract class BaseFragment extends Fragment {

    public Activity mContext;
    private CompositeDisposable mCompositeDisposable;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getContentViewLayoutID(), container, false);
        mContext = getActivity();
        bindViews(view);
        bindEvents();
        JLog.e(this.getClass().getName());
        return view;
    }

    public void addDisposable(Disposable disposable) {
        if (null == disposable)
            return;

        if (null == mCompositeDisposable)
            mCompositeDisposable = new CompositeDisposable();

        mCompositeDisposable.add(disposable);
    }

    private void clearDisposable() {
        if (null != mCompositeDisposable)
            mCompositeDisposable.clear();
    }

    /**
     * 注册订阅事件
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

    @Override
    public void onDestroy() {
        super.onDestroy();


    }

    /**
     * 消息弹窗
     * @param msg
     */
    public void showToast(String msg) {
        ToastUtil.showToast(mContext, msg);
    }


    protected abstract int getContentViewLayoutID();
    protected abstract void bindViews(View contentView);
    protected abstract void bindEvents();
}
