package com.readyidu.robot.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by gx on 2017/10/11.
 */
public class SQLHelper extends SQLiteOpenHelper {

    private volatile static SQLHelper instance;

    public static SQLHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (SQLHelper.class) {
                if (instance == null) {
                    instance = new SQLHelper(context, "BaseMessage.db", null, 2);
                }
            }
        }
        return instance;
    }

    private SQLHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                      int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_CLASS = "Create TABLE If Not Exists BaseMessage(" +
                "id integer primary key AUTOINCREMENT,"
                + "messageType varchar(10),"
                + "isRobot integer,"
                + "messageContent text)";

        String CREATE_NEWS = "Create TABLE If Not Exists NEWS(" +
                "id integer primary key AUTOINCREMENT,"
                + "news_id varchar(10))";

        db.execSQL(CREATE_CLASS);
        db.execSQL(CREATE_NEWS);

        //4.2.3版本
        String CREATE_VIDEO_NEWS = "Create TABLE If Not Exists VideoNEWS(" +
                "id integer primary key AUTOINCREMENT,"
                + "video_news_id varchar(25), user_id varchar(25))";
        db.execSQL(CREATE_VIDEO_NEWS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            switch (newVersion) {
                case 2:
                    String CREATE_VIDEO_NEWS = "Create TABLE If Not Exists VideoNEWS(" +
                            "id integer primary key AUTOINCREMENT,"
                            + "video_news_id varchar(25), user_id varchar(25))";
                    db.execSQL(CREATE_VIDEO_NEWS);
                    break;
            }
        }
    }
}