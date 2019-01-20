package com.readyidu.robot.component.router.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 随机推荐数据
 */
public class RecommendTypeInfo implements Parcelable {

    public int resId;
    public int typeNo;
    public String type;
    public String desc;
    public int optType;//0文本查询

    public RecommendTypeInfo(int resId, int typeNo, String type, String desc, int optType) {
        this.resId = resId;
        this.typeNo = typeNo;
        this.type = type;
        this.desc = desc;
        this.optType = optType;
    }

    public RecommendTypeInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.resId);
        dest.writeInt(this.typeNo);
        dest.writeString(this.type);
        dest.writeString(this.desc);
        dest.writeInt(this.optType);
    }

    protected RecommendTypeInfo(Parcel in) {
        this.resId = in.readInt();
        this.typeNo = in.readInt();
        this.type = in.readString();
        this.desc = in.readString();
        this.optType = in.readInt();
    }

    public static final Creator<RecommendTypeInfo> CREATOR = new Creator<RecommendTypeInfo>() {
        @Override
        public RecommendTypeInfo createFromParcel(Parcel source) {
            return new RecommendTypeInfo(source);
        }

        @Override
        public RecommendTypeInfo[] newArray(int size) {
            return new RecommendTypeInfo[size];
        }
    };
}
