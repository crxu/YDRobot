package com.readyidu.robot.component.voice;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

/**
 * Created by gx on 2017/10/13.
 */
public class SpeechSynthesizerUtil implements SynthesizerListener {
    private String IS_MUTE = "IS_MUTE";
    private SpeechSynthesizer mSpeechSynthesizer;//语音播放对象
    private boolean isMute;//是否为静音模式
    private SharedPreferences sp;

    private SpeechSynthesizerUtil() {
    }

    private static class SpeechSynthesizerUtilHolder {
        static SpeechSynthesizerUtil speechSynthesizerUtil = new SpeechSynthesizerUtil();
    }

    public static SpeechSynthesizerUtil getInstance() {
        return SpeechSynthesizerUtilHolder.speechSynthesizerUtil;
    }

    public void init(Context context) {
        sp = context.getSharedPreferences("setting", 0);
        isMute = sp.getBoolean(IS_MUTE, false);
        //初始化语言播放对象
        mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(context, null);
        mSpeechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "nannan");//发音人
        mSpeechSynthesizer.setParameter(SpeechConstant.SPEED, "80");//语速
        mSpeechSynthesizer.setParameter(SpeechConstant.VOLUME, "100");//音量
        mSpeechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
    }

    public void speak(String content) {
        if (!isMute) {
            mSpeechSynthesizer.startSpeaking(content, this);
        }
    }

    public void stopSpeak() {
        mSpeechSynthesizer.stopSpeaking();
    }

    public boolean isMute() {
        return isMute;
    }

    /**
     * 切换静音模式
     *
     * @return
     */
    public boolean toggleMute() {
        isMute = !isMute;
        if (isMute()) {
            stopSpeak();
        }
        sp.edit().putBoolean(IS_MUTE, isMute).apply();
        return isMute;
    }

    @Override
    public void onSpeakBegin() {

    }

    @Override
    public void onBufferProgress(int i, int i1, int i2, String s) {

    }

    @Override
    public void onSpeakPaused() {

    }

    @Override
    public void onSpeakResumed() {

    }

    @Override
    public void onSpeakProgress(int i, int i1, int i2) {

    }

    @Override
    public void onCompleted(SpeechError speechError) {

    }

    @Override
    public void onEvent(int i, int i1, int i2, Bundle bundle) {

    }
}