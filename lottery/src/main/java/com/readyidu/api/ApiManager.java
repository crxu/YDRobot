package com.readyidu.api;



import com.readyidu.api.response.LotteryInfo;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class ApiManager {

    private ApiService mApiService;

    public ApiManager(ApiService apiService) {
        this.mApiService = apiService;
    }


    public Observable<LotteryInfo> getLottery(String content) {
        return mApiService.getLottery(false,content)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .unsubscribeOn(Schedulers.io());
    }

}
