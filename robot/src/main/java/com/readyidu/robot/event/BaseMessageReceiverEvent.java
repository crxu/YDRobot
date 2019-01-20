package com.readyidu.robot.event;

import com.readyidu.robot.model.BaseModel;

/**
 * @author wanwan
 * @date 2017/12/28
 */
public class BaseMessageReceiverEvent {
    public BaseModel message;
    private boolean isSDKOutside;
    private boolean isSearchList;
    private boolean isVideoNews;
    private String title;

    public BaseMessageReceiverEvent(BaseModel message) {
        this.message = message;
    }

    public boolean isSDKOutside() {
        return isSDKOutside;
    }

    public void setSDKOutside(boolean SDKOutside) {
        isSDKOutside = SDKOutside;
    }

    public boolean isSearchList() {
        return isSearchList;
    }

    public void setSearchList(boolean searchList) {
        isSearchList = searchList;
    }

    public boolean isVideoNews() {
        return isVideoNews;
    }

    public void setVideoNews(boolean videoNews) {
        isVideoNews = videoNews;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}