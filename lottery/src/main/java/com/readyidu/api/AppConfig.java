package com.readyidu.api;

public class AppConfig {

    public static final boolean isOpenLog = true;
    public static final boolean isDebug = true;//是否是测试
    public static final boolean isPreRelease = false;//是否是预发布

    public static String BASE_API_URL = "";

    static {
        if (isDebug) {
            if (isPreRelease) {
                //预发布
                BASE_API_URL = "http://pre.brain.readyidu.com:8666/";

            } else {
                //本地测试
                BASE_API_URL = "http://192.168.4.168:8081/";
            }

        } else {
            //release 正式环境
            BASE_API_URL = "http://brain.readyidu.com:8666/";
        }
    }

    public static final String APP_ID = "10001";
    public static final String APP_PASSWORD = "123456";
    public static final String APP_API_VERSION = "1.1.5";

    public static final String SP_XY_APP = "sp_xy_app";
    public static final String KEY_DEVICE_ID = "key_device_id";
    public static final String KEY_UUID = "key_uuid";
    public static final String KEY_API_URL = "key_api_url";


}
