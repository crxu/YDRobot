package com.readyidu.robot.component.music;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.readyidu.robot.YDRobot;
import com.readyidu.robot.component.rxbus.RxBus;
import com.readyidu.robot.event.music.MusicEvent;
import com.readyidu.robot.utils.data.ArithmeticUtils;
import com.readyidu.robot.utils.log.LogUtils;

/**
 * @Autour: wlq
 * @Description: 音乐播放服务
 * @Date: 2017/10/14 20:07
 * @Update: 2017/10/14 20:07
 * @UpdateRemark: 音乐播放服务
 * @Version: V1.0
 */
public class MusicPlayerService extends Service implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    public static MediaPlayer mMediaPlayer;
    private AudioManager mAm;

    private boolean isPlaying;//音频焦点失去时是否在播放

    private BroadcastReceiver mMusicReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent) {
                return;
            }
            String action = intent.getAction();
            if (MusicConstants.ACTION_MUSIC_PAUSE_PLAY.equals(action)) {
                stop();
            } else if (MusicConstants.ACTION_MUSIC_TOGGLE_PLAY.equals(action)) {//暂停/恢复播放
                togglePlay();
            } else if (MusicConstants.ACTION_MUSIC_NEXT.equals(action)) {//下一首
                stop();
            } else if (MusicConstants.ACTION_MUSIC_UP.equals(action)) {//上一首
                stop();
            } else if (MusicConstants.ACTION_MUSIC_IS_PLYING.equals(action)) {//判断是否播放
                boolean isPlaying = false;
                if (null != mMediaPlayer) {
                    isPlaying = mMediaPlayer.isPlaying();
                }
                RxBus.getInstance().send(new MusicEvent.MusicIsPlaying(isPlaying));
                RxBus.getInstance().send(new MusicEvent.MusicState(MusicPlayerUtils.getmPlayState()));
            } else if (MusicConstants.ACTION_MUSIC_SINGLE_PLAY.equals(action)) {
                release();
                startPlay();
            } else if (MusicConstants.ACTION_MUSIC_RELEASE.equals(action)) {
                release();
            } else if (MusicConstants.ACTION_MUSIC_SET_PROGRESS.equals(action)) {
                if (null != mMediaPlayer) {
                    int progress = Math.max(0, intent.getIntExtra(MusicConstants.MUSIC_PROGRESS, 0));
                    if (mMediaPlayer.isPlaying()) {
                        int total = mMediaPlayer.getDuration();
                        int curProgress = (int) ArithmeticUtils.mul(ArithmeticUtils.div(progress, 100), total);
                        seekTo(curProgress);
                    }
                }
            } else if (MusicConstants.ACTION_MUSIC_GET_PROGRESS.equals(action)) {
                if (null != mMediaPlayer) {
                    if (mMediaPlayer.isPlaying()) {
                        int cur = mMediaPlayer.getCurrentPosition();
                        int total = mMediaPlayer.getDuration();
                        RxBus.getInstance().send(new MusicEvent.MusicPlayProgress(cur, total));
                    }
                }
            } else if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(action)) {
                pause();
            } else if (MusicConstants.ACTION_MUSIC_GET_MUSIC_INFO.equals(action)) {
                RxBus.getInstance().send(new MusicEvent.MusicInfo(MusicPlayerUtils.getCurrentData()));
            }
        }
    };

    @Override
    public void onCreate() {
        initMediaPlayer();

        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction(MusicConstants.ACTION_MUSIC_TOGGLE_PLAY);
        intentFilter2.addAction(MusicConstants.ACTION_MUSIC_NEXT);
        intentFilter2.addAction(MusicConstants.ACTION_MUSIC_UP);
        intentFilter2.addAction(MusicConstants.ACTION_MUSIC_IS_PLYING);
        intentFilter2.addAction(MusicConstants.ACTION_MUSIC_SINGLE_PLAY);
        intentFilter2.addAction(MusicConstants.ACTION_MUSIC_SET_PROGRESS);
        intentFilter2.addAction(MusicConstants.ACTION_MUSIC_GET_PROGRESS);
        intentFilter2.addAction(MusicConstants.ACTION_MUSIC_RELEASE);
        intentFilter2.addAction(MusicConstants.ACTION_MUSIC_GET_MUSIC_INFO);
        intentFilter2.addAction(MusicConstants.ACTION_MUSIC_PAUSE_PLAY);
        intentFilter2.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(mMusicReceiver, intentFilter2);
        super.onCreate();
    }

    private void initMediaPlayer() {
        if (null == mMediaPlayer) {
            LogUtils.e("mediaPlayer initMediaPlayer");
            mMediaPlayer = new MediaPlayer();
            mAm = (AudioManager) getSystemService(AUDIO_SERVICE);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (null != mMediaPlayer) {
                release();
                startPlay();
            } else {
                initMediaPlayer();
                setPlayData();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 开始播放音乐
     */
    private void startPlay() {
        LogUtils.e("mediaPlayer startPlay");
        if (null != mMediaPlayer) {
            setPlayData();
        } else {
            initMediaPlayer();
            setPlayData();
        }
    }

    /**
     * 设置播放数据
     */
    private void setPlayData() {
        LogUtils.e("mediaPlayer setPlayData");

        if (requestFocus()) {
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setOnPreparedListener(this);   //资源准备好
                mMediaPlayer.setOnCompletionListener(this);
                mMediaPlayer.setOnErrorListener(this);  //资源出错

                mMediaPlayer.setDataSource(MusicPlayerUtils.getMusicPath());
                mMediaPlayer.prepareAsync();
            } catch (Exception e) {
                LogUtils.e(e);
                e.printStackTrace();
            }
        }
    }

    /**
     * 暂停、恢复播放
     */
    public void togglePlay() {
        if (null != mMediaPlayer) {
            synchronized (mMediaPlayer) {
                try {
                    if (mMediaPlayer.isPlaying()) {     //暂停
                        pause();
                    } else {    //恢复
                        restart();
                    }
                } catch (Exception e) {
                    LogUtils.e(e);
                }
            }
        } else {
            initMediaPlayer();
            setPlayData();
        }
    }

    /**
     * 暂停播放
     */
    public void pause() {
        if (null != mMediaPlayer) {
            LogUtils.e("mediaPlayer pause");
            mMediaPlayer.pause();
            MusicPlayerUtils.setmPlayState(1);
        }
    }

    /**
     * 继续播放
     */
    public void restart() {
        if (null != mMediaPlayer) {
            LogUtils.e("mediaPlayer restart");
            mMediaPlayer.start();
            MusicPlayerUtils.setmPlayState(2);
        } else {
            initMediaPlayer();
            setPlayData();
        }
    }

    /**
     * 停止播放
     */
    public void stop() {
        LogUtils.e("mediaPlayer stop");
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            MusicPlayerUtils.setmPlayState(3);
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        if (null != mMediaPlayer) {
            synchronized (mMediaPlayer) {
                try {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                } catch (Exception e) {
                    LogUtils.e(e);
                } finally {
                    mMediaPlayer = null;
                }
            }
        }

        mAm.abandonAudioFocus(null);

        MusicPlayerUtils.setmPlayState(3);
    }

    /**
     * 调到指定位置
     *
     * @param progress
     */
    private void seekTo(int progress) {
        if (null != mMediaPlayer) {
            mMediaPlayer.seekTo(progress);
            if (mMediaPlayer.isPlaying()) {
                int cur = mMediaPlayer.getCurrentPosition();
                int total = mMediaPlayer.getDuration();
                RxBus.getInstance().send(new MusicEvent.MusicPlayProgress(cur, total));
            }
        }
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        LogUtils.e("mediaPlayer onPrepared");
        if (null != mMediaPlayer) {
            mMediaPlayer.start();
            MusicPlayerUtils.setmPlayState(0);
            RxBus.getInstance().send(new MusicEvent.MusicIsPlaying(true));
            YDRobot.getInstance().getPlayingMusicInfo();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (!mMediaPlayer.isLooping()) {
            mAm.abandonAudioFocus(this);
        }
        YDRobot.getInstance().switchMusic(false, true);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        LogUtils.e("mediaPlayer onError:(" + i + "," + i1 + ")");
        release();
        MusicPlayerUtils.setmPlayState(3);
        return true;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:  //你已经得到了音频焦点。
                if (null != mMediaPlayer) {
                    if (isPlaying) {
                        restart();
                    }
                    mMediaPlayer.setVolume(1.0f, 1.0f);
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:  //失去音频焦点，并且会持续很长时间。你必须停止所有的音频播放
                //TODO 其他音乐播放器播放音乐时，会触发此回调
                if (null != mMediaPlayer) {
                    if (mMediaPlayer.isPlaying()) {
                        stop();
                    }
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }
                mAm.abandonAudioFocus(this);
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:    //你暂时失去了音频焦点
                if (null != mMediaPlayer) {
                    isPlaying = mMediaPlayer.isPlaying();
                    if (isPlaying) {
                        pause();
                    }
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK://你暂时失去了音频焦点，但你可以小声地继续播放音频（低音量）而不是完全扼杀音频。
                if (null != mMediaPlayer && mMediaPlayer.isPlaying()) {
                    mMediaPlayer.setVolume(0.1f, 0.1f);
                }
                break;
        }
    }

    /**
     * 请求音频焦点
     *
     * @return
     */
    private boolean requestFocus() {
        int result = mAm.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LogUtils.e("mediaPlayer onDestroy");
        release();
        mAm.abandonAudioFocus(this);
        mAm = null;
        if (mMusicReceiver != null) {
            unregisterReceiver(mMusicReceiver);
        }
    }
}