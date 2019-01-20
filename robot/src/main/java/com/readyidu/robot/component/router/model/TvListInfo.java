package com.readyidu.robot.component.router.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Tv界面映射信息列表
 */
public class TvListInfo implements Parcelable {

    public int code;
    public String message;
    public List<TvInfo> data = new ArrayList<>();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.code);
        dest.writeString(this.message);
        dest.writeTypedList(this.data);
    }

    public TvListInfo() {
    }

    protected TvListInfo(Parcel in) {
        this.code = in.readInt();
        this.message = in.readString();
        this.data = in.createTypedArrayList(TvInfo.CREATOR);
    }

    public static final Parcelable.Creator<TvListInfo> CREATOR = new Parcelable.Creator<TvListInfo>() {
        @Override
        public TvListInfo createFromParcel(Parcel source) {
            return new TvListInfo(source);
        }

        @Override
        public TvListInfo[] newArray(int size) {
            return new TvListInfo[size];
        }
    };
}
