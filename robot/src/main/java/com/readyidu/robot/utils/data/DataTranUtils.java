package com.readyidu.robot.utils.data;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.readyidu.robot.AppConfig;
import com.readyidu.robot.model.BaikeModel;
import com.readyidu.robot.model.BaseModel;
import com.readyidu.robot.model.business.menu.Menu;
import com.readyidu.robot.model.business.music.Music;
import com.readyidu.robot.model.business.news.News;
import com.readyidu.robot.model.business.news.VideoNewsDetail;
import com.readyidu.robot.model.business.news.VideoNewsMenu;
import com.readyidu.robot.model.business.share.Share;
import com.readyidu.robot.model.business.tv.TvChannel;
import com.readyidu.robot.model.business.weather.Weather;
import com.readyidu.robot.utils.log.LogUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by gx on 2017/10/17.
 */
public class DataTranUtils {

    private static boolean check(BaseModel baseModel, String type) {
        return baseModel.data != null && type.equals(baseModel.data.type);
    }


    public static Share tranShare(BaseModel baseModel) {
//        if (check(baseModel, AppConfig.TYPE_SHARE)) {
//            try{
//
//            }catch (Exception e){
//                return null;
//            }
//        }

        try {
            if (baseModel.data.arg != null) {
                List result = (List) baseModel.data.arg.get("result");
                if (result != null && result.size() > 0) {
                    LinkedTreeMap content = (LinkedTreeMap) result.get(0);
                    if (content != null) {
//                        HashMap contentMap = new HashMap();
                        return new Gson().fromJson(new Gson().toJson(content), Share.class);
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static Weather tranWeather(BaseModel baseModel) {
        if (check(baseModel, AppConfig.TYPE_WEATHER)) {
            return new Gson().fromJson(baseModel.data.content + "", Weather.class);
        }
        return null;
    }

    public static BaikeModel tranBaike(BaseModel baseModel) {
        if (check(baseModel, AppConfig.TYPE_BAIKE)) {
            return new Gson().fromJson(baseModel.data.content + "", BaikeModel.class);
        }
        return null;
    }


    public static int getTotal(BaseModel baseModel) {
        String jsonStr1 = new Gson().toJson(baseModel.data.content);
        try {
            JSONObject jsonObj = new JSONObject(jsonStr1);
            String jsonStr2 = jsonObj.getString("datas");

            JSONObject jsonObject = new JSONObject(jsonStr2);
            if (jsonObject.has("total")) {
                return jsonObject.getInt("total");
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return 0;
    }

    /**
     * 视频新闻是否查询到搜索结果，没有的话设置默认推荐数据
     *
     * @param baseModel true 有；false 无
     * @return
     */
    public static boolean videoNewsSearchHasResult(BaseModel baseModel) {
        try {
            String jsonStr1 = new Gson().toJson(baseModel.data.content);
            JSONObject jsonObj = new JSONObject(jsonStr1);
            if (jsonObj.has("isDefault")) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return false;
    }

    private static String getContent(BaseModel baseModel) {
        String jsonStr1 = new Gson().toJson(baseModel.data.content);
        try {
            JSONObject jsonObj = new JSONObject(jsonStr1);
            String jsonStr2 = jsonObj.getString("datas");

            JSONObject jsonObject = new JSONObject(jsonStr2);
//            if (jsonObject.has("total")) {
//                totalSize = jsonObject.getInt("total");
//            }

            if (jsonObject.has("datas")) {
                JSONArray jsonArray = jsonObject.getJSONArray("datas");
                return jsonArray.toString();
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return "";
    }

    public static List<News> tranNews(BaseModel baseModel) {
        if (check(baseModel, AppConfig.TYPE_NEWS)) {
            String content = getContent(baseModel);
            if (!TextUtils.isEmpty(content)) {
                return new Gson().fromJson(content, new TypeToken<List<News>>() {
                }.getType());
            }
        }
        return null;
    }

    /**
     * 获取视频新闻详情
     *
     * @param baseModel
     * @return
     */
    public static List<VideoNewsDetail> tranVideoNewsDetail(BaseModel baseModel) {
        String content = getContent(baseModel);
        if (!TextUtils.isEmpty(content)) {
            return new Gson().fromJson(content, new TypeToken<List<VideoNewsDetail>>() {
            }.getType());
        }
        return null;
    }

    /**
     * 获取搜索出来的结果的分类位置
     *
     * @param searchList
     * @param oriList
     * @return
     */
    public static int getSelectItemPosition(List<VideoNewsMenu> searchList, List<VideoNewsMenu> oriList) {
        int pos = -9999;
        VideoNewsMenu selectMenu;
        for (VideoNewsMenu newsMenu : searchList) {
            if (newsMenu.selected) {
                selectMenu = newsMenu;
                for (int i = 0; i < oriList.size(); i++) {
                    if (selectMenu.id == oriList.get(i).id) {
                        pos = i;
                    }
                }
            }
        }
        return pos;
    }

    public static List<Menu> tranMenu(BaseModel baseModel) {
        if (check(baseModel, AppConfig.TYPE_MENU)) {
            String content = getContent(baseModel);
            if (!TextUtils.isEmpty(content)) {
                return new Gson().fromJson(content, new TypeToken<List<Menu>>() {
                }.getType());
            }
        }
        return null;
    }

    public static List<Music> tranMusic(BaseModel baseModel) {
        if (check(baseModel, AppConfig.TYPE_MUSIC)) {
            String content = getContent(baseModel);
            if (!TextUtils.isEmpty(content)) {
                return new Gson().fromJson(content, new TypeToken<List<Music>>() {
                }.getType());
            }
        }
        return null;
    }

//    public static List<TvChannel> tranTv(BaseModel baseModel) {
//        if (check(baseModel, AppConfig.TYPE_TV)) {
//            try {
//                String content = getTVChannelContent(baseModel);
//                JSONObject jsonObject = new JSONObject(content);
//                if (jsonObject.has("tvDemands")) {
//                    JSONArray channelArray = jsonObject.getJSONArray("tvDemands");
//                    if (!TextUtils.isEmpty(channelArray.toString())) {
//                        return new Gson().fromJson(channelArray.toString(),
//                                new TypeToken<List<TvChannel>>() {}.getType());
//                    }
//                } else {
//                    return null;
//                }
//            } catch (Exception e) {
//                LogUtils.e(e);
//            }
//        }
//        return null;
//    }

    public static List<TvChannel> tranTvDetail(BaseModel baseModel) {
        if (check(baseModel, AppConfig.TYPE_TV)) {
            try {
                String content = getTVChannelContent(baseModel);
                if (!TextUtils.isEmpty(content)) {
                    return new Gson().fromJson(content, new TypeToken<List<TvChannel>>() {
                    }.getType());
                }
            } catch (Exception e) {
                LogUtils.e(e);
            }
        }
        return null;
    }


    private static String getTVChannelContent(BaseModel baseModel) {
        String jsonStr1 = new Gson().toJson(baseModel.data.content);

        try {
            JSONObject jsonObj = new JSONObject(jsonStr1);

            if (jsonObj.has("channelList")) {
                JSONObject jsonObject = jsonObj.getJSONObject("channelList");
                return jsonObject.toString();
            }

            if (jsonObj.has("channelInfoList")) {
                JSONArray jsonArray = jsonObj.getJSONArray("channelInfoList");
                return jsonArray.toString();
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return "";
    }
}