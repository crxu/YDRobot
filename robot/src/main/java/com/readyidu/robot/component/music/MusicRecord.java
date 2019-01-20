package com.readyidu.robot.component.music;

import android.content.Intent;
import android.text.TextUtils;

import com.readyidu.robot.YDRobot;
import com.readyidu.robot.api.NetSubscriber;
import com.readyidu.robot.api.impl.RobotServiceImpl;
import com.readyidu.robot.model.BaseModel;
import com.readyidu.robot.model.business.music.Music;
import com.readyidu.robot.utils.net.NetworkUtils;
import com.readyidu.robot.utils.view.ToastUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by gx on 2017/10/20.
 */
public class MusicRecord extends BaseVoiceUtils<Music> {

    private MusicRecord() {
    }

    private static class MusicRecordHolder {
        private static MusicRecord musicRecord = new MusicRecord();
    }

    public static MusicRecord getInstance() {
        return MusicRecordHolder.musicRecord;
    }

    private String prestraiKayWord = "";
    private String keyWord = "";

    public void setPrestraiKayWord(String prestraiKayWord) {
        this.prestraiKayWord = prestraiKayWord;
    }

    public String getKeyWord() {
        if (TextUtils.isEmpty(keyWord)) {
            return "";
        }
        return keyWord;
    }

    @Override
    public void setmMusics() {
        keyWord = prestraiKayWord;
        super.setmMusics();
    }

    public String getMusicId() {
        if (null != currentMusic) {
            return currentMusic.get_source().getMusic_id();
        }
        return "";
    }

    public int getCurrentPosition() {
        if (null != currentMusic) {
            return mMusics.indexOf(currentMusic);
        }
        return 0;
    }

    @Override
    public Music getCurrentData() {
        return currentMusic;
    }

    @Override
    public void setCurrentMusic(Music currentMusic) {
        if (null == currentMusic) {
            return;
        }

        if (this.currentMusic == null || !this.currentMusic.get_source().getMusic_id()
                .equals(currentMusic.get_source().getMusic_id())) {//如果第一次设置或者与上次歌曲不一样
            this.currentMusic = currentMusic;
            readPlay();
        } else {
            this.currentMusic = currentMusic;
        }
    }

    private void readPlay() {
        YDRobot.getInstance().pauseMusic();
        if (TextUtils.isEmpty(getMusicPath())) {
            RobotServiceImpl.getRobotMusicLinkResponse(getCurrentData().get_source().getMusic_id())
                    .subscribeWith(new NetSubscriber<BaseModel>() {
                        @Override
                        protected void onSuccess(BaseModel baseModel) {
                            getCurrentData().setPath(baseModel.data.file_link);
                            play();
                        }

                        @Override
                        protected void onFailure(int errorCode, String errorMessage) {
                            ToastUtil.showToast(YDRobot.getInstance().getContext(), errorCode + ":" + errorMessage);
                            YDRobot.getInstance().getContext().sendBroadcast(new Intent(MusicConstants.ACTION_MUSIC_RELEASE));

                            if (!NetworkUtils.isAvailable(YDRobot.getInstance().getContext())) {//解决断网的时候自动切换歌曲
                                ToastUtil.showToast(YDRobot.getInstance().getContext(), "网络已断开");
                                return;
                            }
                            Flowable.timer(1000, TimeUnit.MILLISECONDS)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<Long>() {
                                        @Override
                                        public void accept(Long aLong) throws Exception {
                                            ToastUtil.showToast(YDRobot.getInstance().getContext(), "即将为您自动切歌");
                                        }
                                    });
                            Flowable.timer(2500, TimeUnit.MILLISECONDS)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<Long>() {
                                        @Override
                                        public void accept(Long aLong) throws Exception {
                                            if (!NetworkUtils.isAvailable(YDRobot.getInstance().getContext())) {//解决短网的时候自动切换歌曲
                                                return;
                                            }
                                            switchMusic(false);
                                        }
                                    });
                        }
                    });
        } else {
            play();
        }
    }

    @Override
    public String getMusicPath() {
        if (null != currentMusic) {
            return currentMusic.getPath();
        } else {
            return "";
        }
    }

}