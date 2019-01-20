package com.readyidu.robot.component.music;

import android.content.Intent;
import android.os.Handler;

import com.readyidu.robot.YDRobot;
import com.readyidu.robot.component.rxbus.RxBus;
import com.readyidu.robot.event.music.MusicEvent;

import java.util.ArrayList;

/**
 * Created by gx on 2018/3/9.
 */
public abstract class BaseVoiceUtils<T> implements VoicePlayInterface<T> {

    protected ArrayList<T> mMusics = new ArrayList<>();
    protected ArrayList<T> prestrainMusic = new ArrayList<>();//预加载列表，在点击列表进入播放界面的时候赋值到mMusics

    protected T currentMusic;

    public ArrayList<T> getmMusics() {
        return mMusics;
    }

    public ArrayList<T> getPrestrainMusic() {
        return prestrainMusic;
    }

    public void setmMusics() {
        mMusics.clear();
        mMusics.addAll(prestrainMusic);
    }

    private int mPlayMode = MusicConstants.PlayMode.ORDER;

    public int getmPlayMode() {
        return mPlayMode;
    }

    public void setmPlayMode(int mPlayMode) {
        this.mPlayMode = mPlayMode;
    }

    public void switchPlayMode() {
        switch (mPlayMode) {
            case MusicConstants.PlayMode.SINGLE:
                setmPlayMode(MusicConstants.PlayMode.ORDER);
                break;
            case MusicConstants.PlayMode.ORDER:
                setmPlayMode(MusicConstants.PlayMode.RANDOM);
                break;
            case MusicConstants.PlayMode.RANDOM:
                setmPlayMode(MusicConstants.PlayMode.SINGLE);
                break;
        }
    }

    public void switchMusic(boolean up) {
        switchMusic(up, false);
    }

    /**
     * 切换歌曲
     *
     * @param up   是否是上一首
     * @param auto 是否是播放完毕自动切换的
     */
    public void switchMusic(boolean up, boolean auto) {
        switch (mPlayMode) {
            case MusicConstants.PlayMode.SINGLE:
                if (auto) {//如果是手动切换的话按顺序播放处理
                    play();
                } else {
                    nextMusic(up);
                }
                break;
            case MusicConstants.PlayMode.ORDER:
                nextMusic(up);
                break;
            case MusicConstants.PlayMode.RANDOM:
                double random = Math.random();
                int in = (int) (random * mMusics.size());
                setCurrentMusic(mMusics.get(in));
                break;
        }
        RxBus.getInstance().send(new MusicEvent.SwitchMusic(MusicRecord.getInstance().getCurrentData()));
    }

    private void nextMusic(boolean up) {
        int position = mMusics.indexOf(currentMusic);
        if (up) {
            if (position > 0) {
                setCurrentMusic(mMusics.get(position - 1));
            } else {
                setCurrentMusic(mMusics.get(mMusics.size() - 1));
            }
        } else {
            if (position < mMusics.size() - 1) {
                setCurrentMusic(mMusics.get(position + 1));
            } else {
                setCurrentMusic(mMusics.get(0));
            }
        }
    }

    abstract void setCurrentMusic(T t);

    public void refreshProgress() {
        stopRefreshSeekBar();
        mHandler.post(r);
    }

    public void stopRefreshSeekBar() {
        mHandler.removeCallbacks(r);
    }

    private Handler mHandler = new Handler();
    private Runnable r = new Runnable() {
        @Override
        public void run() {
            YDRobot.getInstance().getContext().
                    sendBroadcast(new Intent(MusicConstants.ACTION_MUSIC_GET_PROGRESS));

            mHandler.postDelayed(r, 1000);
        }
    };

    @Override
    public String getMusicPath() {
        return null;
    }

    @Override
    public T getCurrentData() {
        return null;
    }

    @Override
    public void play() {
        MusicPlayerUtils.play(this);
    }

    @Override
    public void release() {
        currentMusic = null;
        MusicPlayerUtils.release(this);
    }
}