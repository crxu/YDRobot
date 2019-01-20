package com.readyidu.robot.component.music;

import android.content.Intent;

import com.readyidu.robot.YDRobot;
import com.readyidu.robot.component.rxbus.RxBus;
import com.readyidu.robot.event.music.MusicEvent;
import com.readyidu.robot.utils.log.LogUtils;

/**
 * Created by gx on 2018/3/8.
 */
public class MusicPlayerUtils {
    private static VoicePlayInterface voicePlayInterface;

    private static int mPlayState;//播放状态：0 播放；1 暂停；2恢复；3 停止

    public static void play(VoicePlayInterface voicePlayInterface) {
        if (voicePlayInterface != null && MusicPlayerUtils.voicePlayInterface != voicePlayInterface) {
            MusicPlayerUtils.voicePlayInterface = voicePlayInterface;
        }

        YDRobot.getInstance().getContext().
                startService(new Intent(YDRobot.getInstance().getContext(), MusicPlayerService.class));
    }

    public static void release(VoicePlayInterface voicePlayInterface) {
        if (MusicPlayerUtils.voicePlayInterface == voicePlayInterface) {
            YDRobot.getInstance().releaseMusic();
        }
    }

    public static int getmPlayState() {
        return mPlayState;
    }

    public static void setmPlayState(int mPlayState) {
        MusicPlayerUtils.mPlayState = mPlayState;
        RxBus.getInstance().send(new MusicEvent.MusicState(mPlayState));
        LogUtils.i("2 MusicEvent.MusicState:" + mPlayState);
    }

    public static String getMusicPath() {
        if (MusicPlayerUtils.voicePlayInterface != null) {
            return MusicPlayerUtils.voicePlayInterface.getMusicPath();
        }
        return "";
    }

    public static Object getCurrentData() {
        if (MusicPlayerUtils.voicePlayInterface != null) {
            return MusicPlayerUtils.voicePlayInterface.getCurrentData();
        }
        return null;
    }
}