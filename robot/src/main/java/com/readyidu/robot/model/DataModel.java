package com.readyidu.robot.model;

import com.readyidu.robot.AppConfig;
import com.readyidu.robot.model.business.news.VideoNewsMenu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * @Autour: wlq
 * @Description: 网络数据data节点下实体类
 * @Date: 2017/10/11 15:18
 * @Update: 2017/10/11 15:18
 * @UpdateRemark:
 * @Version: V1.0
 */
public class DataModel<T> implements Serializable {

    public T content;  //搜索内容

    public String contentType;

    public ArrayList<MessageModel> messages;//多类目消息

    public String question;    //搜索内容的全称

    public String type;    //搜索所属笼子分类

    public String keyword; //提取的关键字

    public int file_duration;   //音乐时长

    public String file_link;    //音乐播放地址

    public ArrayList<VideoNewsMenu> menu;   //视频新闻菜单

    public String service;

    public HashMap arg;

    public boolean isValid() {
        return isSupportType(type) &&
                (null != content || (null != messages && messages.size() > 0));
    }

    public boolean isSupportType(String type) {
        return AppConfig.TYPE_AUTO_REPLY.equals(type)
                || AppConfig.TYPE_IFLY.equals(type)
                || AppConfig.TYPE_BAIKE.equals(type)
                || AppConfig.TYPE_MULTIPLE.equals(type)
                || AppConfig.TYPE_WEATHER.equals(type)
                || AppConfig.TYPE_MUSIC.equals(type)
                || AppConfig.TYPE_MENU.equals(type)
                || AppConfig.TYPE_NEWS.equals(type)
                || AppConfig.TYPE_VNEWS.equals(type)
                || AppConfig.TYPE_FILM.equals(type)
                || AppConfig.TYPE_PHRASE.equals(type)
                || AppConfig.TYPE_POETRY.equals(type)
                || AppConfig.TYPE_TV.equals(type)
                || AppConfig.TYPE_JOKE.equals(type)
                || AppConfig.TYPE_FM.equals(type)
                || AppConfig.TYPE_lottery.equals(type)
                || AppConfig.TYPE_SHARE.equals(type);
    }
}