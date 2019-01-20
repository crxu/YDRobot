package com.readyidu.robot.component.router.api;

import com.readyidu.robot.model.BaseModel;
import com.readyidu.robot.component.router.api.response.RespParamsInfo;
import com.readyidu.robot.component.router.model.TvListInfo;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ApiService {

    /**
     * 获取初始参数
     * http://aged-server.readyidu.com  http://192.168.4.99:9988
     */
    @FormUrlEncoded
    @POST("http://aged-server.readyidu.com/appInit/getParams.do")
    Observable<RespParamsInfo> getParams(
            @Field("params") String params,
            @Field("paramsPlatform") String paramsPlatform,
            @Field("paramsVersion") String paramsVersion
    );

    @FormUrlEncoded
    @POST
    Observable<BaseModel> getBrainReply(
            @Url String URL,
            @Field("openCacheFlag") boolean openCacheFlag,
            @Field("content") String content
    );

    @FormUrlEncoded
    @POST
    Observable<BaseModel> getBrainReply(
            @Url String URL,
            @Field("content") String content
    );

    /**
     * 获取TV节目映射列表
     */
//    http://192.168.4.99:6262/router/channel/getMapper.do
//    http://112.35.30.146:19090/router/channel/getMapper.do
    @GET("http://112.35.30.146:19090/router/channel/getMapper.do")
    Call<TvListInfo> getTvlList();

}
