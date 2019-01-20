package com.readyidu.robot.model;

/**
 * Created by gx on 2017/10/12.
 */
public class ExpandModel {
    private int picRes;
    private String name;
    private String sendStr;
    private String type;


    public ExpandModel(int picRes, String name, String sendStr, String type) {
        this.picRes = picRes;
        this.name = name;
        this.sendStr = sendStr;
        this.type = type;
    }

    public int getPicRes() {
        return picRes;
    }

    public String getName() {
        return name;
    }

    public String getSendStr() {
        return sendStr;
    }

    public String getType() {
        return type;
    }
}