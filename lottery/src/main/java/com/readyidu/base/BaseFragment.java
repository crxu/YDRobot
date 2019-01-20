package com.readyidu.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.readyidu.utils.JLog;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * File: BaseFragment.java Author: Administrator Version:1.0.2 Create: 2018/9/4
 * 0004 11:18 describe Fragment基类
 */

public abstract class BaseFragment extends Fragment {
    protected View              rootView;
    private CompositeDisposable mCompositeDisposable;
    protected FragmentActivity  activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null)
            rootView = inflater.inflate(getLayoutResource(), container, false);
        initView();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        JLog.e(this.getClass().getName());
    }

    @Override
    public void onDestroy() {
        clearDisposable();
        super.onDestroy();
    }

    //获取布局文件
    protected abstract int getLayoutResource();

    //初始化view
    protected abstract void initView();

    public void addDisposable(Disposable disposable) {
        if (null == disposable) {
            return;
        }
        try {
            if (null == mCompositeDisposable) {
                mCompositeDisposable = new CompositeDisposable();
            }
            mCompositeDisposable.add(disposable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearDisposable() {
        try {
            if (null != mCompositeDisposable) {
                mCompositeDisposable.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
