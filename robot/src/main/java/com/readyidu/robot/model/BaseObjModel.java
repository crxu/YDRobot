package com.readyidu.robot.model;

import java.io.Serializable;

/**
 * @Autour: wlq
 * @Description: 基类
 * @Date: 2017/10/18 16:13
 * @Update: 2017/10/18 16:13
 * @UpdateRemark:
 * @Version: V1.0
*/
public class BaseObjModel<T> implements Serializable {

    public int code;

    public String message;

    public T data;
}
