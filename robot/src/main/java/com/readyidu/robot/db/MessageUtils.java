package com.readyidu.robot.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.readyidu.robot.YDRobot;
import com.readyidu.robot.message.base.BaseMessage;
import com.readyidu.robot.message.base.Message;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by gx on 2017/10/11.
 */
public class MessageUtils {

    public static int addMessage(Message message) {
        SQLiteDatabase db = SQLHelper.getInstance(YDRobot.getInstance().getContext()).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("messageType", message.getMessageType());
        values.put("messageContent", new Gson().toJson(message.getContent()));
        long id = db.insert("BaseMessage", null, values);
        db.close();
        return (int) id;
    }

    public static void updateTextMessageStatus(Message message) {
        SQLiteDatabase db = SQLHelper.getInstance(YDRobot.getInstance().getContext()).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("messageContent", new Gson().toJson(message.getContent()));
        db.update("BaseMessage", values, "id ='" + message.getId() + "'", null);
        db.close();
    }

    public static void deleteAllMessage() {
        SQLiteDatabase db = SQLHelper.getInstance(YDRobot.getInstance().getContext()).getWritableDatabase();
        db.delete("BaseMessage", null, null);
        db.close();
    }

    public static void deleteMessage(int id) {
        SQLiteDatabase db = SQLHelper.getInstance(YDRobot.getInstance().getContext()).getWritableDatabase();
        db.delete("BaseMessage", "id=?", new String[]{id + ""});
        db.close();
    }

    public static ArrayList<Message> getMessage(int offset, int pageSize) {
        ArrayList<Message> tempMessages = new ArrayList<>();
        String SELECT_MESSAGE = "Select * From BaseMessage order by id desc limit " + offset + "," + pageSize;
        SQLiteDatabase db = SQLHelper.getInstance(YDRobot.getInstance().getContext()).getWritableDatabase();
        Cursor cursor = db.rawQuery(SELECT_MESSAGE, null);
        while (cursor.moveToNext()) {

            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String messageType = cursor.getString(cursor.getColumnIndex("messageType"));
            String messageContent = cursor.getString(cursor.getColumnIndex("messageContent"));
            Message message = new Message();
            message.setId(id);

            //解析消息体
            HashMap<String, Class<? extends BaseMessage>> messages = YDRobot.getInstance().getMessages();
            Class<? extends BaseMessage> cls = messages.get(messageType);
            BaseMessage baseMessage = new Gson().fromJson(messageContent, cls);
            if (baseMessage != null) {
                message.obtain(baseMessage, baseMessage.getMessageType());
            }

            tempMessages.add(message);
        }
        cursor.close();
        db.close();
        return tempMessages;
    }
}