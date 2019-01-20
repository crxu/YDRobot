package com.readyidu.robot.ui.news.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.readyidu.robot.component.rxbus.RxBus;
import com.umeng.analytics.MobclickAgent;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author Wlq
 * @description 基于懒加载和缓存的Fragment基类
 * @date 2017/11/6 上午10:38
 */
public abstract class LazyFragment extends Fragment {

    protected View mRootView;
    protected Activity mActivity;

    private boolean isFragmentVisible;
    private boolean isReuseView;
    private boolean isFirstVisible;

    private CompositeDisposable mCompositeDisposable;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mActivity = getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariable();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (null == mRootView) {
            mRootView = inflater.inflate(getContentViewId(), container, false);
        }
        initView();

        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        //如果setUserVisibleHint()在rootView创建前调用时，那么
        //就等到rootView创建完后才回调onFragmentVisibleChange(true)
        //保证onFragmentVisibleChange()的回调发生在rootView创建完成之后，以便支持ui操作
        if (mRootView == null) {
            mRootView = view;
            if (getUserVisibleHint()) {
                if (isFirstVisible) {
                    onFirstVisible();
                    isFirstVisible = false;
                }
                onVisibleChange(true);
                isFragmentVisible = true;
            }
        }
        super.onViewCreated(isReuseView ? mRootView : view, savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (mRootView == null) {
            return;
        }
        if (isFirstVisible && isVisibleToUser) {
            onFirstVisible();
            isFirstVisible = false;
        }
        if (isVisibleToUser) {
            onVisibleChange(true);
            isFragmentVisible = true;
            return;
        }
        if (isFragmentVisible) {
            isFragmentVisible = false;
            onVisibleChange(false);
        }
    }

    private void initVariable() {
        isFirstVisible = true;
        isFragmentVisible = false;
        mRootView = null;
        isReuseView = true;
    }

    /**
     * 设置是否使用 view 的复用，默认开启
     * view 的复用是指，ViewPager 在销毁和重建 Fragment 时会不断调用 onCreateView() -> onDestroyView()
     * 之间的生命函数，这样可能会出现重复创建 view 的情况，导致界面上显示多个相同的 Fragment
     * view 的复用其实就是指保存第一次创建的 view，后面再 onCreateView() 时直接返回第一次创建的 view
     * @param isReuse
     */
    protected void reuseView(boolean isReuse) {
        isReuseView = isReuse;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getUserVisibleHint()) {
            setUserVisibleHint(true);
        }
        MobclickAgent.onResume(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();

        MobclickAgent.onPause(getActivity());
    }

    public void addDisposable(Disposable disposable) {
        if (null == disposable)
            return;

        if (null == mCompositeDisposable)
            mCompositeDisposable = new CompositeDisposable();

        mCompositeDisposable.add(disposable);
    }

    protected void clearDisposable() {
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
        rxBus.addRxBusDisposable(mActivity, disposable);
    }

    /**
     * 取消注册订阅事件
     */
    public void unregisterRxBus() {
        RxBus.getInstance().unSubscribe(mActivity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        clearDisposable();
        unregisterRxBus();
    }

    protected boolean isFragmentVisible() {
        return isFragmentVisible;
    }

    /**
     * 去除setUserVisibleHint()多余的回调场景，保证只有当fragment可见状态发生变化时才回调
     * 回调时机在view创建完后，所以支持ui操作，解决在setUserVisibleHint()里进行ui操作有可能报null异常的问题
     * 可在该回调方法里进行一些ui显示与隐藏，比如加载框的显示和隐藏
     *
     * @param isVisible true  不可见 -> 可见
     *                  false 可见  -> 不可见
     */
    protected void onVisibleChange(boolean isVisible) {}

    /**
     * 在fragment首次可见时回调，可在这里进行加载数据，保证只在第一次打开Fragment时才会加载数据，
     * 这样就可以防止每次进入都重复加载数据
     * 该方法会在 onVisibleChange() 之前调用，所以第一次打开时，可以用一个全局变量表示数据下载状态，
     * 然后在该方法内将状态设置为下载状态，接着去执行下载的任务
     * 最后在 onVisibleChange() 里根据数据下载状态来控制下载进度ui控件的显示与隐藏
     */
    protected void onFirstVisible() {}

    protected abstract void initView();

    protected abstract int getContentViewId();
}
