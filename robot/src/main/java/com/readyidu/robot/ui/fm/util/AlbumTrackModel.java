package com.readyidu.robot.ui.fm.util;

import android.os.Parcel;
import android.os.Parcelable;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * Created by yuzhang on 2018/6/2.
 */

public class AlbumTrackModel implements Parcelable {

    private List<Track> curTrackList;
    private long trackId;
    private String trackTitle;
    private String playCount;
    private String trackCount;
    private String updatedAt;
    private String cover;
    private String albumTitle;
    private String albumIntro;
    private int steps;
    private String albumId;
    private String sort;
    private int selectStepsPage;
    private boolean selectSteps;

    public List<Track> getCurTrackList() {
        return curTrackList;
    }

    public void setCurTrackList(List<Track> curTrackList) {
        this.curTrackList = curTrackList;
    }

    public long getTrackId() {
        return trackId;
    }

    public void setTrackId(long trackId) {
        this.trackId = trackId;
    }

    public String getTrackTitle() {
        return trackTitle;
    }

    public void setTrackTitle(String trackTitle) {
        this.trackTitle = trackTitle;
    }

    public String getPlayCount() {
        return playCount;
    }

    public void setPlayCount(String playCount) {
        this.playCount = playCount;
    }

    public String getTrackCount() {
        return trackCount;
    }

    public void setTrackCount(String trackCount) {
        this.trackCount = trackCount;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public String getAlbumIntro() {
        return albumIntro;
    }

    public void setAlbumIntro(String albumIntro) {
        this.albumIntro = albumIntro;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public int getSelectStepsPage() {
        return selectStepsPage;
    }

    public void setSelectStepsPage(int selectStepsPage) {
        this.selectStepsPage = selectStepsPage;
    }

    public boolean isSelectSteps() {
        return selectSteps;
    }

    public void setSelectSteps(boolean selectSteps) {
        this.selectSteps = selectSteps;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(curTrackList);
        dest.writeLong(this.trackId);
        dest.writeString(this.trackTitle);
        dest.writeString(this.playCount);
        dest.writeString(this.trackCount);
        dest.writeString(this.updatedAt);
        dest.writeString(this.cover);
        dest.writeString(this.albumTitle);
        dest.writeString(this.albumIntro);
        dest.writeInt(this.steps);
        dest.writeString(this.albumId);
        dest.writeString(this.sort);
        dest.writeInt(this.selectStepsPage);
        dest.writeByte(selectSteps ? (byte) 1 : (byte) 0);
    }

    public AlbumTrackModel() {
    }

    protected AlbumTrackModel(Parcel in) {
        this.curTrackList = in.createTypedArrayList(Track.CREATOR);
        this.trackId = in.readLong();
        this.trackTitle = in.readString();
        this.playCount = in.readString();
        this.trackCount = in.readString();
        this.updatedAt = in.readString();
        this.cover = in.readString();
        this.albumTitle = in.readString();
        this.albumIntro = in.readString();
        this.steps = in.readInt();
        this.albumId = in.readString();
        this.sort = in.readString();
        this.selectStepsPage = in.readInt();
        this.selectSteps = in.readByte() != 0;
    }

    public static final Creator<AlbumTrackModel> CREATOR = new Creator<AlbumTrackModel>() {
        public AlbumTrackModel createFromParcel(Parcel source) {
            return new AlbumTrackModel(source);
        }

        public AlbumTrackModel[] newArray(int size) {
            return new AlbumTrackModel[size];
        }
    };
}
