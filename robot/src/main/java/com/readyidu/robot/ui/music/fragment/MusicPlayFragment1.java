package com.readyidu.robot.ui.music.fragment;

import android.animation.ObjectAnimator;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.readyidu.robot.R;
import com.readyidu.robot.base.BaseFragment;
import com.readyidu.robot.component.music.MusicRecord;
import com.readyidu.robot.event.music.MusicEvent;
import com.readyidu.robot.model.business.music.Music;
import com.readyidu.robot.utils.glide.GlideUtils;
import com.readyidu.robot.utils.log.LogUtils;

import io.reactivex.functions.Consumer;

/**
 * @Autour: wlq
 * @Description: 音乐播放封面
 * @Date: 2017/10/14 18:08
 * @Update: 2017/10/14 18:08
 * @UpdateRemark: 音乐播放封面
 * @Version:
 */
public class MusicPlayFragment1 extends BaseFragment {

    ImageView roundedImageView;
    private ObjectAnimator objectAnimator;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_music_play1;
    }

    @Override
    protected void bindViews(View contentView) {
        roundedImageView = (ImageView) contentView.findViewById(R.id.iv_music_play_singer);
    }

    @Override
    protected void bindEvents() {
        refreshImage(MusicRecord.getInstance().getCurrentData());
        registerRxBus(MusicEvent.SwitchMusic.class, new Consumer<MusicEvent.SwitchMusic>() {
            @Override
            public void accept(MusicEvent.SwitchMusic switchMusic) throws Exception {
                refreshImage(switchMusic.music);
            }
        });
        registerRxBus(MusicEvent.MusicState.class, new Consumer<MusicEvent.MusicState>() {
            @Override
            public void accept(MusicEvent.MusicState musicState) throws Exception {
                switch (musicState.playState) {
                    case 0:
                        startMusicAnimation();
                        break;
                    case 1:
                        pauseAnim();
                        break;
                    case 2:
                        resumeAnim();
                        break;
                    case 3:
                        stopAnim();
                        break;
                }
            }
        });
    }

    private void refreshImage(Music music) {
        try {
            String musicImageUrl = music.get_source().getAlbum_pic();
            GlideUtils.loadMusicSingerImageRes(mContext, R.drawable.ic_music_play_head_default_background, roundedImageView);
            if (!TextUtils.isEmpty(musicImageUrl)) {
                GlideUtils.loadMusicSingerImageNet2(mContext, musicImageUrl, roundedImageView);
            }
        } catch (Exception e) {
            LogUtils.e(e);
            GlideUtils.loadMusicSingerImageRes(mContext, R.drawable.ic_music_play_head_default_background, roundedImageView);
        }
    }

    private void startMusicAnimation() {
        if (null == objectAnimator) {
            objectAnimator = ObjectAnimator.ofFloat(roundedImageView, "rotation", 0.0f, 360.0f);
            objectAnimator.setDuration(30000);
            objectAnimator.setInterpolator(new LinearInterpolator());
            objectAnimator.setRepeatCount(-1);
            objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
        }
        if (!objectAnimator.isRunning()) {
            objectAnimator.start();
        }
    }

    private void pauseAnim() {
        if (null != objectAnimator) {
            objectAnimator.pause();
        }
    }

    private void resumeAnim() {
        if (null != objectAnimator) {
            objectAnimator.resume();
        } else {
            startMusicAnimation();
        }
    }

    private void stopAnim() {
        if (null != objectAnimator) {
            objectAnimator.end();
            objectAnimator.cancel();
            objectAnimator = null;
        }
    }
}
