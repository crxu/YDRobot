package com.readyidu.robot.api.impl;

import com.readyidu.robot.AppConfig;
import com.readyidu.robot.api.HttpHelper;
import com.readyidu.robot.api.TVService;
import com.readyidu.robot.api.exception.BaseApiException;
import com.readyidu.robot.model.BaseListModel;
import com.readyidu.robot.model.BaseObjModel;
import com.readyidu.robot.model.business.tv.CheckModel;
import com.readyidu.robot.model.business.tv.ProgrammeListModel;
import com.readyidu.robot.model.business.tv.TvChannel;
import com.readyidu.robot.model.business.tv.TvChannelParent;
import com.readyidu.robot.model.business.tv.TvSource;
import com.readyidu.robot.model.business.tv.TvType;
import com.readyidu.robot.model.business.tv.base.Base;
import com.readyidu.robot.utils.JLog;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by gx on 2017/12/27.
 */
public class TVServiceImpl {

    public static TVService tvService;

    /**
     * 统一处理接口返回数据
     *
     * @param respBase2
     * @return
     */
    private static <T> Observable<BaseListModel<T>> flatResult(final BaseListModel<T> respBase2) {
        return Observable.create(new ObservableOnSubscribe<BaseListModel<T>>() {
            @Override
            public void subscribe(ObservableEmitter<BaseListModel<T>> e) throws Exception {
                if (200 == respBase2.code) {
                    e.onNext(respBase2);
                } else {
                    Throwable throwable = new BaseApiException(respBase2.code, respBase2.message);
                    e.onError(throwable);
                }
                e.onComplete();
            }
        });
    }

    /**
     * 统一处理接口返回数据
     *
     * @param respBase2
     * @return
     */
    private static <T> Observable<BaseObjModel<T>> flatResult2(final BaseObjModel<T> respBase2) {
        return Observable.create(new ObservableOnSubscribe<BaseObjModel<T>>() {
            @Override
            public void subscribe(ObservableEmitter<BaseObjModel<T>> e) throws Exception {
                if (200 == respBase2.code) {
                    e.onNext(respBase2);
                } else {
                    Throwable throwable = new BaseApiException(respBase2.code, respBase2.message);
                    e.onError(throwable);
                }
                e.onComplete();
            }
        });
    }

    /**
     * 统一处理接口返回数据
     *
     * @param respBase2
     * @return
     */
    private static <T> Observable<T> flatResult3(final Base<T> respBase2) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> e) throws Exception {
                if (200 == respBase2.errorCode) {
                    e.onNext(respBase2.data);
                } else {
                    Throwable throwable = new BaseApiException(respBase2.errorCode, respBase2.errorMessage);
                    e.onError(throwable);
                }
                e.onComplete();
            }
        });
    }

    public static Observable<BaseListModel<TvType>> getTypeList() {
        if (tvService == null) {
            HttpHelper.initialize();
        }
        return tvService
                .getTypeList()
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<BaseListModel<TvType>, ObservableSource<BaseListModel<TvType>>>() {
                    @Override
                    public ObservableSource<BaseListModel<TvType>> apply(BaseListModel<TvType> hotRecommendModelBase) throws Exception {
                        return flatResult(hotRecommendModelBase);
                    }
                })
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<BaseObjModel<TvChannelParent>> getNewChannelListByTypeId(String typeId) {
        if (tvService == null) {
            HttpHelper.initialize();
        }
        return tvService
                .getNewChannelListByTypeId(typeId)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<BaseObjModel<TvChannelParent>, ObservableSource<BaseObjModel<TvChannelParent>>>() {
                    @Override
                    public ObservableSource<BaseObjModel<TvChannelParent>> apply(BaseObjModel<TvChannelParent> hotRecommendModelBase) throws Exception {
                        return flatResult2(hotRecommendModelBase);
                    }
                })
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public static Observable<String> getSourceById(int id) {
        JLog.e("TAG","id:"+id);
        if (tvService == null) {
            HttpHelper.initialize();
        }
        return tvService
                .getSourceById(id)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<Base<String>, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Base<String> hotRecommendModelBase) throws Exception {
                        return flatResult3(hotRecommendModelBase);
                    }
                })
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<String> getDemandById(int id) {
        if (tvService == null) {
            HttpHelper.initialize();
        }
        return tvService
                .getDemandById(id)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<Base<String>, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Base<String> hotRecommendModelBase) throws Exception {
                        return flatResult3(hotRecommendModelBase);
                    }
                })
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取uri播放地址
     *
     * @param source
     * @return
     */
    public static Observable<BaseObjModel<TvSource>> getRobotTvSourceUri(String source) {
        if (tvService == null) {
            HttpHelper.initialize();
        }
        return tvService
                .getRobotTvSourceUri(source)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<BaseObjModel<TvSource>, ObservableSource<BaseObjModel<TvSource>>>() {
                    @Override
                    public ObservableSource<BaseObjModel<TvSource>> apply(BaseObjModel<TvSource> baseModel3) throws Exception {
                        return flatResult2(baseModel3);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    public static Observable<ProgrammeListModel> getChannelPlaybill(String channelId) {
        if (tvService == null) {
            HttpHelper.initialize();
        }
        return tvService
                .getChannelPlaybill(channelId)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<Base<ProgrammeListModel>, ObservableSource<ProgrammeListModel>>() {
                    @Override
                    public ObservableSource<ProgrammeListModel> apply(Base<ProgrammeListModel> hotRecommendModelBase) throws Exception {
                        return flatResult3(hotRecommendModelBase);
                    }
                })
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<BaseListModel<TvChannel>> getNumberOfDramas(int id) {
        if (tvService == null) {
            HttpHelper.initialize();
        }
        return tvService
                .getNumberOfDramas(id)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<BaseListModel<TvChannel>, ObservableSource<BaseListModel<TvChannel>>>() {
                    @Override
                    public ObservableSource<BaseListModel<TvChannel>> apply(BaseListModel<TvChannel> baseListModel) throws Exception {
                        return flatResult(baseListModel);
                    }
                })
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<BaseObjModel<String>> getCustomizedList(String url) {
        if (tvService == null) {
            HttpHelper.initialize();
        }
        return tvService.getCustomizedList(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    public static Observable<BaseObjModel<String>> bundling(String token, String tvDeviceId) {
        if (tvService == null) {
            HttpHelper.initialize();
        }
        return tvService.bundling(token, tvDeviceId, AppConfig.APP_NICK_NAME)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    public static Observable<BaseObjModel<String>> callBackUpdate(String key, String token) {
        if (tvService == null) {
            HttpHelper.initialize();
        }
        return tvService.callBackUpdate(key, token, AppConfig.APP_NICK_NAME)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    public static Observable<BaseObjModel<String>> getQiniuToken() {
        if (tvService == null) {
            HttpHelper.initialize();
        }
        return tvService.getQiniuToken()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    public static Observable<BaseObjModel<CheckModel>> checkByUserId() {
        if (tvService == null) {
            HttpHelper.initialize();
        }
        return tvService.checkByUserId()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    public static Observable<BaseObjModel<String>> updateDefinedName(String definedName) {
        if (tvService == null) {
            HttpHelper.initialize();
        }
        return tvService.updateDefinedName(definedName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    public static Observable<BaseObjModel<String>> deleteDefinedChannel() {
        if (tvService == null) {
            HttpHelper.initialize();
        }
        return tvService.deleteDefinedChannel()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }
}