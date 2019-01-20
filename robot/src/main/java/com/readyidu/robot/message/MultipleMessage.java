package com.readyidu.robot.message;

import android.os.Parcel;

import com.readyidu.robot.message.base.BaseMessage;
import com.readyidu.robot.message.base.MessageTag;
import com.readyidu.robot.model.MessageModel;

import java.util.ArrayList;

/**
 * Created by gx on 2017/10/13.
 */
@MessageTag(value = "MultipleMessage")
public class MultipleMessage extends BaseMessage {

    private ArrayList<MessageModel> messageModels;
    private String content;

    public ArrayList<MessageModel> getMessageModels() {
        return messageModels;
    }

    public void setMessageModels(ArrayList<MessageModel> messageModels) {
        this.messageModels = messageModels;
    }

    public MultipleMessage() {
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeList(this.messageModels);
        dest.writeString(this.content);
    }

    protected MultipleMessage(Parcel in) {
        super(in);
        this.messageModels = new ArrayList<>();
        in.readList(this.messageModels, MessageModel.class.getClassLoader());
        this.content = in.readString();
    }

    public static final Creator<MultipleMessage> CREATOR = new Creator<MultipleMessage>() {
        @Override
        public MultipleMessage createFromParcel(Parcel source) {
            return new MultipleMessage(source);
        }

        @Override
        public MultipleMessage[] newArray(int size) {
            return new MultipleMessage[size];
        }
    };
}