package com.readyidu.robot.component.rxbus;

import android.app.Activity;

import java.util.HashMap;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * @Autour: wlq
 * @Description: RxBus实现
 * @Date: 2017/10/13 14:47
 * @Update: 2017/10/13 14:47
 * @UpdateRemark:
 * @Version:
 */
public class RxBus {

    private HashMap<Activity, CompositeDisposable> mDisposableMap;

    private Subject mSubject;

    private RxBus() {
        mSubject = PublishSubject.create().toSerialized();
    }

    private static class RxBusHolder {
        private static RxBus instance = new RxBus();
    }

    public static RxBus getInstance() {
        return RxBusHolder.instance;
    }

    /**
     * 发送消息 即调用所有观察者的onNext()方法
     */
    public void send(Object obj) {
        mSubject.onNext(obj);
    }

    /**
     * 返回指定类型的带背压的Flowable实例
     *
     * @param <T>
     * @param type
     * @return
     */
    public <T> Flowable<T> getFlowable(Class<T> type) {
        return mSubject.toFlowable(BackpressureStrategy.BUFFER).ofType(type);
    }

    /**
     * 返回Observable实例
     *
     * @param <T>
     * @param type
     * @return
     */
    public <T> Observable<T> getObservable(Class<T> type) {
        return mSubject.ofType(type);
    }

    /**
     * 是否已有观察者订阅
     *
     * @return
     */
    public boolean hasObservers() {
        return mSubject.hasObservers();
    }

    /**
     * 订阅
     *
     * @param <T>
     * @param type
     * @param next
     * @param error
     * @return
     */
    public <T> Disposable doSubscribe(Class<T> type, Consumer<T> next, Consumer<Throwable> error) {
        return getObservable(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(next, error);
    }

    /**
     * 取消订阅
     *
     * @param activity
     */
    public void unSubscribe(Activity activity) {
        if (mDisposableMap == null) {
            return;
        }

        if (!mDisposableMap.containsKey(activity)) {
            return;
        }
        if (mDisposableMap.get(activity) != null) {
            mDisposableMap.get(activity).dispose();
        }

        mDisposableMap.remove(activity);
    }

    /**
     * 保存订阅后的disposable
     *
     * @param activity
     * @param disposable
     */
    public void addRxBusDisposable(Activity activity, Disposable disposable) {
        if (mDisposableMap == null) {
            mDisposableMap = new HashMap<>();
        }
        if (mDisposableMap.get(activity) != null) {
            mDisposableMap.get(activity).add(disposable);
        } else {
            //一次性容器,可以持有多个并提供 添加和移除。
            CompositeDisposable disposables = new CompositeDisposable();
            disposables.add(disposable);
            mDisposableMap.put(activity, disposables);
        }
    }
}