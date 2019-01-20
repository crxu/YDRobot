package com.readyidu.robot.model.business.news;

import java.io.Serializable;

/**
 * @author Wlq
 * @description 视频新闻分类
 * @date 2017/12/15 上午10:33
 */
public class VideoNewsMenu implements Serializable {

    public int disable;

    public int id;

    public String menuName;

    public String menuType;

    public boolean selected;

    public int sort;
}
