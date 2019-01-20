package com.readyidu.robot.event.news;

import com.readyidu.robot.model.business.news.VideoNewsDetail;

import java.util.List;

/**
 * @author Wlq
 * @description 视频新闻
 * @date 2017/12/19 上午11:35
 */
public class VideoNewsEvent {

    public static class PlayCompleteEvent {

        public boolean isComplete;

        public PlayCompleteEvent(boolean isComplete) {
            this.isComplete = isComplete;
        }
    }

    public static class PlayingEvent {

        public PlayingEvent() {}
    }

    public static class QuitFullScreenEvent {

        public QuitFullScreenEvent() {}
    }

    public static class PlayerClickPauseEvent {

        public PlayerClickPauseEvent() {}
    }

    public static class ClickFullScreenEvent {

        public ClickFullScreenEvent() {}
    }

    public static class ClickBackButtonEvent {

        public ClickBackButtonEvent() {}
    }

    public static class CancelWifiWarnEvent {

        public CancelWifiWarnEvent() {}
    }

    public static class SelectCurrentItemEvent {

        public String currentItem;

        public SelectCurrentItemEvent(String currentItem) {
            this.currentItem = currentItem;
        }
    }

    /**
     * 搜索后选中新闻分类
     */
    public static class SearchNewsSelectEvent {

        public boolean isHasResult; //是否搜索出结果，没有的话设置默认推荐的新闻

        public String searchContent;

        public String keyword;

        public String itemName;

        public List<VideoNewsDetail> newsDetails;

        public int total;

        public boolean isSearchFromVoice;

        public SearchNewsSelectEvent(boolean isHasResult, boolean isSearchFromVoice, String searchContent, String keyword,
                                     String itemName, List<VideoNewsDetail> newsDetails, int total) {
            this.isHasResult = isHasResult;
            this.isSearchFromVoice = isSearchFromVoice;
            this.searchContent = searchContent;
            this.newsDetails = newsDetails;
            this.itemName = itemName;
            this.total = total;
            this.keyword = keyword;
        }
    }
}
