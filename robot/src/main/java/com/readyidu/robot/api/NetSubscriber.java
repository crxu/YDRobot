package com.readyidu.robot.api;

import com.readyidu.robot.YDRobot;
import com.readyidu.robot.api.exception.ExceptionHandle;
import com.readyidu.robot.model.BaseModel;
import com.readyidu.robot.model.DataModel;
import com.readyidu.robot.utils.log.LogUtils;
import com.readyidu.robot.utils.view.DialogUtils;
import com.readyidu.robot.utils.view.ToastUtil;

import io.reactivex.observers.DisposableObserver;

/**
 * @Autour: wlq
 * @Description: 网络请求观察者
 * @Date: 2017/10/13 12:42
 * @Update: 2017/10/13 12:42
 * @UpdateRemark:
 * @Version:
 */
public abstract class NetSubscriber<T> extends DisposableObserver<T> {

    @Override
    public void onComplete() {
        DialogUtils.closeProgressDialog();
    }

    @Override
    public void onNext(T t) {
        try {
            BaseModel baseModel = (BaseModel) t;
            if (baseModel.isSuccess()) {
                DataModel dataModel = baseModel.data;
                if (null == dataModel) {
                    onFailure(baseModel.errorCode, baseModel.errorMessage);
                } else {
                    onSuccess(baseModel);
                }
            } else {
                onFailure(baseModel.errorCode, baseModel.errorMessage);
            }
        } catch (Exception e) {
            LogUtils.e(e);
            e.printStackTrace();
        }
        DialogUtils.closeProgressDialog();
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof Exception) {
            //访问获得对应的Exception
            ExceptionHandle.ResponseThrowable throwable = ExceptionHandle.handleException(e);

            onFailure(throwable.code, throwable.message);
            if (throwable.code == ExceptionHandle.ERROR.HTTP_ERROR) {
                ToastUtil.showToast(YDRobot.getInstance().getContext(), "服务不可用");
                return;
            }
            if (throwable.code == ExceptionHandle.ERROR.NETWORK_ERROR) {
                ToastUtil.showToast(YDRobot.getInstance().getContext(), "当前网络欠佳");
            }
        } else {
            //将Throwable 和 未知错误的status code返回
            ExceptionHandle.ResponseThrowable throwable = new ExceptionHandle.ResponseThrowable(e, ExceptionHandle.ERROR.UNKNOWN);
            onFailure(throwable.code, throwable.message);
        }
        DialogUtils.closeProgressDialog();
    }

    protected abstract void onSuccess(BaseModel baseModel);

    protected abstract void onFailure(int errorCode, String errorMessage);
}
