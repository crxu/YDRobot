package com.readyidu.robot.model.business.tv;

import java.io.Serializable;

/**
 * 电视播放源详情
 */
public class TvSourceDetail implements Serializable {

    public double responseTime;

    public String source;

    /**
     * 新版数据
     */
    public int r;    //分辨率(1280)高清

    public int f;    //是否是公司的源 0:是1:不是

    public int i;    //	源ID

    public int m;    //响应时间(0-2)极速(2-5)流畅(5+)一般
}
