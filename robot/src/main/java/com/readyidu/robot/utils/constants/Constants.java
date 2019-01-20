package com.readyidu.robot.utils.constants;

/**
 * @Autour: wlq
 * @Description: 常量管理类
 * @Date: 2017/10/13 11:05
 * @Update: 2017/10/13 11:05
 * @UpdateRemark:
 * @Version: V1.0
 */
public final class Constants {

    /*菜谱*/
    public static final String MENU_BEAN = "menu_bean";     //菜谱javaBean

    /*电视*/
    public static final String TV_SOURCE_LIST = "tv_source_list";     //电视源列表
    public static final String TV_CHANNEL_BEAN = "tv_channel_bean";     //电视频道
    public static final String TV_SOURCE_POSITION = "tv_source_position";  //播放源位置
    public static final String TV_CURRENT_SOURCE = "tv_current_source";  //当前播放源
    public static final String TV_CURRENT_PLAY_TIME = "tv_current_play_progress";   //当前视频播放进度位置（毫秒）
    public static final String TV_TOTAL_PLAY_TIME = "tv_total_play_progress";   //当前视频播放进度位置（毫秒）

    public static final String VIDEO_NEWS_ITEM_NAME = "VIDEO_NEWS_ITEM_NAME";   //视频新闻item名称

    public static final String TV_IS_CLICK_FULL_SCREEN = "tv_is_click_full";  //是否点击全屏
    public static final String TV_IS_SHOW_WIFI_DIALOG = "tv_is_show_wifi_dialog";  //是否显示wifi弹框
    public static final String VIDEO_NEWS_IS_SHOW_WIFI_DIALOG = "VIDEO_NEWS_IS_SHOW_WIFI_DIALOG";  //视频新闻是否显示wifi弹框
    public static final String TV_IS_LAND_HAND_CLOSE = "tv_is_land_hand_close";  //是否手动关闭横屏
    public static final String TV_LOADING_TYPE = "tv_loading_type";  //视频loading类型:0正在加载；1重新加载；2重播；3播放中
    public static final String TV_IS_SHOW_LAND_VOLUME_BRIGHT_TIP = "TV_IS_SHOW_LAND_VOLUME_BRIGHT_TIP"; //是否显示滑动教学界面

    public static final String CLASSIFY_POSITION = "CLASSIFY_POSITION"; //分类位置
    public static final String CLASSIFY_POSITION_TEMP = "CLASSIFY_POSITION_TEMP"; //分类位置
    public static final String CHANNEL_POSITION = "CHANNEL_POSITION"; //央视&卫视&本地频道位置索引
    public static final String SOURCE_POSITION = "SOURCE_POSITION"; //源位置索引
    public static final String SITCOM_POSITION = "SITCOM_POSITION"; //电视剧集位置索引
    public static final String SITCOM_LIVE_POSITION = "SITCOM_LIVE_POSITION"; //电视剧直播位置索引
    public static final String MOVIE_ON_DEMAND_SELECT_POSITION = "MOVIE_ON_DEMAND_SELECT_POSITION"; //某个电影分类下所选电影位置索引
    public static final String MOVIE_ON_DEMAND_CLASSIFY_POSITION = "MOVIE_ON_DEMAND_CLASSIFY_POSITION"; //某个电影分类位置索引
    public static final String TV_LOCAL_CLASSIFY_POSITION = "TV_LOCAL_CLASSIFY_POSITION";   //地方分类下的某个分类位置索引
    public static final String TV_LOCAL_CLASSIFY_CHANNEL_POSITION = "TV_LOCAL_CLASSIFY_CHANNEL_POSITION"; //地方分类下的某个分类下的某个频道位置索引
    public static final String TV_CARTOON_LIVE_POSITION = "TV_LOCAL_CLASSIFY_CHANNEL_POSITION"; //少儿动画直播位置
    public static final String TV_CARTOON_DEMAND_POSITION = "TV_LOCAL_CLASSIFY_CHANNEL_POSITION"; //少儿动画点播位置
}
