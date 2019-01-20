package com.readyidu.robot.model.business.tv;

import java.util.ArrayList;
import java.util.List;

/**
 * @Autour: wlq
 * @Description: 直播&轮播数据
 * @Date: 2017/10/18 16:18
 * @Update: 2017/10/18 16:18
 * @UpdateRemark:
 * @Version: V1.0
 */
public class TvChannel extends BaseTvChannel {

    public int type;    //客户端渲染类型  -2 自定义源  -1 电视直播（没有授权） 0：央视卫视 1：地方 2：电影 3：少儿 4：电视剧
    public boolean isMovie; //是否为点播类型：true是；false否
    public int classifyPosition;
    public int kindPosition;
    public int classifyPositionSecond;
    public ArrayList<ProgrammeModel> programmeModels = new ArrayList<>();

    public ArrayList<TvChannel> tvDemands = new ArrayList<>();//地方台、电源点播点播专用

    /**
     * 新版数据tvChannel.classifyPosition
     */
    public int i;    //频道ID&点播ID

    public String n;    //当前节目表

    public List<TvSourceDetail> o;  //播放源

    public int p;   //父ID

    public int t;   //类型ID

    public String g;//	二级类目分类	string

    public TvChannel(ArrayList<TvChannel> tvDemands, String c, int i, String g, int type) {
        this.tvDemands = tvDemands;
        super.c = c;
        this.i = i;
        this.g = g;
        this.type = type;
    }

    public TvChannel() {
    }

    @Override
    public TvChannel clone() {
        TvChannel stu = null;
        try {
            stu = (TvChannel) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return stu;
    }

    @Override
    public String toString() {
        return "TvChannel{" +
                "type=" + type +
                ", isMovie=" + isMovie +
                ", classifyPosition=" + classifyPosition +
                ", kindPosition=" + kindPosition +
                ", classifyPositionSecond=" + classifyPositionSecond +
                ", programmeModels=" + programmeModels +
                ", tvDemands=" + tvDemands +
                ", c='" + super.c + '\'' +
                ", i=" + i +
                ", n='" + n + '\'' +
                ", o=" + o +
                ", p=" + p +
                ", t=" + t +
                ", g='" + g + '\'' +
                '}';
    }

    private boolean isSelect;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public void toggleSelece() {
        isSelect = !isSelect;
    }
}