package com.readyidu.robot.ui.fm.util;

import java.io.Serializable;

/**
 * Created by yuzhang on 2018/6/3.
 */

public class AlbumHistoryBase implements Serializable {
    private AlbumHistoryList data;
    private int errorCode;
    private String errorMessage;

    public AlbumHistoryList getData() {
        return data;
    }

    public void setData(AlbumHistoryList data) {
        this.data = data;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
