package com.readyidu.robot.model.business.tv;

/**
 * Created by gx on 2017/12/1.
 */
public class ProgrammeModel {
    private String channelName;//string
    private String showTime;//string
    private long stamp;

    public String getChannelName() {
        return channelName;
    }

    public String getShowTime() {
        return showTime;
    }

    public long getShowTimeMillis() {
        return stamp;
    }
}