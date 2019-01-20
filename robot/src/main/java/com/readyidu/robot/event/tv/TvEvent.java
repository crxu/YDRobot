package com.readyidu.robot.event.tv;

import com.readyidu.robot.model.business.tv.TvChannel;
import com.readyidu.robot.model.business.tv.TvSourceDetail;
import com.readyidu.robot.ui.tv.utils.TvDataTransform;

/**
 * @Autour: wlq
 * @Description: 电视直播相关事件
 * @Date: 2017/10/22 12:00
 * @Update: 2017/10/22 12:00
 * @UpdateRemark: 电视直播相关事件
 * @Version: V1.0
 */
public class TvEvent {

    /**
     * 切换源
     */
    public static class ChangeSourceEvent {

        public TvSourceDetail tvSourceDetail;//为null的时候隐藏选择源列表

        public ChangeSourceEvent(TvSourceDetail tvSourceDetail) {
            this.tvSourceDetail = tvSourceDetail;
        }
    }

    /**
     * 查询相关节目
     */
    public static class SearchTvEvent {

        public TvChannel tv;

        public SearchTvEvent(TvChannel tv) {
            this.tv = tv;
        }
    }

    /**
     * 设置播放源
     */
    public static class PlayEvent {

        public boolean isInit;  //是否为首次加载数据

        public TvChannel tvChannel;

        public PlayEvent(TvChannel tvChannel) {
            this.tvChannel = tvChannel;
        }

        public PlayEvent(TvChannel tvChannel, boolean isInit) {
            this.tvChannel = tvChannel;
            this.isInit = isInit;
        }
    }

    /**
     * 点播类型，查看更多
     */
    public static class SeeMoreEvent {

        public int type;    //0 电视点播；1 电影点播;2 地方台列表

        public int count;

        public int classifyPos;

        public TvChannel tvChannel;

        public SeeMoreEvent(TvChannel tvChannel, int classifyPos) {
            this.tvChannel = tvChannel;
            this.classifyPos = classifyPos;
        }
    }

    /**
     * 切换视频类目，刷新节目数据
     */
    public static class ChannelChangedEvent {
        public int index;
        public boolean status;//0  获取本地    1  网络请求
        public int kindIndex;

        public boolean requestSuccess;

        public ChannelChangedEvent(int index) {
            this.index = index;
            kindIndex = TvDataTransform.getKindPosition();
        }

        public ChannelChangedEvent(int index, boolean status,boolean requestSuccess) {
            this.index = index;
            this.status = status;
            this.requestSuccess = requestSuccess;
        }

        public ChannelChangedEvent(int index, int kindIndex) {
            this.index = index;
            this.kindIndex = kindIndex;
        }
    }

    public static class ChangeProgromme {
        public TvChannel tvChannel;

        public ChangeProgromme(TvChannel tvChannel) {
            this.tvChannel = tvChannel;
        }
    }

    public static class CustomerSourceChange {

    }

    public static class CustomerSourceNameChange {

    }

    public static class BindSuccessChange {

    }
}
