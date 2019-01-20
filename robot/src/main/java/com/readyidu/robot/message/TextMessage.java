package com.readyidu.robot.message;

import android.os.Parcel;

import com.readyidu.robot.message.base.BaseMessage;
import com.readyidu.robot.message.base.MessageTag;

/**
 * Created by gx on 2017/10/11.
 */
@MessageTag(value = "TextMessage")
public class TextMessage extends BaseMessage {

    private String text;

    public TextMessage(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.text);
    }

    protected TextMessage(Parcel in) {
        super(in);
        this.text = in.readString();
    }

    public static final Creator<TextMessage> CREATOR = new Creator<TextMessage>() {
        @Override
        public TextMessage createFromParcel(Parcel source) {
            return new TextMessage(source);
        }

        @Override
        public TextMessage[] newArray(int size) {
            return new TextMessage[size];
        }
    };
}