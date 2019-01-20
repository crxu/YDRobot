package com.readyidu.robot.model.business.weather;

import android.os.Parcel;
import android.os.Parcelable;

/**

 */
public class HeDailyForecast implements Parcelable {

    public String date;
    public String pop;
    public String hum;
    public String uv;
    public String vis;
    public String pres;
    public String pcpn;
    public Tmp tmp;
    public Astro astro;//天文数值
    public Cond cond;
    public Wind wind;

    public static class Tmp implements Parcelable {

        public String min;
        public String max;

        public Tmp() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.min);
            dest.writeString(this.max);
        }

        protected Tmp(Parcel in) {
            this.min = in.readString();
            this.max = in.readString();
        }

        public static final Creator<Tmp> CREATOR = new Creator<Tmp>() {
            @Override
            public Tmp createFromParcel(Parcel source) {
                return new Tmp(source);
            }

            @Override
            public Tmp[] newArray(int size) {
                return new Tmp[size];
            }
        };
    }

    public static class Astro implements Parcelable {

        public String ss;//月升时间
        public String mr;//月落时间
        public String ms; //日出时间
        public String sr; //日落时间

        public Astro() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.ss);
            dest.writeString(this.mr);
            dest.writeString(this.ms);
            dest.writeString(this.sr);
        }

        protected Astro(Parcel in) {
            this.ss = in.readString();
            this.mr = in.readString();
            this.ms = in.readString();
            this.sr = in.readString();
        }

        public static final Creator<Astro> CREATOR = new Creator<Astro>() {
            @Override
            public Astro createFromParcel(Parcel source) {
                return new Astro(source);
            }

            @Override
            public Astro[] newArray(int size) {
                return new Astro[size];
            }
        };
    }

    public static class Cond implements Parcelable {

        public String txt_n;
        public String code_n;
        public String code_d;
        public String code_n_url;
        public String code_d_url;
        public String txt_d;

        public Cond() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.txt_n);
            dest.writeString(this.code_n);
            dest.writeString(this.code_d);
            dest.writeString(this.code_n_url);
            dest.writeString(this.code_d_url);
            dest.writeString(this.txt_d);
        }

        protected Cond(Parcel in) {
            this.txt_n = in.readString();
            this.code_n = in.readString();
            this.code_d = in.readString();
            this.code_n_url = in.readString();
            this.code_d_url = in.readString();
            this.txt_d = in.readString();
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

    public HeDailyForecast() {
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
        dest.writeString(this.uv);
        dest.writeString(this.vis);
        dest.writeString(this.pres);
        dest.writeString(this.pcpn);
        dest.writeParcelable(this.tmp, flags);
        dest.writeParcelable(this.astro, flags);
        dest.writeParcelable(this.cond, flags);
        dest.writeParcelable(this.wind, flags);
    }

    protected HeDailyForecast(Parcel in) {
        this.date = in.readString();
        this.pop = in.readString();
        this.hum = in.readString();
        this.uv = in.readString();
        this.vis = in.readString();
        this.pres = in.readString();
        this.pcpn = in.readString();
        this.tmp = in.readParcelable(Tmp.class.getClassLoader());
        this.astro = in.readParcelable(Astro.class.getClassLoader());
        this.cond = in.readParcelable(Cond.class.getClassLoader());
        this.wind = in.readParcelable(Wind.class.getClassLoader());
    }

    public static final Creator<HeDailyForecast> CREATOR = new Creator<HeDailyForecast>() {
        @Override
        public HeDailyForecast createFromParcel(Parcel source) {
            return new HeDailyForecast(source);
        }

        @Override
        public HeDailyForecast[] newArray(int size) {
            return new HeDailyForecast[size];
        }
    };
}
