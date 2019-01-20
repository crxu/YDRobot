package com.readyidu.robot.model.business.tv;

import java.io.Serializable;

/**
 * @Autour: wlq
 * @Description: 频道地区分布
 * @Date: 2017/10/18 16:15
 * @Update: 2017/10/18 16:15
 * @UpdateRemark:
 * @Version:
 */
public class TvType implements Serializable {

    public int id;

    public String type;
    public int categoryId; //客户端渲染类型 -2 自定义源  -1 电视直播（没有授权） 0：央视卫视 1：地方 2：电影 3：少儿 4：电视剧

    public TvType(int id, String type) {
        this.id = id;
        this.type = type;
    }
}