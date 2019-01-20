package com.readyidu.robot.message.base;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gx on 2017/10/18.
 */
public class Message implements Parcelable {
    private boolean isRobot;
    private BaseMessage content;
    protected String messageType;
    protected int id;

    public void obtain(BaseMessage content, String messageType) {
        this.content = content;
        this.messageType = messageType;
    }

    public boolean isRobot() {
        return isRobot;
    }

    public void setRobot(boolean robot) {
        isRobot = robot;
    }

    public BaseMessage getContent() {
        return content;
    }

    public void setContent(BaseMessage content) {
        this.content = content;
    }

    public String getMessageType() {
        return messageType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Message() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isRobot ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.content, flags);
        dest.writeString(this.messageType);
        dest.writeInt(this.id);
    }

    protected Message(Parcel in) {
        this.isRobot = in.readByte() != 0;
        this.content = in.readParcelable(BaseMessage.class.getClassLoader());
        this.messageType = in.readString();
        this.id = in.readInt();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
}