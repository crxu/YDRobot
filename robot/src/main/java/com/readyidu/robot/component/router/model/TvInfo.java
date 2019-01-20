package com.readyidu.robot.component.router.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

/**
 * Tv界面映射信息
 */
public class TvInfo implements Parcelable {

    public String key;//语音结果
    public String value;//对应的电视台

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.value);
    }

    public TvInfo() {
    }

    protected TvInfo(Parcel in) {
        this.key = in.readString();
        this.value = in.readString();
    }

    public static final Parcelable.Creator<TvInfo> CREATOR = new Parcelable.Creator<TvInfo>() {
        @Override
        public TvInfo createFromParcel(Parcel source) {
            return new TvInfo(source);
        }

        @Override
        public TvInfo[] newArray(int size) {
            return new TvInfo[size];
        }
    };
}
