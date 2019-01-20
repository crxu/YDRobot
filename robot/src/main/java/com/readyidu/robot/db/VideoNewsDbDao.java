package com.readyidu.robot.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readyidu.robot.AppConfig;
import com.readyidu.robot.YDRobot;
import com.readyidu.robot.utils.log.LogUtils;

/**
 * @author Wlq
 * @description 视频新闻
 * @date 2017/12/19 下午4:52
 */
public class VideoNewsDbDao {

    /**
     * 加入一条已观看
     * @param newsId
     * @return
     */
    public static int addAlreadyLookVideoNews(String newsId) {
        SQLiteDatabase db = SQLHelper.getInstance(YDRobot.getInstance().getContext()).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("video_news_id", newsId);
        contentValues.put("user_id", AppConfig.APP_USERID);
        long id = db.insertOrThrow("VideoNEWS", null, contentValues);
        db.close();
        return (int) id;
    }

    /**
     * 判断视频是否已观看
     * @param newsId
     * @return
     */
    public static boolean isVideoLooked(String newsId, String userId) {
        try {
            SQLiteDatabase db = SQLHelper.getInstance(YDRobot.getInstance().getContext()).getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from VideoNEWS where video_news_id = '"
                    + newsId + "'", null);
            int id = 0;
            String user_Id = "";
            while (cursor.moveToNext()) {
                id = cursor.getInt(cursor.getColumnIndex("id"));
                user_Id = cursor.getString(cursor.getColumnIndex("user_id"));
            }
            cursor.close();
            db.close();
            return id > 0 && userId.equals(user_Id);
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return false;
    }
}
