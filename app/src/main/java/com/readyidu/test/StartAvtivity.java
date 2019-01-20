package com.readyidu.test;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.facebook.stetho.Stetho;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.readyidu.robot.AppConfig;
import com.readyidu.robot.ContextCallBack;
import com.readyidu.robot.YDRobot;
import com.readyidu.robot.event.SearchXYBrainEvent;
import com.readyidu.robot.model.business.index.HotRecommendModel;
import com.readyidu.robot.model.business.music.Music;
import com.readyidu.robot.model.business.news.News;
import com.readyidu.robot.utils.log.LogUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StartAvtivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_avtivity);
        YDRobot.getInstance().setDebugMode(true);
        YDRobot.getInstance().init(this);
        YDRobot.getInstance().registerMessageType(ImageMessage.class);
        YDRobot.getInstance().registerMessageTemplate(new ImageMessageTemplate());
        YDRobot.getInstance().setContextCallBack(new ContextCallBack() {
            @Override
            public Context getContext() {
                return StartAvtivity.this;
            }
        });

        Stetho.initializeWithDefaults(this);
    }

    public void getHotRecommend(View view) {
        YDRobot.getInstance().setHotRecommendListener(new YDRobot.GetHotRecommendListener() {
            @Override
            public void getHotRecommend(HotRecommendModel hotRecommendModel) {

                LogUtils.i("hot_music:" + hotRecommendModel.getMusic());
                LogUtils.i("hot_news:" + hotRecommendModel.getNews());
                LogUtils.i("hot_weather:" + hotRecommendModel.getWeather());

                try {
                    JSONObject jsonObject = new JSONObject(hotRecommendModel.getMusic());
                    JSONArray newsArray = jsonObject.getJSONArray("data");
                    final ArrayList<Music> musics = new Gson().fromJson(newsArray.toString(), new TypeToken<List<Music>>() {
                    }.getType());

                    JSONObject jsonObject2 = new JSONObject(hotRecommendModel.getNews());
                    JSONArray newsArray2 = jsonObject2.getJSONArray("data");
                    final ArrayList<News> newes = new Gson().fromJson(newsArray2.toString(), new TypeToken<List<News>>() {
                    }.getType());

//                    YDRobot.getInstance().toBrain(AppConfig.TYPE_NEWS);
//                    YDRobot.getInstance().toBrain(AppConfig.TYPE_MUSIC);

                    YDRobot.getInstance().toMusicList(musics);
//                    YDRobot.getInstance().toNewsDetail(newes.get(0));
                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure() {
                LogUtils.i("获取热门推荐失败");
            }
        });
    }

    public void toWeather(View view) {

        YDRobot.getInstance().toBrain(AppConfig.TYPE_WEATHER);
    }

    public void toMusic(View view) {

        YDRobot.getInstance().toBrain(AppConfig.TYPE_MUSIC);
    }

    public void toNews(View view) {

        YDRobot.getInstance().toBrain(AppConfig.TYPE_VNEWS);
    }

    public void toTv(View view) {

        YDRobot.getInstance().toBrain(AppConfig.TYPE_TV);
    }

    public void toBaike(View view) {

        YDRobot.getInstance().toBrain(AppConfig.TYPE_BAIKE);
    }

    public void toJieri(View view) {

        YDRobot.getInstance().toBrain(AppConfig.TYPE_CALENDAR);
    }

    public void toMenu(View view) {

        YDRobot.getInstance().toBrain(AppConfig.TYPE_MENU);
    }

    public void tolottery(View view) {
        YDRobot.getInstance().toBrain(AppConfig.TYPE_lottery);
    }


    public void toChat(View view) {
        YDRobot.getInstance().execMessage(new SearchXYBrainEvent("元旦是几号", false));
        YDRobot.getInstance().startRobot(this);
    }


    public void robet(View view) {
        YDRobot.getInstance().toBrain(AppConfig.TYPE_robet);
    }
}
