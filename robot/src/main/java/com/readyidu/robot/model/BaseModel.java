package com.readyidu.robot.model;

import java.io.Serializable;

/**
 * Created by gx on 2017/10/11.
 */
public class BaseModel implements Serializable{

    public int errorCode;

    public String errorMessage;

    public DataModel data;

    public boolean isSuccess() {
        return errorCode == 200;
    }
}