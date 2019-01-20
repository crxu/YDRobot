package com.readyidu.robot.model.business.weather;

import android.os.Parcel;
import android.os.Parcelable;

/**
 */
public class HeAqiInfo implements Parcelable {

    public AqiCity city;

    public boolean isValid() {
        return null != city;
    }

    public static class AqiCity implements Parcelable {

        public String no2;
        public String o3;
        public String pm25;
        public String qlty;
        public String so2;
        public String aqi;
        public String pm10;
        public String co;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.no2);
            dest.writeString(this.o3);
            dest.writeString(this.pm25);
            dest.writeString(this.qlty);
            dest.writeString(this.so2);
            dest.writeString(this.aqi);
            dest.writeString(this.pm10);
            dest.writeString(this.co);
        }

        public AqiCity() {
        }

        protected AqiCity(Parcel in) {
            this.no2 = in.readString();
            this.o3 = in.readString();
            this.pm25 = in.readString();
            this.qlty = in.readString();
            this.so2 = in.readString();
            this.aqi = in.readString();
            this.pm10 = in.readString();
            this.co = in.readString();
        }

        public static final Parcelable.Creator<AqiCity> CREATOR = new Parcelable.Creator<AqiCity>() {
            @Override
            public AqiCity createFromParcel(Parcel source) {
                return new AqiCity(source);
            }

            @Override
            public AqiCity[] newArray(int size) {
                return new AqiCity[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.city, flags);
    }

    public HeAqiInfo() {
    }

    protected HeAqiInfo(Parcel in) {
        this.city = in.readParcelable(AqiCity.class.getClassLoader());
    }

    public static final Parcelable.Creator<HeAqiInfo> CREATOR = new Parcelable.Creator<HeAqiInfo>() {
        @Override
        public HeAqiInfo createFromParcel(Parcel source) {
            return new HeAqiInfo(source);
        }

        @Override
        public HeAqiInfo[] newArray(int size) {
            return new HeAqiInfo[size];
        }
    };

}
