package com.readyidu.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.readyidu.api.AppConfig;
import com.readyidu.LotteryApplication;

import java.util.UUID;

public class DeviceUtils {

    /**
     * 获取注册设备信息
     */
    public static String getDeviceRegisterInfo() {
        return "deviceId=" + getDeviceId() + "&packageName=com.readyidu.routerapp"
                + "&deviceType=android" + "&deviceName=" + getDeviceName() + "&sysVersion="
                + getOSVersion() + "&resolution=720*1080" + "&country=" + getCountry() + "language="
                + getLanguage();
    }

    /**
     * 获取设备ID
     */
    public static String getDeviceId() {
        SharedPreferences sp = LotteryApplication.getContext()
                .getSharedPreferences(AppConfig.SP_XY_APP, Context.MODE_PRIVATE);
        String device_id = sp.getString(AppConfig.KEY_DEVICE_ID, "");
        if (!TextUtils.isEmpty(device_id)) {
            return device_id;
        }

        StringBuilder deviceId = new StringBuilder();
        // 渠道标志
        deviceId.append("a");

        try {
            //wifi mac地址
            WifiManager wifi = (WifiManager) LotteryApplication.getContext().getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            String wifiMac = info.getMacAddress();
            if (!TextUtils.isEmpty(wifiMac)) {
                deviceId.append("wifi");
                deviceId.append(wifiMac);
                Log.e("getDeviceId : ", deviceId.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //IMEI（imei）
            TelephonyManager tm = (TelephonyManager) LotteryApplication.getContext()
                    .getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            if (!TextUtils.isEmpty(imei)) {
                deviceId.append("imei");
                deviceId.append(imei);
                Log.e("getDeviceId : ", deviceId.toString());
            }

            //序列号（sn）
            String sn = tm.getSimSerialNumber();
            if (!TextUtils.isEmpty(sn)) {
                deviceId.append("sn");
                deviceId.append(sn);
                Log.e("getDeviceId : ", deviceId.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //如果上面都没有， 则生成一个id：随机码
            String uuid = getUUID();
            if (!TextUtils.isEmpty(uuid)) {
                deviceId.append("id");
                deviceId.append(uuid);
                Log.e("getDeviceId : ", deviceId.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("getDeviceId : ", deviceId.toString());

        String result = MD5.toMD5(deviceId.toString());
        sp.edit().putString(AppConfig.KEY_DEVICE_ID, result).apply();

        return result;
    }

    /**
     * 得到全局唯一UUID
     */
    private static String getUUID() {
        SharedPreferences sp = LotteryApplication.getContext()
                .getSharedPreferences(AppConfig.SP_XY_APP, Context.MODE_PRIVATE);
        String uuid = sp.getString(AppConfig.KEY_UUID, "");
        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
            sp.edit().putString(AppConfig.KEY_UUID, uuid).apply();
        }
        Log.e("getUUID : ", uuid);
        return uuid;
    }

    /**
     * 获取设备所属国家
     */
    private static String getCountry() {
        return LotteryApplication.getContext().getResources().getConfiguration().locale
                .getCountry();
    }

    /**
     * 获取设备当前语言
     */
    private static String getLanguage() {
        return LotteryApplication.getContext().getResources().getConfiguration().locale
                .getLanguage();
    }

    /**
     * 设备名称
     */
    private static String getDeviceName() {
        return Build.DEVICE;
    }

    /**
     * 系统版本号
     */
    private static String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

}
