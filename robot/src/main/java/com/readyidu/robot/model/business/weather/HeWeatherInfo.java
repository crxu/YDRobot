package com.readyidu.robot.model.business.weather;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * 和风天气
 */
public class HeWeatherInfo implements Parcelable {

    public HeNowInfo now;
    public HeSuggestionInfo suggestion;
    public HeAqiInfo aqi;
    public HeBasicInfo basic;
    public List<HeDailyForecast> daily_forecast = new ArrayList<>();
    public List<HeHourlyForecast> hourly_forecast = new ArrayList<>();
    public String status;//ok

    public boolean isValid() {
        return "ok".equals(status)
                && null != now && now.isValid()
                && null != suggestion && suggestion.isValid()
                && null != aqi && aqi.isValid()
                && null != basic && basic.isValid()
                && null != daily_forecast && daily_forecast.size() > 0
                && null != hourly_forecast && hourly_forecast.size() > 0;
    }

    public HeWeatherInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.now, flags);
        dest.writeParcelable(this.suggestion, flags);
        dest.writeParcelable(this.aqi, flags);
        dest.writeParcelable(this.basic, flags);
        dest.writeTypedList(this.daily_forecast);
        dest.writeTypedList(this.hourly_forecast);
        dest.writeString(this.status);
    }

    protected HeWeatherInfo(Parcel in) {
        this.now = in.readParcelable(HeNowInfo.class.getClassLoader());
        this.suggestion = in.readParcelable(HeSuggestionInfo.class.getClassLoader());
        this.aqi = in.readParcelable(HeAqiInfo.class.getClassLoader());
        this.basic = in.readParcelable(HeBasicInfo.class.getClassLoader());
        this.daily_forecast = in.createTypedArrayList(HeDailyForecast.CREATOR);
        this.hourly_forecast = in.createTypedArrayList(HeHourlyForecast.CREATOR);
        this.status = in.readString();
    }

    public static final Creator<HeWeatherInfo> CREATOR = new Creator<HeWeatherInfo>() {
        @Override
        public HeWeatherInfo createFromParcel(Parcel source) {
            return new HeWeatherInfo(source);
        }

        @Override
        public HeWeatherInfo[] newArray(int size) {
            return new HeWeatherInfo[size];
        }
    };
}
