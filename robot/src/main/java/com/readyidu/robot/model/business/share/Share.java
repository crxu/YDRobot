package com.readyidu.robot.model.business.share;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by yuzhang on 2018/4/18.
 */

public class Share implements Parcelable {
    private String currentPrice;
    private String source;
    private String riseValue;
    private String openingPrice;
    private String riseRate;
    private String stockCode;
    private String url;
    private String mbmChartUrl;
    private String lowPrice;
    private String highPrice;
    private String name;
    private String closingPrice;
    private String updateDateTime;
    private List<ShareDetail> detail;

    public String getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(String currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getRiseValue() {
        return riseValue;
    }

    public void setRiseValue(String riseValue) {
        this.riseValue = riseValue;
    }

    public String getOpeningPrice() {
        return openingPrice;
    }

    public void setOpeningPrice(String openingPrice) {
        this.openingPrice = openingPrice;
    }

    public String getRiseRate() {
        return riseRate;
    }

    public void setRiseRate(String riseRate) {
        this.riseRate = riseRate;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMbmChartUrl() {
        return mbmChartUrl;
    }

    public void setMbmChartUrl(String mbmChartUrl) {
        this.mbmChartUrl = mbmChartUrl;
    }

    public String getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(String lowPrice) {
        this.lowPrice = lowPrice;
    }

    public String getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(String highPrice) {
        this.highPrice = highPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClosingPrice() {
        return closingPrice;
    }

    public void setClosingPrice(String closingPrice) {
        this.closingPrice = closingPrice;
    }

    public List<ShareDetail> getDetail() {
        return detail;
    }

    public void setDetail(List<ShareDetail> detail) {
        this.detail = detail;
    }

    public String getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(String updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    public static class ShareDetail implements Parcelable {
        private String role;
        private String price;
        private String count;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.role);
            dest.writeString(this.price);
            dest.writeString(this.count);
        }

        public ShareDetail() {
        }

        protected ShareDetail(Parcel in) {
            this.role = in.readString();
            this.price = in.readString();
            this.count = in.readString();
        }

        public static final Parcelable.Creator<ShareDetail> CREATOR = new Parcelable.Creator<ShareDetail>() {
            public ShareDetail createFromParcel(Parcel source) {
                return new ShareDetail(source);
            }

            public ShareDetail[] newArray(int size) {
                return new ShareDetail[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.currentPrice);
        dest.writeString(this.source);
        dest.writeString(this.riseValue);
        dest.writeString(this.openingPrice);
        dest.writeString(this.riseRate);
        dest.writeString(this.stockCode);
        dest.writeString(this.url);
        dest.writeString(this.mbmChartUrl);
        dest.writeString(this.lowPrice);
        dest.writeString(this.highPrice);
        dest.writeString(this.name);
        dest.writeString(this.updateDateTime);
        dest.writeString(this.closingPrice);
        dest.writeTypedList(detail);
    }

    public Share() {
    }

    protected Share(Parcel in) {
        this.currentPrice = in.readString();
        this.source = in.readString();
        this.riseValue = in.readString();
        this.openingPrice = in.readString();
        this.riseRate = in.readString();
        this.stockCode = in.readString();
        this.url = in.readString();
        this.mbmChartUrl = in.readString();
        this.lowPrice = in.readString();
        this.highPrice = in.readString();
        this.name = in.readString();
        this.updateDateTime = in.readString();
        this.closingPrice = in.readString();
        this.detail = in.createTypedArrayList(ShareDetail.CREATOR);
    }

    public static final Parcelable.Creator<Share> CREATOR = new Parcelable.Creator<Share>() {
        public Share createFromParcel(Parcel source) {
            return new Share(source);
        }

        public Share[] newArray(int size) {
            return new Share[size];
        }
    };
}
