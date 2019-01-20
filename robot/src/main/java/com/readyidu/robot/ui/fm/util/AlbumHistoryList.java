package com.readyidu.robot.ui.fm.util;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yuzhang on 2018/6/3.
 */

public class AlbumHistoryList implements Serializable {
    private List<AlbumHistoryModel> rows;
    private int total;

    public List<AlbumHistoryModel> getRows() {
        return rows;
    }

    public void setRows(List<AlbumHistoryModel> rows) {
        this.rows = rows;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
