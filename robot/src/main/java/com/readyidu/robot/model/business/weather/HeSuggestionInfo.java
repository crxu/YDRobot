package com.readyidu.robot.model.business.weather;

import android.os.Parcel;
import android.os.Parcelable;

import com.readyidu.robot.R;

/**
 * 生活建议
 */
public class HeSuggestionInfo implements Parcelable {

    public Suggestion cw;//洗车指数
    public Suggestion sport;//运动指数
    public Suggestion drsg;//穿衣指数
    public Suggestion trav;//旅游指数
    public Suggestion flu;//感冒指数
    public Suggestion uv;//紫外线指数
    public Suggestion air;
    public Suggestion comf;//舒适度指数

    public boolean isValid() {
        return true;
    }

    public int size() {
        return 6;
    }

    public Suggestion get(int position) {
        switch (position) {
            case 0:
                cw.sug = R.drawable.ic_sug_xc;
                cw.type = "洗车";
                return cw;

            case 1:
                sport.sug = R.drawable.ic_sug_yd;
                sport.type = "运动";
                return sport;

            case 2:
                drsg.sug = R.drawable.ic_sug_cy;
                drsg.type = "穿衣";
                return drsg;

            case 3:
                trav.sug = R.drawable.ic_sug_lx;
                trav.type = "旅行";
                return trav;

            case 4:
                flu.sug = R.drawable.ic_sug_gm;
                flu.type = "感冒";
                return flu;

            case 5:
                uv.sug = R.drawable.ic_sug_zwx;
                uv.type = "紫外线";
                return uv;

            default:
                cw.sug = R.drawable.ic_sug_xc;
                cw.type = "洗车";
                return cw;
        }
    }

    public static class Suggestion implements Parcelable {

        public int sug;
        public String txt;
        public String brf;
        public String type;

        public Suggestion() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.sug);
            dest.writeString(this.txt);
            dest.writeString(this.brf);
        }

        protected Suggestion(Parcel in) {
            this.sug = in.readInt();
            this.txt = in.readString();
            this.brf = in.readString();
        }

        public static final Creator<Suggestion> CREATOR = new Creator<Suggestion>() {
            @Override
            public Suggestion createFromParcel(Parcel source) {
                return new Suggestion(source);
            }

            @Override
            public Suggestion[] newArray(int size) {
                return new Suggestion[size];
            }
        };
    }

    public HeSuggestionInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.cw, flags);
        dest.writeParcelable(this.sport, flags);
        dest.writeParcelable(this.drsg, flags);
        dest.writeParcelable(this.trav, flags);
        dest.writeParcelable(this.flu, flags);
        dest.writeParcelable(this.uv, flags);
        dest.writeParcelable(this.air, flags);
        dest.writeParcelable(this.comf, flags);
    }

    protected HeSuggestionInfo(Parcel in) {
        this.cw = in.readParcelable(Suggestion.class.getClassLoader());
        this.sport = in.readParcelable(Suggestion.class.getClassLoader());
        this.drsg = in.readParcelable(Suggestion.class.getClassLoader());
        this.trav = in.readParcelable(Suggestion.class.getClassLoader());
        this.flu = in.readParcelable(Suggestion.class.getClassLoader());
        this.uv = in.readParcelable(Suggestion.class.getClassLoader());
        this.air = in.readParcelable(Suggestion.class.getClassLoader());
        this.comf = in.readParcelable(Suggestion.class.getClassLoader());
    }

    public static final Creator<HeSuggestionInfo> CREATOR = new Creator<HeSuggestionInfo>() {
        @Override
        public HeSuggestionInfo createFromParcel(Parcel source) {
            return new HeSuggestionInfo(source);
        }

        @Override
        public HeSuggestionInfo[] newArray(int size) {
            return new HeSuggestionInfo[size];
        }
    };
}
