package com.readyidu.robot.api.impl;

import com.readyidu.basic.models.LoginUser;
import com.readyidu.robot.api.HttpHelper;
import com.readyidu.robot.api.RobotService;
import com.readyidu.robot.api.exception.BaseApiException;
import com.readyidu.robot.model.BaseListModel;
import com.readyidu.robot.model.BaseModel;
import com.readyidu.robot.model.BaseObjModel;
import com.readyidu.robot.model.business.index.HotRecommendModel;
import com.readyidu.robot.model.business.tv.TvDetail;
import com.readyidu.robot.model.business.tv.base.Base;
import com.readyidu.robot.model.business.tv.base.BaseContent;
import com.readyidu.robot.model.business.tv.base.BaseData;
import com.readyidu.robot.ui.fm.util.AlbumHistoryBase;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by gx on 2017/10/11.
 */
public class RobotServiceImpl {
    public static RobotService robotService;


    public static Observable<BaseModel> getRobotResponse(String requestContent, boolean isCache) {
        if (robotService == null) {
            HttpHelper.initialize();
        }
        return robotService.getRobotResponse(requestContent, isCache)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());

    }


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

    /**
     * 获取菜谱笼子语音输入数据
     *
     * @param requestContent
     * @param isCache
     */
    public static Observable<BaseModel> getRobotMenuResponse(String requestContent, boolean isCache, int pageNo, int pageSize) {
        if (robotService == null) {
            HttpHelper.initialize();
        }
        return robotService.getRobotMenuResponse(requestContent, isCache, pageNo, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());

    }

    /**
     * 获取音乐笼子语音输入数据
     *
     * @param requestContent
     * @param isCache
     */
    public static Observable<BaseModel> getRobotMusicResponse(String requestContent, boolean isCache, int pageNo, int pageSize) {
        if (robotService == null) {
            HttpHelper.initialize();
        }
        return robotService.getRobotMusicResponse(requestContent, isCache, pageNo, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());

    }

    /**
     * 获取音乐播放地址
     *
     * @param songId
     * @return
     */
    public static Observable<BaseModel> getRobotMusicLinkResponse(String songId) {
        if (robotService == null) {
            HttpHelper.initialize();
        }
        return robotService.getRobotMusicLinkResponse(songId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    /**
     * 获取天气菜谱笼子语音输入数据
     *
     * @param requestContent
     * @param isCache
     */
    public static Observable<BaseModel> getRobotWeatherResponse(String requestContent, boolean isCache) {
        if (robotService == null) {
            HttpHelper.initialize();
        }
        return robotService.getRobotWeatherResponse(requestContent, isCache)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    /**
     * 获取股票笼子语音输入数据
     *
     * @param requestContent
     * @param isCache
     */
    public static Observable<BaseModel> getRobotShareResponse(String requestContent, boolean isCache) {
        if (robotService == null) {
            HttpHelper.initialize();
        }
        return robotService.getRobotShareResponse(requestContent, isCache)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }


    /**
     * 获取新闻笼子语音输入数据
     *
     * @param requestContent
     * @param isCache
     */
    public static Observable<BaseModel> getRobotNewsResponse(String requestContent, boolean isCache, int pageNo, int pageSize) {
        if (robotService == null) {
            HttpHelper.initialize();
        }
        return robotService.getRobotNewsResponse(requestContent, isCache, pageNo, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());

    }
//
//    /**
//     * 获取所有频道分类列表
//     *
//     * @return
//     */
//    public static Observable<BaseListModel<TvType>> getRobotTvType() {
//        return robotService
//                .getRobotTvTypeList()
//                .subscribeOn(Schedulers.io())
//                .flatMap(new Function<BaseListModel<TvType>, ObservableSource<BaseListModel<TvType>>>() {
//                    @Override
//                    public ObservableSource<BaseListModel<TvType>> apply(BaseListModel<TvType> tvAreaBaseModel2) throws Exception {
//                        return flatResult(tvAreaBaseModel2);
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .unsubscribeOn(Schedulers.io());
//    }
//
//    /**
//     * 获取所有频道列表
//     *
//     * @return
//     */
//    public static Observable<BaseObjModel<TvChannelParent>> getRobotTvChannelList() {
//        return robotService
//                .getRobotTvChannelList()
//                .subscribeOn(Schedulers.io())
//                .flatMap(new Function<BaseObjModel<TvChannelParent>, ObservableSource<BaseObjModel<TvChannelParent>>>() {
//                    @Override
//                    public ObservableSource<BaseObjModel<TvChannelParent>> apply(BaseObjModel<TvChannelParent> tvAreaBaseModel2) throws Exception {
//                        return flatResult2(tvAreaBaseModel2);
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .unsubscribeOn(Schedulers.io());
//    }
//
//    /**
//     * 获取uri播放地址
//     *
//     * @param source
//     * @return
//     */
//    public static Observable<BaseObjModel<TvSource>> getRobotTvSourceUri(String source) {
//        return robotService
//                .getRobotTvSourceUri(source)
//                .subscribeOn(Schedulers.io())
//                .flatMap(new Function<BaseObjModel<TvSource>, ObservableSource<BaseObjModel<TvSource>>>() {
//                    @Override
//                    public ObservableSource<BaseObjModel<TvSource>> apply(BaseObjModel<TvSource> baseModel3) throws Exception {
//                        return flatResult2(baseModel3);
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .unsubscribeOn(Schedulers.io());
//    }

    /**
     * 获取电视直播笼子语音输入数据
     *
     * @param requestContent
     * @param isCache
     */
    public static Observable<BaseData<BaseContent>> getRobotTvLiveResponse(String requestContent, boolean isCache) {
        if (robotService == null) {
            HttpHelper.initialize();
        }
        return robotService
                .getRobotTvLiveResponse(requestContent, isCache)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<Base<BaseData<BaseContent>>, ObservableSource<BaseData<BaseContent>>>() {
                    @Override
                    public ObservableSource<BaseData<BaseContent>> apply(@NonNull Base<BaseData<BaseContent>> baseDataBaseTv) throws Exception {
                        return flatResult3(baseDataBaseTv);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    /**
     * 根据频道名称获取节目列表
     *
     * @param channelName
     * @return
     */
    public static Observable<BaseListModel<TvDetail>> getRobotProgramListByName(String channelName) {
        if (robotService == null) {
            HttpHelper.initialize();
        }
        return robotService
                .getRobotProgramListByName(channelName)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<BaseListModel<TvDetail>, ObservableSource<BaseListModel<TvDetail>>>() {
                    @Override
                    public ObservableSource<BaseListModel<TvDetail>> apply(@NonNull BaseListModel<TvDetail> baseListModel) throws Exception {
                        return flatResult(baseListModel);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    /**
     * 上报无效的源
     *
     * @param source
     * @return
     */
    public static Observable<Object> putErrorTvSource(String source) {
        if (robotService == null) {
            HttpHelper.initialize();
        }
        return robotService
                .putErrorTvSource(source)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<Base<Object>, ObservableSource<Object>>() {
                    @Override
                    public ObservableSource<Object> apply(@NonNull Base<Object> objectBase) throws Exception {
                        return flatResult3(objectBase);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

    /**
     * 获取热门推荐
     *
     * @return
     */
    public static Observable<HotRecommendModel> getHotRecommend(int pageSize) {
        if (robotService == null) {
            HttpHelper.initialize();
        }
        return robotService
                .getHotRemommend(pageSize)
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<Base<HotRecommendModel>, ObservableSource<HotRecommendModel>>() {
                    @Override
                    public ObservableSource<HotRecommendModel> apply(Base<HotRecommendModel> hotRecommendModelBase) throws Exception {
                        return flatResult3(hotRecommendModelBase);
                    }
                })
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

//    public static Observable<BaseObjModel<TvChannelParent>> getNewChannelListByTypeId(String typeId) {
//        return robotService
//                .getNewChannelListByTypeId(typeId)
//                .subscribeOn(Schedulers.io())
//                .flatMap(new Function<BaseObjModel<TvChannelParent>, ObservableSource<BaseObjModel<TvChannelParent>>>() {
//                    @Override
//                    public ObservableSource<BaseObjModel<TvChannelParent>> apply(BaseObjModel<TvChannelParent> hotRecommendModelBase) throws Exception {
//                        return flatResult2(hotRecommendModelBase);
//                    }
//                })
//                .unsubscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }
//
//    public static Observable<BaseListModel<TvType>> getTypeList() {
//        return robotService
//                .getTypeList()
//                .subscribeOn(Schedulers.io())
//                .flatMap(new Function<BaseListModel<TvType>, ObservableSource<BaseListModel<TvType>>>() {
//                    @Override
//                    public ObservableSource<BaseListModel<TvType>> apply(BaseListModel<TvType> hotRecommendModelBase) throws Exception {
//                        return flatResult(hotRecommendModelBase);
//                    }
//                })
//                .unsubscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }
//
//    public static Observable<String> getSourceById(int id) {
//        return robotService
//                .getSourceById(id)
//                .subscribeOn(Schedulers.io())
//                .flatMap(new Function<Base<String>, ObservableSource<String>>() {
//                    @Override
//                    public ObservableSource<String> apply(Base<String> hotRecommendModelBase) throws Exception {
//                        return flatResult3(hotRecommendModelBase);
//                    }
//                })
//                .unsubscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }
//
//    public static Observable<String> getDemandById(int id) {
//        return robotService
//                .getDemandById(id)
//                .subscribeOn(Schedulers.io())
//                .flatMap(new Function<Base<String>, ObservableSource<String>>() {
//                    @Override
//                    public ObservableSource<String> apply(Base<String> hotRecommendModelBase) throws Exception {
//                        return flatResult3(hotRecommendModelBase);
//                    }
//                })
//                .unsubscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }
//
//    public static Observable<ProgrammeListModel> getChannelPlaybill(String channelId) {
//        return robotService
//                .getChannelPlaybill(channelId)
//                .subscribeOn(Schedulers.io())
//                .flatMap(new Function<Base<ProgrammeListModel>, ObservableSource<ProgrammeListModel>>() {
//                    @Override
//                    public ObservableSource<ProgrammeListModel> apply(Base<ProgrammeListModel> hotRecommendModelBase) throws Exception {
//                        return flatResult3(hotRecommendModelBase);
//                    }
//                })
//                .unsubscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }
//
//    public static Observable<BaseListModel<TvChannel>> getNumberOfDramas(int id) {
//        return robotService
//                .getNumberOfDramas(id)
//                .subscribeOn(Schedulers.io())
//                .flatMap(new Function<BaseListModel<TvChannel>, ObservableSource<BaseListModel<TvChannel>>>() {
//                    @Override
//                    public ObservableSource<BaseListModel<TvChannel>> apply(BaseListModel<TvChannel> baseListModel) throws Exception {
//                        return flatResult(baseListModel);
//                    }
//                })
//                .unsubscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }

    /**
     * 获取视频新闻相关内容
     *
     * @param requestContent
     * @param isCache
     */
    public static Observable<BaseModel> getRobotVideoNewsData(String requestContent, boolean isCache,
                                                              int pageNo, int pageSize) {
        if (robotService == null) {
            HttpHelper.initialize();
        }
        return robotService.getRobotVideoNewsResponse(requestContent, isCache, pageNo, pageSize)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());

    }

    public static Observable<BaseModel> fmHistoricalAdd(String albumOrColumnOrRadioJson, long contentId, int type, String userId) {
        if (robotService == null) {
            HttpHelper.initialize();
        }
        return robotService.fmHistoricalAdd(albumOrColumnOrRadioJson, contentId, type, LoginUser.getUserId()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());

    }

    public static Observable<BaseModel> fmHistoricalDelete(String histRecdIds) {
        if (robotService == null) {
            HttpHelper.initialize();
        }
        return robotService.fmHistoricalDelete(histRecdIds).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());

    }

    public static Observable<AlbumHistoryBase> fmHistoricalList(int type, String userId, int pageNo,
                                                                int pageSize) {
        if (robotService == null) {
            HttpHelper.initialize();
        }
        return robotService.fmHistoricalList(type, LoginUser.getUserId(), pageNo, pageSize).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());

    }

    public static Observable<BaseModel> fmApp(String content,
                                              boolean openCacheFlag) {
        if (robotService == null) {
            HttpHelper.initialize();
        }
        return robotService.fmApp(content, openCacheFlag).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());

    }


}