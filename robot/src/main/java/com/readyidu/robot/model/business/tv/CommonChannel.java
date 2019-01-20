package com.readyidu.robot.model.business.tv;

import com.readyidu.robot.utils.log.LogUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 封装返回的数据 0：央视卫视 1：地方 2：电影 3：少儿 4：电视剧
 * Created by gx on 2017/11/29.
 */
public class CommonChannel {

    private int classifyPosition;
    private int type;       //客户端渲染类型 0：央视卫视 1：地方 2：电影 3：少儿 4：电视剧
    private ArrayList<TvChannel> tvLives;  //直播&轮播
    private ArrayList<TvChannel> onDemands;   //点播

    public CommonChannel(ArrayList<TvChannel> tvChannels, ArrayList<TvChannel> onDemands, int classifyPosition, TvType tvType) {
        this.tvLives = tvChannels;
        this.onDemands = onDemands;

        type = tvType.categoryId;

        if (tvChannels != null && tvChannels.size() > 0) {
            for (TvChannel t : tvChannels) {
                t.classifyPosition = classifyPosition;
                t.type = type;
                t.kindPosition = 0;
            }
        }
        if (onDemands != null && onDemands.size() > 0) {
            for (TvChannel t : onDemands) {
                t.classifyPosition = classifyPosition;
                t.isMovie = true;
                t.type = type;
                t.kindPosition = 1;
            }
        }


        if (type == 1) {
            LinkedHashMap<String, ArrayList<TvChannel>> hashMap = new LinkedHashMap<>();
            if (tvChannels != null && tvChannels.size() > 0) {
                for (TvChannel tc : tvChannels) {
                    ArrayList<TvChannel> temp = hashMap.get(tc.g);
                    if (temp == null) {
                        temp = new ArrayList<>();
                    }
                    temp.add(tc);
                    hashMap.put(tc.g, temp);
                }
            }

            this.tvLives.clear();

            for (Object o : hashMap.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                ArrayList<TvChannel> val = (ArrayList<TvChannel>) entry.getValue();
                if (val != null && val.size() > 0) {
                    TvChannel tvChannel = val.get(0);
                    //强制把 频道数据替换为
                    TvChannel e = new TvChannel(val, tvChannel.g, tvChannel.i, tvChannel.g, type);
                    e.classifyPosition = classifyPosition;
                    e.type = this.type;
                    e.kindPosition = 0;
                    this.tvLives.add(e);
                }
            }
            for (int i = 0; i < this.tvLives.size(); i++) {
                TvChannel tvChannel = tvLives.get(i);
                tvChannel.classifyPositionSecond = i;
                if (tvChannel.tvDemands.size() > 0) {
                    for (int j = 0; j < tvChannel.tvDemands.size(); j++) {
                        tvChannel.tvDemands.get(j).classifyPositionSecond = i;
                    }
                    this.tvLives.get(i).classifyPositionSecond = i;

                }
            }
        }

        if (type == 2) {
            LinkedHashMap<Integer, TvChannel> hashMap = new LinkedHashMap<>();

            for (TvChannel tc : this.tvLives) {
                if (!hashMap.containsKey(tc.i)) {
                    hashMap.put(tc.i, tc.clone());
                }
            }

            for (TvChannel tc : this.onDemands) {
                TvChannel tvChannel = hashMap.get(tc.p);
                if (tvChannel != null) {
                    tvChannel.tvDemands.add(tc);
                } else {
                    LogUtils.e("缺少电影类型：" + tc.p);
                }
            }

            this.onDemands.clear();

            for (Object o : hashMap.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                TvChannel val = (TvChannel) entry.getValue();
                val.isMovie = true;
                if (val.tvDemands != null && val.tvDemands.size() > 0) {
                    val.g = val.tvDemands.get(0).g;
                    this.onDemands.add(val);
                }
            }

            for (int i = 0; i < this.onDemands.size(); i++) {

                TvChannel tvChannel = this.onDemands.get(i);
                tvChannel.classifyPositionSecond = i;
                tvChannel.kindPosition = 1;

                if (tvChannel.tvDemands.size() > 0) {
                    for (int j = 0; j < tvChannel.tvDemands.size(); j++) {
                        tvChannel.tvDemands.get(j).classifyPositionSecond = i;
                        tvChannel.tvDemands.get(j).kindPosition = 1;
                    }
                }
            }
        }
    }

    public int getClassifyPosition() {
        return classifyPosition;
    }

    public void setClassifyPosition(int classifyPosition) {
        this.classifyPosition = classifyPosition;
    }

    public ArrayList<TvChannel> getTvLives() {
        return tvLives;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setTvLives(ArrayList<TvChannel> tvLives) {
        this.tvLives = tvLives;
    }

    public ArrayList<TvChannel> getOnDemands() {
        return onDemands;
    }

    public void setOnDemands(ArrayList<TvChannel> onDemands) {
        this.onDemands = onDemands;
    }

}