package com.readyidu.robot.model.business.weather;

import android.os.Parcel;
import android.os.Parcelable;

public class HeHourlyForecast implements Parcelable {

    public String date;
    public String pop;
    public String hum;
    public String pres;
    public String tmp;
    public Cond cond;
    public Wind wind;

    public static class Cond implements Parcelable {

        public String txt;
        public String code;
        public String code_url;

        public Cond() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.txt);
            dest.writeString(this.code);
            dest.writeString(this.code_url);
        }

        protected Cond(Parcel in) {
            this.txt = in.readString();
            this.code = in.readString();
            this.code_url = in.readString();
        }

        public static final Creator<Cond> CREATOR = new Creator<Cond>() {
            @Override
            public Cond createFromParcel(Parcel source) {
                return new Cond(source);
            }

            @Override
            public Cond[] newArray(int size) {
                return new Cond[size];
            }
        };
    }

    public static class Wind implements Parcelable {

        public String sc;
        public String spd;
        public String deg;
        public String dir;

        public Wind() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.sc);
            dest.writeString(this.spd);
            dest.writeString(this.deg);
            dest.writeString(this.dir);
        }

        protected Wind(Parcel in) {
            this.sc = in.readString();
            this.spd = in.readString();
            this.deg = in.readString();
            this.dir = in.readString();
        }

        public static final Creator<Wind> CREATOR = new Creator<Wind>() {
            @Override
            public Wind createFromParcel(Parcel source) {
                return new Wind(source);
            }

            @Override
            public Wind[] newArray(int size) {
                return new Wind[size];
            }
        };
    }

    public HeHourlyForecast() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeString(this.pop);
        dest.writeString(this.hum);
        dest.writeString(this.pres);
        dest.writeString(this.tmp);
        dest.writeParcelable(this.cond, flags);
        dest.writeParcelable(this.wind, flags);
    }

    protected HeHourlyForecast(Parcel in) {
        this.date = in.readString();
        this.pop = in.readString();
        this.hum = in.readString();
        this.pres = in.readString();
        this.tmp = in.readString();
        this.cond = in.readParcelable(Cond.class.getClassLoader());
        this.wind = in.readParcelable(Wind.class.getClassLoader());
    }

    public static final Creator<HeHourlyForecast> CREATOR = new Creator<HeHourlyForecast>() {
        @Override
        public HeHourlyForecast createFromParcel(Parcel source) {
            return new HeHourlyForecast(source);
        }

        @Override
        public HeHourlyForecast[] newArray(int size) {
            return new HeHourlyForecast[size];
        }
    };
}
