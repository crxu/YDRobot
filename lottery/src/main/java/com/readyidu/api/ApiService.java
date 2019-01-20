package com.readyidu.api;

import com.readyidu.api.response.LotteryInfo;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {

    @FormUrlEncoded
    @POST("brain/xunfei/lottery")
    Observable<LotteryInfo> getLottery(@Field("openCacheFlag") boolean openCacheFlag, @Field("content") String content);
}
