package com.readyidu.robot.event.news;

import com.readyidu.robot.model.business.news.News;

import java.util.List;

/**
 * Created by gx on 2017/10/16.
 */
public class NewsEvent {
    public List<News> mNews;
    public String mSearchKey;
    public String mKeyword;
    public int total;

    public NewsEvent(List<News> musics, String searchKey, String keyword) {
        this.mNews = musics;
        this.mSearchKey = searchKey;
        this.mKeyword = keyword;
    }

    public NewsEvent(List<News> mNews, String mSearchKey, String mKeyword, int total) {
        this.mNews = mNews;
        this.mSearchKey = mSearchKey;
        this.mKeyword = mKeyword;
        this.total = total;
    }
}