package com.readyidu.robot.model.business.weather;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 */
public class HeNowInfo implements Parcelable {

    public int hum;
    public String vis;
    public String pres;
    public String pcpn;
    public String fl;
    public String tmp;
    public Cond cond;
    public Wind wind;

    public boolean isValid() {
        return true;
    }

    public static class Cond implements Parcelable {

        public String txt;
        public String code;
        public String code_url;

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

        public Cond() {
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

        public Wind() {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.hum);
        dest.writeString(this.vis);
        dest.writeString(this.pres);
        dest.writeString(this.pcpn);
        dest.writeString(this.fl);
        dest.writeString(this.tmp);
        dest.writeParcelable(this.cond, flags);
        dest.writeParcelable(this.wind, flags);
    }

    public HeNowInfo() {
    }

    protected HeNowInfo(Parcel in) {
        this.hum = in.readInt();
        this.vis = in.readString();
        this.pres = in.readString();
        this.pcpn = in.readString();
        this.fl = in.readString();
        this.tmp = in.readString();
        this.cond = in.readParcelable(Cond.class.getClassLoader());
        this.wind = in.readParcelable(Wind.class.getClassLoader());
    }

    public static final Parcelable.Creator<HeNowInfo> CREATOR = new Parcelable.Creator<HeNowInfo>() {
        @Override
        public HeNowInfo createFromParcel(Parcel source) {
            return new HeNowInfo(source);
        }

        @Override
        public HeNowInfo[] newArray(int size) {
            return new HeNowInfo[size];
        }
    };
}
