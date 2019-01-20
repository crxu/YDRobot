package com.readyidu.robot.ui.fm.util;

import android.os.Parcel;
import android.os.Parcelable;

import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;

import java.util.List;

/**
 * Created by yuzhang on 2018/6/3.
 */

public class RadioScheduleModel implements Parcelable {
    private Radio radio;
    private List<Schedule> schedules;
    private int scheduleIndex;

    public Radio getRadio() {
        return radio;
    }

    public void setRadio(Radio radio) {
        this.radio = radio;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules;
    }

    public int getScheduleIndex() {
        return scheduleIndex;
    }

    public void setScheduleIndex(int scheduleIndex) {
        this.scheduleIndex = scheduleIndex;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.radio, 0);
        dest.writeTypedList(schedules);
        dest.writeInt(this.scheduleIndex);
    }

    public RadioScheduleModel() {
    }

    protected RadioScheduleModel(Parcel in) {
        this.radio = in.readParcelable(Radio.class.getClassLoader());
        this.schedules = in.createTypedArrayList(Schedule.CREATOR);
        this.scheduleIndex = in.readInt();
    }

    public static final Parcelable.Creator<RadioScheduleModel> CREATOR = new Parcelable.Creator<RadioScheduleModel>() {
        public RadioScheduleModel createFromParcel(Parcel source) {
            return new RadioScheduleModel(source);
        }

        public RadioScheduleModel[] newArray(int size) {
            return new RadioScheduleModel[size];
        }
    };
}
