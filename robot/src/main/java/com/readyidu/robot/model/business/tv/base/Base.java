package com.readyidu.robot.model.business.tv.base;

import java.io.Serializable;

public class Base<T> implements Serializable {

    public int errorCode;

    public String errorMessage;

    public T data;
}
