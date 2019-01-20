package com.readyidu.robot.ui.fm.util;

/**
 * Created by yuzhang on 2018/6/3.
 */

public class AlbumHistoryModel {
    private String albumOrColumnOrRadioJson;
    private long contentId;
    private long histRecdId;

    public String getAlbumOrColumnOrRadioJson() {
        return albumOrColumnOrRadioJson;
    }

    public void setAlbumOrColumnOrRadioJson(String albumOrColumnOrRadioJson) {
        this.albumOrColumnOrRadioJson = albumOrColumnOrRadioJson;
    }

    public long getContentId() {
        return contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public long getHistRecdId() {
        return histRecdId;
    }

    public void setHistRecdId(long histRecdId) {
        this.histRecdId = histRecdId;
    }
}

