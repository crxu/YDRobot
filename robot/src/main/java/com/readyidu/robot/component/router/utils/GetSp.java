package com.readyidu.robot.component.router.utils;

import com.readyidu.robot.utils.SPUtils;

/**
 * Created by Admin on 2017/3/9.
 */
public class GetSp {

    private static final String SP_KEY_USER_ID = "UserId";
    private static final String SP_KEY_USER_TOKEN = "UserToken";
    private static final String SP_KEY_DEVICE_KEY = "DEVICE_KEY";

    public static String getIsVoice() {
        return (String) SPUtils.get("speech_isVoice", "");
    }

    public static void setIsVoice(String isVoice) {
        SPUtils.put("speech_isVoice", isVoice);
    }

    public static void setTvKey(String tvKey) {
        SPUtils.put("tv_kill", tvKey);
    }

    public static String getTvKey() {
        return (String) SPUtils.get("tv_kill", "");
    }
}