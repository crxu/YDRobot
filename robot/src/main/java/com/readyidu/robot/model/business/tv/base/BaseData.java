package com.readyidu.robot.model.business.tv.base;

import java.io.Serializable;

public class BaseData<T> implements Serializable{

    public T content;

    public String question;    //搜索内容的全称

    public String type;    //搜索所属笼子分类

    public String keyword; //提取的关键字
}
