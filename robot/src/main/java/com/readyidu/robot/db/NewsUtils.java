package com.readyidu.robot.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readyidu.robot.YDRobot;
import com.readyidu.robot.utils.log.LogUtils;

/**
 * Created by gx on 2017/10/19.
 */
public class NewsUtils {

    public static int addNew(String newsId) {
        SQLiteDatabase db = SQLHelper.getInstance(YDRobot.getInstance().getContext()).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("news_id", newsId);
        long id = db.insert("NEWS", null, values);
        db.close();
        return (int) id;
    }

    public static boolean isReaded(String newsId) {
        try {
            SQLiteDatabase db = SQLHelper.getInstance(YDRobot.getInstance().getContext()).getWritableDatabase();
            Cursor cursor = db.rawQuery("select * from NEWS where news_id = '" + newsId + "'", null);
            int id = 0;
            while (cursor.moveToNext()) {
                id = cursor.getInt(cursor.getColumnIndex("id"));
            }
            cursor.close();
            db.close();
            return id > 0;
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return false;
    }
}