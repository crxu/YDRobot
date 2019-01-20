package com.readyidu.robot.model.business.weather;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * @Autour: wlq
 * @Description: 天气笼子实体类
 * @Date: 2017/10/11 18:36
 * @Update: 2017/10/11 18:36
 * @UpdateRemark:
 * @Version: V1.0
 */
public class Weather implements Parcelable {

    private ArrayList<HeWeatherInfo> HeWeather5;

    public ArrayList<HeWeatherInfo> getHeWeather5() {
        return HeWeather5;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.HeWeather5);
    }

    public Weather() {
    }

    protected Weather(Parcel in) {
        this.HeWeather5 = in.createTypedArrayList(HeWeatherInfo.CREATOR);
    }

    public static final Parcelable.Creator<Weather> CREATOR = new Parcelable.Creator<Weather>() {
        @Override
        public Weather createFromParcel(Parcel source) {
            return new Weather(source);
        }

        @Override
        public Weather[] newArray(int size) {
            return new Weather[size];
        }
    };
}
