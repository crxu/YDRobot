package com.readyidu.robot;

import com.readyidu.robot.model.ExpandModel;

import java.util.ArrayList;

public class AppConfig {

    public static String BASE_API_URL;
    public static String BASE_API_URL_TV;

    private static int mode = 3;   //0 线下内网；1 线下外网；2 预发布环境；3 生产环境

    static {
        switch (mode) {
            case 0:
                BASE_API_URL = "http://192.168.4.168:8081/";
                BASE_API_URL_TV = "http://192.168.4.104:9092/";
                break;
            case 1:
                BASE_API_URL = "http://112.35.30.146:8666/";
                BASE_API_URL_TV = "http://192.168.4.104:9092/";
                break;
            case 2:
                BASE_API_URL = "http://pre.brain.readyidu.com:8666/";
                BASE_API_URL_TV = "http://47.97.21.98:9092/";
                break;
            case 3:
                BASE_API_URL = "http://brain.readyidu.com:8666/";
                BASE_API_URL_TV = "http://live.readyidu.com/";
                break;
        }
    }

    public static void setBaseApiUrl(String baseApiUrl) {
        BASE_API_URL = baseApiUrl;
    }

    public static void setBaseApiUrlTv(String baseApiUrlTv) {
        BASE_API_URL_TV = baseApiUrlTv;
    }

    public static String APP_ID = "10002";
    public static String APP_PASSWORD = "123456";
    public static String APP_USERID = "1005159";
    public static String APP_NICK_NAME = "默认账户";
    public static String location_id = "3372";
    public static final String APP_API_VERSION = "1.1.0";

    public static final String SP_XY_APP = "sp_xy_app";
    public static final String KEY_DEVICE_ID = "key_device_id";
    public static final String KEY_UUID = "key_uuid";
    public static final String KEY_API_URL = "key_api_url";

    public static final String CAGE_TV_LIVE = "cage_tv_live";//小益TV笼子
    public static final String CAGE_NONE = "none";//小益TV笼子
    public static String mCurrentCageType = CAGE_NONE;//标识当前笼子类型

    //小益笼子，多app共享
    public static final String SP_XY_CAGE = "sp_xy_cage";
    public static final String KEY_CURRENT_CAGE_TYPE = "key_current_cage_type";//标识当前笼子类型

    public static final String USER_QUESTION = "user_question";

    public static final String TYPE_AUTO_REPLY = "autoReply";//文本自动回复
    public static final String TYPE_IFLY = "xunfei";//讯飞自动回复
    public static final String TYPE_BAIKE = "baike";//百科自动回复
    public static final String TYPE_MULTIPLE = "Multiple";//多类型
    public static final String TYPE_WEATHER = "weather";//天气
    public static final String TYPE_MUSIC = "music";//音乐
    public static final String TYPE_MENU = "menu";//菜谱
    public static final String TYPE_NEWS = "news";//新闻
    public static final String TYPE_VNEWS = "vnews";//视频新闻
    public static final String TYPE_FILM = "video";//电影
    public static final String TYPE_PHRASE = "phrase";//成语接龙
    public static final String TYPE_POETRY = "poetry";//诗词
    public static final String TYPE_JOKE = "joke";//笑话
    public static final String TYPE_TV = "tvLive";//直播
    public static final String TYPE_CALCULATE = "calculate";//计算
    public static final String TYPE_CALENDAR = "calendar";//日历
    public static final String TYPE_SHARE = "share";//股票
    public static final String TYPE_FM = "fm";//日历
    public static final String TYPE_lottery = "lottery";//日历

    public static final String TYPE_robet= "robet";//日历

    public static ArrayList<ExpandModel> expandModels = new ArrayList<>();

    static {
        expandModels.add(new ExpandModel(R.drawable.expand_weather, "天气", "我要查天气", AppConfig.TYPE_WEATHER));
        expandModels.add(new ExpandModel(R.drawable.expand_cook, "菜谱", "我要看菜谱", AppConfig.TYPE_MENU));
        expandModels.add(new ExpandModel(R.drawable.expand_music, "音乐", "我要听音乐", AppConfig.TYPE_MUSIC));
        expandModels.add(new ExpandModel(R.drawable.expand_calendar, "节日", "我要查日历", AppConfig.TYPE_IFLY));
        expandModels.add(new ExpandModel(R.drawable.expand_news, "新闻", "我要看新闻", AppConfig.TYPE_VNEWS));
        expandModels.add(new ExpandModel(R.drawable.expand_calcul, "计算器", "我要算数", AppConfig.TYPE_IFLY));
        expandModels.add(new ExpandModel(R.drawable.expand_live, "电视频道", "我要看电视", AppConfig.TYPE_TV));
        expandModels.add(new ExpandModel(R.drawable.expand_baike, "百科", "我要查百科", AppConfig.TYPE_BAIKE));
        expandModels.add(new ExpandModel(R.drawable.gupiao, "股票", "您要查找什么股票信息呢？(例如 帮我查科大讯飞股票)", AppConfig.TYPE_SHARE));
        expandModels.add(new ExpandModel(R.drawable.expand_fm, "FM", "我要听FM", AppConfig.TYPE_FM));
        //expandModels.add(new ExpandModel(R.drawable.expand_news, "彩票", "彩票", AppConfig.TYPE_lottery));
        expandModels.add(new ExpandModel(0, "", "", ""));
        expandModels.add(new ExpandModel(0, "", "", ""));
        expandModels.add(new ExpandModel(0, "", "", ""));
        expandModels.add(new ExpandModel(0, "", "", ""));
        expandModels.add(new ExpandModel(0, "", "", ""));
        expandModels.add(new ExpandModel(0, "", "", ""));
    }


    public static String[] multiStrings = new String[]{"天气", "音乐", "新闻", "视频新闻", "电视频道","彩票", "菜谱", "节日", "计算器", "百科"};
    public static String[] multiTypes = new String[]{AppConfig.TYPE_WEATHER, AppConfig.TYPE_MUSIC, AppConfig.TYPE_NEWS, AppConfig.TYPE_VNEWS,
            AppConfig.TYPE_TV, AppConfig.TYPE_lottery,AppConfig.TYPE_MENU, AppConfig.TYPE_IFLY, AppConfig.TYPE_IFLY, AppConfig.TYPE_BAIKE};
    public static int[] multiResIds =
            new int[]{R.drawable.expand_weather, R.drawable.expand_music, R.drawable.expand_news, R.drawable.expand_vnews,
                    R.drawable.expand_live, R.drawable.expand_news,R.drawable.expand_cook, R.drawable.expand_calendar, R.drawable.expand_calcul, R.drawable.expand_baike};
}