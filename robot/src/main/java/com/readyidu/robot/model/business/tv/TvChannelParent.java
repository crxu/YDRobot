package com.readyidu.robot.model.business.tv;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @Autour: wlq
 * @Description: 频道列表父容器
 * @Date: 2017/10/18 16:18
 * @Update: 2017/10/18 16:18
 * @UpdateRemark:
 * @Version: V1.0
 */
public class TvChannelParent implements Serializable {

    public ArrayList<TvChannel> channels;    //所有频道

    public ArrayList<TvChannel> hotChannels; //热门频道

    public ArrayList<TvChannel> movieList;   //电影
}
