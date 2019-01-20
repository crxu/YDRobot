package com.readyidu.robot.api;

import com.readyidu.robot.model.BaseListModel;
import com.readyidu.robot.model.BaseObjModel;
import com.readyidu.robot.model.business.tv.CheckModel;
import com.readyidu.robot.model.business.tv.ProgrammeListModel;
import com.readyidu.robot.model.business.tv.TvChannel;
import com.readyidu.robot.model.business.tv.TvChannelParent;
import com.readyidu.robot.model.business.tv.TvSource;
import com.readyidu.robot.model.business.tv.TvType;
import com.readyidu.robot.model.business.tv.base.Base;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by gx on 2017/12/27.
 */
public interface TVService {

    /**
     * 获取所有视频分类
     *
     * @return
     */
    @GET("getTypeList")
    Observable<BaseListModel<TvType>> getTypeList();

    /**
     * 根据视频分类获取所有播放视频
     *
     * @param typeId
     * @return
     */
    @GET("getNewChannelListByTypeId")
    Observable<BaseObjModel<TvChannelParent>> getNewChannelListByTypeId(
            @Query("typeId") String typeId
    );

    /**
     * 获取直播源
     *
     * @param id
     * @return
     */
    @GET("getSourceById")
    Observable<Base<String>> getSourceById(@Query("id") int id);

    /**
     * 获取点播源
     *
     * @param id
     * @return
     */
    @GET("getDemandById")
    Observable<Base<String>> getDemandById(@Query("id") int id);

    /**
     * 获取uri播放地址
     *
     * @return
     */
    @GET("getSourceUrl")
    Observable<BaseObjModel<TvSource>> getRobotTvSourceUri(
            @Query("sourceUri") String uri
    );

    /**
     * 获取播放节目
     *
     * @param channelId
     * @return
     */
    @GET("getChannelPlaybill")
    Observable<Base<ProgrammeListModel>> getChannelPlaybill(@Query("channelId") String channelId);

    /**
     * 获取电视剧所有集数
     *
     * @return
     */
    @GET("selectTvShowByChannelId")
    Observable<BaseListModel<TvChannel>> getNumberOfDramas(
            @Query("channelId") int channelId
    );

    @FormUrlEncoded
    @POST("app/getCustomizedList")
    Observable<BaseObjModel<String>> getCustomizedList(@Field("url") String url);

    @GET("app/bundling")
    Observable<BaseObjModel<String>> bundling(@Query("token") String token,
                                              @Query("tvDeviceId") String tvDeviceId,
                                              @Query("nickname") String nickname);

    @GET("app/callBackUpdate")
    Observable<BaseObjModel<String>> callBackUpdate(@Query("key") String key,
                                                    @Query("token") String token,
                                                    @Query("nickname") String nickname);

    @GET("app/getQiniuToken")
    Observable<BaseObjModel<String>> getQiniuToken();

    @GET("app/checkByUserId")
    Observable<BaseObjModel<CheckModel>> checkByUserId();

    @GET("app/updateDefinedName")
    Observable<BaseObjModel<String>> updateDefinedName(@Query("definedName") String definedName);

    @GET("app/deleteDefinedChannel")
    Observable<BaseObjModel<String>> deleteDefinedChannel();
}