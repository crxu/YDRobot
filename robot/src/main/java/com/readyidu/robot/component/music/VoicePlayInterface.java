package com.readyidu.robot.component.music;

/**
 * Created by gx on 2018/3/9.
 */
public interface VoicePlayInterface<T> {

    String getMusicPath();

    T getCurrentData();

    void play();

    void release();

}