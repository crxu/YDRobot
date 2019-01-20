package com.readyidu.robot.model.business.weather;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 */
public class HeBasicInfo implements Parcelable {

    public String city;
    public Update update;
    public String id;
    public String cnty;
    public String parent_city;
    public String lon;
    public String lat;

    public boolean isValid() {
        return null != update;
    }

    public static class Update implements Parcelable {

        public String loc;
        public String utc;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.loc);
            dest.writeString(this.utc);
        }

        public Update() {
        }

        protected Update(Parcel in) {
            this.loc = in.readString();
            this.utc = in.readString();
        }

        public static final Parcelable.Creator<Update> CREATOR = new Parcelable.Creator<Update>() {
            @Override
            public Update createFromParcel(Parcel source) {
                return new Update(source);
            }

            @Override
            public Update[] newArray(int size) {
                return new Update[size];
            }
        };
    }

    public HeBasicInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.city);
        dest.writeParcelable(this.update, flags);
        dest.writeString(this.id);
        dest.writeString(this.cnty);
        dest.writeString(this.parent_city);
        dest.writeString(this.lon);
        dest.writeString(this.lat);
    }

    protected HeBasicInfo(Parcel in) {
        this.city = in.readString();
        this.update = in.readParcelable(Update.class.getClassLoader());
        this.id = in.readString();
        this.cnty = in.readString();
        this.parent_city = in.readString();
        this.lon = in.readString();
        this.lat = in.readString();
    }

    public static final Creator<HeBasicInfo> CREATOR = new Creator<HeBasicInfo>() {
        @Override
        public HeBasicInfo createFromParcel(Parcel source) {
            return new HeBasicInfo(source);
        }

        @Override
        public HeBasicInfo[] newArray(int size) {
            return new HeBasicInfo[size];
        }
    };
}
