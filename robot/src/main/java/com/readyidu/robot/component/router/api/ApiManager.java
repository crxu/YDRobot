package com.readyidu.robot.component.router.api;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.readyidu.robot.YDRobot;
import com.readyidu.robot.AppConfig;
import com.readyidu.robot.component.router.model.TvInfo;
import com.readyidu.robot.component.router.model.TvListInfo;
import com.readyidu.robot.component.router.utils.ACache;
import com.readyidu.robot.component.router.utils.FucUtil;
import com.readyidu.robot.model.BaseModel;
import com.readyidu.robot.utils.log.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Response;

public class ApiManager {

    private static final String TAG = ApiManager.class.getSimpleName();
    private static final String KEY_TV_CHANNEL = "key_tv_channel";

    private ApiService mApiService;

    private static class ApiManagerHolder {
        private static ApiManager apiManager = new ApiManager(ApiServiceImpl.apiService);
    }

    public static ApiManager getInstance() {
        return ApiManagerHolder.apiManager;
    }

    private ApiManager(ApiService apiService) {
        this.mApiService = apiService;
    }

    public ArrayList<TvInfo> getCacheList() {
        ACache mCache = ACache.get(YDRobot.getInstance().getContext());
        String json = mCache.getAsString(KEY_TV_CHANNEL);//SPUtil.getString(BaseAppContext.getContext(), SP_NAME, KEY_TV_CHANNEL, "");
        LogUtils.e("getTvlList******->" + json);

        ArrayList<TvInfo> list = null;
        try {
            if (!TextUtils.isEmpty(json)) {
                list = new Gson().fromJson(json, new TypeToken<ArrayList<TvInfo>>() {
                }.getType());
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return list;
    }

    public void clearCache() {
        try {
            ACache mCache = ACache.get(YDRobot.getInstance().getContext());
            mCache.clear();
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    //读取内置的命令
    public ArrayList<TvInfo> getDefaultOrderList() {
        ArrayList<TvInfo> list = null;

        try {
            String contents = FucUtil.readFile(YDRobot.getInstance().getContext(), "tv_order.json", "utf-8");
            if (!TextUtils.isEmpty(contents)) {
                list = new Gson().fromJson(contents, new TypeToken<ArrayList<TvInfo>>() {
                }.getType());
            }

        } catch (Exception e) {
            LogUtils.e(e);
        }
        return list;
    }

    public List<TvInfo> getTvlList() {
        try {
            Call<TvListInfo> call = mApiService.getTvlList();
            Response<TvListInfo> repo = call.execute();
            if (repo.isSuccessful() && null != repo.body() && repo.body().code == 200) {
                //添加缓存
                ACache mCache = ACache.get(YDRobot.getInstance().getContext());
                mCache.put(KEY_TV_CHANNEL, repo.body().data.toString(), 2 * ACache.TIME_HOUR);

                return repo.body().data;
            }

        } catch (Exception e) {
            LogUtils.e(e);
        }

        return null;
    }

    /**
     * 获取当前笼子
     */
    public String getCurrentCage() {
        String cage = AppConfig.mCurrentCageType;
        if (TextUtils.isEmpty(cage)) {
            cage = AppConfig.CAGE_NONE;
        }
        return cage;
    }

    /**
     * 是否在笼子里
     */
    public boolean isInCage() {
        String cage = getCurrentCage();

        return AppConfig.TYPE_MENU.equals(cage)
                || AppConfig.TYPE_MUSIC.equals(cage)
                || AppConfig.TYPE_NEWS.equals(cage)
                || AppConfig.TYPE_VNEWS.equals(cage)
                || AppConfig.TYPE_WEATHER.equals(cage)
                || AppConfig.TYPE_TV.equals(cage)
                || AppConfig.TYPE_IFLY.equals(cage);

    }
//
//    /**
//     * 是否在TV笼子里
//     */
//    public boolean isInTvCage() {
//        String cage = getCurrentCage();
//        return AppConfig.CAGE_TV_LIVE.equals(cage);
//    }

    /**
     * 获取小益大脑回复
     */
    public Observable<BaseModel> getBrainReply(final String content) {
        return Observable.just(getCurrentCage())
                .delay(500, TimeUnit.MILLISECONDS)
                .flatMap(new Function<String, ObservableSource<BaseModel>>() {
                    @Override
                    public ObservableSource<BaseModel> apply(@NonNull String type) throws Exception {
                        boolean openCacheFlag = false;
                        if (AppConfig.TYPE_MENU.equals(type)) {
                            return mApiService.getBrainReply(AppConfig.BASE_API_URL + "brain/menu", content);

                        } else if (AppConfig.TYPE_MUSIC.equals(type)) {
                            return mApiService.getBrainReply(AppConfig.BASE_API_URL + "brain/music", content);

                        } else if (AppConfig.TYPE_NEWS.equals(type)) {
                            return mApiService.getBrainReply(AppConfig.BASE_API_URL + "brain/news", content);

                        } else if (AppConfig.TYPE_VNEWS.equals(type)) {
                            return mApiService.getBrainReply(AppConfig.BASE_API_URL + "brain/videoNews", content);

                        } else if (AppConfig.TYPE_WEATHER.equals(type)) {
                            return mApiService.getBrainReply(AppConfig.BASE_API_URL + "brain/weather", content);

                        } else if (AppConfig.TYPE_IFLY.equals(type)) {

                            return mApiService.getBrainReply(AppConfig.BASE_API_URL + "brain/calendarAndcalculator", content);
                        } else if (AppConfig.TYPE_BAIKE.equals(type)) {

                            openCacheFlag = false;
                        } else if (AppConfig.TYPE_TV.equals(type)) {

                            return mApiService.getBrainReply(AppConfig.BASE_API_URL + "brain/tvLive", content);
                        } else if (AppConfig.TYPE_CALCULATE.equals(type)) {

                            openCacheFlag = false;
                        } else if (AppConfig.TYPE_CALENDAR.equals(type)) {

                            openCacheFlag = false;
                        }

                        return mApiService.getBrainReply(AppConfig.BASE_API_URL + "brain", openCacheFlag, content);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }

//    /**
//     * 获取URL参数
//     */
//    public Observable<RespParamsInfo> getParams() {
//        //先读取缓存的URL
//        SharedPreferences sp = YDRobot.getInstance().getContext().getSharedPreferences(AppConfig.SP_XY_APP, Context.MODE_PRIVATE);
//        String url = sp.getString(AppConfig.KEY_API_URL, "");
//        if (!TextUtils.isEmpty(url)) {
//            AppConfig.BASE_API_URL = url;
//        }
//
//        String key = "561527c1095ed941e6282729738d7fd1";
//        String param = DeviceUtils.getDeviceRegisterInfo();
//        return mApiService
//                .getParams(AesUtils.aesEncode(key, param), "0", "7")
//                .flatMap(new Function<RespParamsInfo, ObservableSource<RespParamsInfo>>() {
//                    @Override
//                    public ObservableSource<RespParamsInfo> apply(@NonNull RespParamsInfo respParamsInfo) throws Exception {
//                        if (null != respParamsInfo && respParamsInfo.isValid()) {
//                            String apiUrl = respParamsInfo.data.brainUrl;
//                            SharedPreferences sp = YDRobot.getInstance().getContext().getSharedPreferences("sp_xy_id", Context.MODE_PRIVATE);
//
//                            if (!apiUrl.endsWith("\\/")) {
//                                apiUrl += "/";
//                            }
//
//                            sp.edit().putString(AppConfig.KEY_API_URL, apiUrl).apply();
//                            AppConfig.BASE_API_URL = apiUrl;
//                        }
//
//                        return Observable.just(respParamsInfo);
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .unsubscribeOn(Schedulers.io());
//    }

}
