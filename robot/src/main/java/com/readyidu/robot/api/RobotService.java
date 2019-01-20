package com.readyidu.robot.api;

import com.readyidu.robot.model.BaseListModel;
import com.readyidu.robot.model.BaseModel;
import com.readyidu.robot.model.BaseObjModel;
import com.readyidu.robot.model.DataModel;
import com.readyidu.robot.model.business.index.HotRecommendModel;
import com.readyidu.robot.model.business.tv.ProgrammeListModel;
import com.readyidu.robot.model.business.tv.TvChannel;
import com.readyidu.robot.model.business.tv.TvChannelParent;
import com.readyidu.robot.model.business.tv.TvDetail;
import com.readyidu.robot.model.business.tv.TvSource;
import com.readyidu.robot.model.business.tv.TvType;
import com.readyidu.robot.model.business.tv.base.Base;
import com.readyidu.robot.model.business.tv.base.BaseContent;
import com.readyidu.robot.model.business.tv.base.BaseData;
import com.readyidu.robot.ui.fm.util.AlbumHistoryBase;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by gx on 2017/10/11.
 */
public interface RobotService {

    /**
     * 获取笼子之外语音输入数据
     */
    @FormUrlEncoded
    @POST("brain")
    Observable<BaseModel> getRobotResponse(
            @Field("content") String deviceKey,
            @Field("openCacheFlag") boolean openCacheFlag
    );

    /**
     * 获取菜谱笼子语音输入数据
     */
    @FormUrlEncoded
    @POST("brain/menu")
    Observable<BaseModel> getRobotMenuResponse(
            @Field("content") String deviceKey,
            @Field("openCacheFlag") boolean openCacheFlag,
            @Field("pageNo") int pageNo,
            @Field("pageSize") int pageSize
    );

    /**
     * 获取音乐笼子语音输入数据
     */
    @FormUrlEncoded
    @POST("brain/music")
    Observable<BaseModel> getRobotMusicResponse(
            @Field("content") String deviceKey,
            @Field("openCacheFlag") boolean openCacheFlag,
            @Field("pageNo") int pageNo,
            @Field("pageSize") int pageSize
    );

    /**
     * 获取音乐播放地址
     *
     * @param songId
     * @return
     */
    @GET("MP3Url")
    Observable<BaseModel> getRobotMusicLinkResponse(
            @Query("songId") String songId
    );

    /**
     * 获取天气菜谱笼子语音输入数据
     */
    @FormUrlEncoded
    @POST("brain/weather")
    Observable<BaseModel> getRobotWeatherResponse(
            @Field("content") String deviceKey,
            @Field("openCacheFlag") boolean openCacheFlag
    );

    /**
     * 获取股票笼子语音输入数据
     */
    @FormUrlEncoded
    @POST("/brain/xunfei/stock")
    Observable<BaseModel> getRobotShareResponse(
            @Field("content") String deviceKey,
            @Field("openCacheFlag") boolean openCacheFlag
    );

    /**
     * 获取新闻笼子语音输入数据
     */
    @FormUrlEncoded
    @POST("brain/news")
    Observable<BaseModel> getRobotNewsResponse(
            @Field("content") String deviceKey,
            @Field("openCacheFlag") boolean openCacheFlag,
            @Field("pageNo") int pageNo,
            @Field("pageSize") int pageSize
    );

    /**
     * 下载音乐歌词文件
     *
     * @param fileUrl
     * @return
     */
    @GET
    Call<ResponseBody> downloadRobotMusicLrc(@Url String fileUrl);

    /**
     * 获取频道分类列表
     *
     * @return
     */
    @GET("getChannelTypeList")
    Observable<BaseListModel<TvType>> getRobotTvTypeList();

    /**
     * 获取所有频道列表
     *
     * @return
     */
    @GET("/getChannelList")
    Observable<BaseObjModel<TvChannelParent>> getRobotTvChannelList();

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
     * 获取电视笼子语音输入数据
     */
    @FormUrlEncoded
    @POST("brain/tvLive")
    Observable<Base<BaseData<BaseContent>>> getRobotTvLiveResponse(
            @Field("content") String deviceKey,
            @Field("openCacheFlag") boolean openCacheFlag
    );

    /**
     * 根据频道名称获取节目列表
     *
     * @param tvName
     * @return
     */
    @GET("getSourceListByTvName")
    Observable<BaseListModel<TvDetail>> getRobotProgramListByName(
            @Query("tvName") String tvName
    );

    /**
     * 上报无效的源
     *
     * @param source
     * @return
     */
    @FormUrlEncoded
    @POST("insertReport")
    Observable<Base<Object>> putErrorTvSource(
            @Field("source") String source
    );

    /**
     * 获取计算器和日历笼子语音输入数据
     */
    @FormUrlEncoded
    @POST("brain/calendarAndcalculator")
    Observable<DataModel> getRobotCACResponse(
            @Field("content") String deviceKey,
            @Field("openCacheFlag") String openCacheFlag
    );

    /**
     * 获取热门推荐
     */
    @GET("recommend")
    Observable<Base<HotRecommendModel>> getHotRemommend(
            @Query("pageSize") int pageSize
    );

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
     * 获取所有视频分类
     *
     * @return
     */
    @GET("getTypeList")
    Observable<BaseListModel<TvType>> getTypeList();

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

    /**
     * 获取视频新闻相关菜单分类
     */
    @FormUrlEncoded
    @POST("brain/videoNews")
    Observable<BaseModel> getRobotVideoNewsResponse(
            @Field("content") String deviceKey,
            @Field("openCacheFlag") boolean openCacheFlag,
            @Field("pageNo") int pageNo,
            @Field("pageSize") int pageSize
    );

    @FormUrlEncoded
    @POST("fmHistorical/add")
    Observable<BaseModel> fmHistoricalAdd(@Field("albumOrColumnOrRadioJson") String albumOrColumnOrRadioJson, @Field("contentId") long contentId
            , @Field("type") int type
            , @Field("userId") String userId
    );

    @FormUrlEncoded
    @POST("fmHistorical/delete")
    Observable<BaseModel> fmHistoricalDelete(@Field("histRecdIds") String histRecdIds
    );

    @GET("fmHistorical/list")
    Observable<AlbumHistoryBase> fmHistoricalList(@Query("type") int type
            , @Query("userId") String userId, @Query("pageNo") int pageNo,
                                                  @Query("pageSize") int pageSize
    );

    @FormUrlEncoded
    @POST("brain/fmApp")
    Observable<BaseModel> fmApp(@Field("content") String content,
                                @Field("openCacheFlag") boolean openCacheFlag);
}