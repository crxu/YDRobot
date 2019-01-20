package com.readyidu.robot.message.base;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gx on 2017/10/11.
 */
public class BaseMessage implements Parcelable {
    private boolean isRobot;
    protected int id;
    private long messageTime;
    private boolean showTime = true;
    private int sendFlag ;//默认为发送中 0 、 发送失败 -1  发送成功 1

    public boolean isRobot() {
        return isRobot;
    }

    public void setRobot(boolean robot) {
        isRobot = robot;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public boolean isShowTime() {
        return showTime;
    }

    public void setShowTime(boolean showTime) {
        this.showTime = showTime;
    }

    public int getSendFlag() {
        return sendFlag;
    }

    public void setSendFlag(int sendFlag) {
        this.sendFlag = sendFlag;
    }

    public String getMessageType() {
        Class<? extends BaseMessage> aClass = getClass();
        boolean annotationPresent = aClass.isAnnotationPresent(MessageTag.class);
        if (annotationPresent) {
            MessageTag annotation = aClass.getAnnotation(MessageTag.class);
            return annotation.value();
        }
        return "";
    }

    public BaseMessage() {
        this.messageTime = System.currentTimeMillis();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isRobot ? (byte) 1 : (byte) 0);
        dest.writeInt(this.id);
        dest.writeLong(this.messageTime);
        dest.writeByte(this.showTime ? (byte) 1 : (byte) 0);
        dest.writeInt(this.sendFlag);
    }

    protected BaseMessage(Parcel in) {
        this.isRobot = in.readByte() != 0;
        this.id = in.readInt();
        this.messageTime = in.readLong();
        this.showTime = in.readByte() != 0;
        this.sendFlag = in.readInt();
    }

}