package com.readyidu.test;

import android.os.Parcel;

import com.readyidu.robot.message.base.BaseMessage;
import com.readyidu.robot.message.base.MessageTag;

/**
 * Created by gx on 2017/10/11.
 */
@MessageTag(value = "ImageMessage")
public class ImageMessage extends BaseMessage {

    private String url;

    public ImageMessage(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.url);
    }

    protected ImageMessage(Parcel in) {
        super(in);
        this.url = in.readString();
    }

    public static final Creator<ImageMessage> CREATOR = new Creator<ImageMessage>() {
        @Override
        public ImageMessage createFromParcel(Parcel source) {
            return new ImageMessage(source);
        }

        @Override
        public ImageMessage[] newArray(int size) {
            return new ImageMessage[size];
        }
    };
}