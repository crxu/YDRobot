package com.readyidu.api.response;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jackshao on 2018/6/11.
 */

public class LotteryMenu implements Parcelable {
  private String name;
  private int type;
  private boolean isLeft;

  public LotteryMenu(String name, int type, boolean isLeft){
    this.name = name;
    this.type = type;
    this.isLeft = isLeft;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public boolean isLeft() {
    return isLeft;
  }

  public void setLeft(boolean left) {
    isLeft = left;
  }


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.name);
    dest.writeInt(this.type);
    dest.writeByte(this.isLeft ? (byte) 1 : (byte) 0);
  }

  protected LotteryMenu(Parcel in) {
    this.name = in.readString();
    this.type = in.readInt();
    this.isLeft = in.readByte() != 0;
  }

  public static final Creator<LotteryMenu> CREATOR = new Creator<LotteryMenu>() {
    @Override
    public LotteryMenu createFromParcel(Parcel source) {
      return new LotteryMenu(source);
    }

    @Override
    public LotteryMenu[] newArray(int size) {
      return new LotteryMenu[size];
    }
  };
}
