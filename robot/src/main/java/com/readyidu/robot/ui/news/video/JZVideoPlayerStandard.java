package com.readyidu.robot.ui.news.video;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.readyidu.robot.R;
import com.readyidu.robot.component.ijk.utils.NetWorkUtils;
import com.readyidu.robot.ui.tv.utils.TvDataTransform;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Nathen
 * On 2016/04/18 16:15
 */
public class JZVideoPlayerStandard extends JZVideoPlayer {

    public FrameLayout mFlBack;
    public TextView titleTextView;

    protected DismissControlViewTimerTask mDismissControlViewTimerTask;
    protected static Timer DISMISS_CONTROL_VIEW_TIMER;

    public JZVideoPlayerStandard(Context context) {
        super(context);
    }

    public JZVideoPlayerStandard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(Context context) {
        super.init(context);

        titleTextView = (TextView) findViewById(R.id.title);
        mFlBack = (FrameLayout) findViewById(R.id.fl_back);
        mRlAlreadyLook = (RelativeLayout) findViewById(R.id.rl_already_look);
        mIvAlreadyLookThumb = (ImageView) findViewById(R.id.iv_already_look_thumb);
        mRlThumb = (RelativeLayout) findViewById(R.id.rl_thumb_layout);
        thumbImageView = (ImageView) findViewById(R.id.thumb);
        mRlLoading = (RelativeLayout) findViewById(R.id.rl_loading);
        mRlReload = (RelativeLayout) findViewById(R.id.rl_reload);
        mIvReloadThumb = (ImageView) findViewById(R.id.iv_reload_thumb);
        mTvReload = (TextView) findViewById(R.id.tv_reload);
        mRlReplay = (RelativeLayout) findViewById(R.id.rl_replay_layout);
        mIvReplayThumb = (ImageView) findViewById(R.id.iv_replay_thumb);

        String reloadString = "视频加载失败，<font color=\'#619fff\'>点击重试</font>";
        mTvReload.setText(Html.fromHtml(reloadString));

        mRlAlreadyLook.setOnClickListener(this);
        mFlBack.setOnClickListener(this);
        mRlThumb.setOnClickListener(this);
        mRlReload.setOnClickListener(this);
        mRlReplay.setOnClickListener(this);
    }

    public void setUp(Object[] dataSourceObjects, int defaultUrlMapIndex, int screen, Object... objects) {
        super.setUp(dataSourceObjects, defaultUrlMapIndex, screen, objects);
        if (objects.length != 0) titleTextView.setText(objects[0].toString());
        if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            fullscreenButton.setImageResource(R.drawable.ic_land_to_port);
            mFlBack.setVisibility(View.VISIBLE);
            titleTextView.setVisibility(View.VISIBLE);
        } else if (currentScreen == SCREEN_WINDOW_LIST) {
            fullscreenButton.setImageResource(R.drawable.ic_port_to_land);
            mFlBack.setVisibility(View.GONE);
            titleTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.jz_layout_standard;
    }

    @Override
    public void onStateNormal() {
        super.onStateNormal();
        changeUiToNormal();
    }

    @Override
    public void onStatePreparing() {
        super.onStatePreparing();
        changeUiToPreparing();
    }

    @Override
    public void onStatePreparingChangingUrl(int urlMapIndex, long seekToInAdvance) {
        super.onStatePreparingChangingUrl(urlMapIndex, seekToInAdvance);
        setPlayStateUI(CURRENT_STATE_PREPARING);
    }

    @Override
    public void onStatePlaying() {
        super.onStatePlaying();
        changeUiToPlayingClear();
    }

    @Override
    public void onStatePause() {
        super.onStatePause();
        changeUiToPauseShow();
        cancelDismissControlViewTimer();
    }

    @Override
    public void onStateError() {
        super.onStateError();
        changeUiToError();
    }

    @Override
    public void onStateAutoComplete() {
        super.onStateAutoComplete();
        changeUiToComplete();
        cancelDismissControlViewTimer();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int id = v.getId();
        if (id == R.id.surface_container) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    startDismissControlViewTimer();
                    if (!mChangeVolume) {
                        onEvent(JZUserActionStandard.ON_CLICK_BLANK);
                        onClickUiToggle();
                    }
                    break;
            }
        } else if (id == R.id.bottom_seek_progress) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    cancelDismissControlViewTimer();
                    break;
                case MotionEvent.ACTION_UP:
                    startDismissControlViewTimer();
                    break;
            }
        }
        return super.onTouch(v, event);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int i = v.getId();
        if (i == R.id.rl_thumb_layout) {
            if (!NetWorkUtils.isNetworkAvailable(mAttachActivity)) {
                showToast("网络连接断开");
                return;
            }
            if (currentState == CURRENT_STATE_NORMAL) {
                if (TvDataTransform.getVideoNewsIsShowWifiDialog() && NetWorkUtils.isMobileConnected(mAttachActivity)) {
                    showWifiWarnDialogAfterClick(0);
                } else {
                    onEvent(JZUserActionStandard.ON_CLICK_START_THUMB);
                    startVideo();
                }
            } else if (currentState == CURRENT_STATE_AUTO_COMPLETE) {
                onClickUiToggle();
            }
        } else if (i == R.id.surface_container) {
            startDismissControlViewTimer();
        } else if (i == R.id.fl_back) {
            autoQuitFullscreen();
        } else if (i == R.id.rl_reload) {   //加载失败，重新加载
            if (!NetWorkUtils.isNetworkAvailable(mAttachActivity)) {
                showToast("网络连接断开");
                return;
            }
            if (TvDataTransform.getVideoNewsIsShowWifiDialog() && NetWorkUtils.isMobileConnected(mAttachActivity)) {
                showWifiWarnDialogAfterClick(1);
            } else {
                initTextureView();
                addTextureView();
                JZMediaManager.setDataSource(dataSourceObjects);
                JZMediaManager.setCurrentDataSource(JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex));
                onStatePreparing();
                onEvent(JZUserAction.ON_CLICK_START_ERROR);
            }
        }
    }

    /**
     * 是否显示已观看
     */
    public void setAlreadyLook(boolean isLook) {
        if (isLook) {   //已观看
            mRlAlreadyLook.setVisibility(View.VISIBLE);
            mRlThumb.setVisibility(View.GONE);
            mRlLoading.setVisibility(View.GONE);
            mRlReplay.setVisibility(View.GONE);
            mRlReload.setVisibility(View.GONE);
        } else {    //未观看
            mRlThumb.setVisibility(View.VISIBLE);
            mRlAlreadyLook.setVisibility(View.GONE);
            mRlLoading.setVisibility(View.GONE);
            mRlReplay.setVisibility(View.GONE);
            mRlReload.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        super.onStartTrackingTouch(seekBar);
        cancelDismissControlViewTimer();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        super.onStopTrackingTouch(seekBar);
        startDismissControlViewTimer();
    }

    public void onClickUiToggle() {
        if (currentState == CURRENT_STATE_PREPARING) {
            changeUiToPreparing();
        } else if (currentState == CURRENT_STATE_PLAYING) {
            if (bottomContainer.isShown()) {
                changeUiToPlayingClear();
            } else {
                changeUiToPlayingShow();
            }
        } else if (currentState == CURRENT_STATE_PAUSE) {
            if (null != bottomContainer && bottomContainer.isShown()) {
                changeUiToPauseClear();
            } else {
                changeUiToPauseShow();
            }
        }
    }

    public void onCLickUiToggleToClear() {
        if (currentState == CURRENT_STATE_PREPARING) {
            if (null != bottomContainer && bottomContainer.isShown()) {
                changeUiToPreparing();
            }
        } else if (currentState == CURRENT_STATE_PLAYING) {
            if (null != bottomContainer && bottomContainer.isShown()) {
                changeUiToPlayingClear();
            }
        } else if (currentState == CURRENT_STATE_PAUSE) {
            if (null != bottomContainer && bottomContainer.isShown()) {
                changeUiToPauseClear();
            }
        } else if (currentState == CURRENT_STATE_AUTO_COMPLETE) {
            if (null != bottomContainer && bottomContainer.isShown()) {
                changeUiToComplete();
            }
        }
    }

    @Override
    public void setProgressAndText(int progress, long position, long duration) {
        super.setProgressAndText(progress, position, duration);
    }

    @Override
    public void setBufferProgress(int bufferProgress) {
        super.setBufferProgress(bufferProgress);
    }

    @Override
    public void resetProgressAndTime() {
        super.resetProgressAndTime();
    }

    public void changeUiToNormal() {
        switch (currentScreen) {
            case SCREEN_WINDOW_LIST:
                setAllControlsVisibility(View.VISIBLE, View.INVISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.VISIBLE, View.INVISIBLE, View.INVISIBLE);
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisibility(View.VISIBLE, View.INVISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.VISIBLE, View.INVISIBLE, View.INVISIBLE);
                break;
        }
    }

    public void changeUiToPreparing() {
        switch (currentScreen) {
            case SCREEN_WINDOW_LIST:
                setAllControlsVisibility(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.VISIBLE, View.VISIBLE, View.INVISIBLE, View.INVISIBLE);
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisibility(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.VISIBLE, View.VISIBLE, View.INVISIBLE, View.INVISIBLE);
                break;
        }

    }

    public void changeUiToPlayingShow() {
        switch (currentScreen) {
            case SCREEN_WINDOW_LIST:
                setAllControlsVisibility(View.VISIBLE, View.VISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisibility(View.VISIBLE, View.VISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
                break;
        }

    }

    public void changeUiToPlayingClear() {
        switch (currentScreen) {
            case SCREEN_WINDOW_LIST:
                setAllControlsVisibility(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.VISIBLE, View.INVISIBLE);
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisibility(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.VISIBLE, View.INVISIBLE);
                break;
        }

    }

    public void changeUiToPauseShow() {
        switch (currentScreen) {
            case SCREEN_WINDOW_LIST:
                setAllControlsVisibility(View.VISIBLE, View.VISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisibility(View.VISIBLE, View.VISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.INVISIBLE);
                break;
        }
    }

    public void changeUiToPauseClear() {
        switch (currentScreen) {
            case SCREEN_WINDOW_LIST:
                setAllControlsVisibility(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.VISIBLE, View.INVISIBLE);
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisibility(View.INVISIBLE, View.INVISIBLE, View.INVISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.VISIBLE, View.INVISIBLE);
                break;
        }

    }

    public void changeUiToComplete() {
        switch (currentScreen) {
            case SCREEN_WINDOW_LIST:
                setAllControlsVisibility(View.VISIBLE, View.INVISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.VISIBLE, View.INVISIBLE, View.INVISIBLE);
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisibility(View.VISIBLE, View.INVISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.VISIBLE, View.INVISIBLE, View.INVISIBLE);
                break;
        }

    }

    public void changeUiToError() {
        switch (currentScreen) {
            case SCREEN_WINDOW_LIST:
                setAllControlsVisibility(View.INVISIBLE, View.INVISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.VISIBLE);
                break;
            case SCREEN_WINDOW_FULLSCREEN:
                setAllControlsVisibility(View.INVISIBLE, View.INVISIBLE, View.VISIBLE,
                        View.INVISIBLE, View.INVISIBLE, View.INVISIBLE, View.VISIBLE);
                break;
        }

    }

    public void setAllControlsVisibility(int topCon, int bottomCon, int startBtn, int loadingPro,
                                         int thumbImg, int bottomPro, int retryLayout) {
        topContainer.setVisibility(topCon);
        bottomContainer.setVisibility(bottomCon);
    }

    @Override
    public void showVolumeDialog(float deltaY, int volumePercent) {
        super.showVolumeDialog(deltaY, volumePercent);
        mLlVolumeBright.setVisibility(View.VISIBLE);
        if (volumePercent > 100) {
            volumePercent = 100;
        } else if (volumePercent < 0) {
            volumePercent = 0;
        }
        mIvVolumeBright.setBackgroundResource(R.mipmap.ic_volume_on);
        mPbVolumeBright.setMax(100);
        mPbVolumeBright.setProgress(volumePercent);
        onCLickUiToggleToClear();
    }

    @Override
    public void dismissVolumeDialog() {
        super.dismissVolumeDialog();
        mLlVolumeBright.setVisibility(View.GONE);
    }

    @Override
    public void showBrightnessDialog(int brightnessPercent) {
        super.showBrightnessDialog(brightnessPercent);
        mLlVolumeBright.setVisibility(View.VISIBLE);
        if (brightnessPercent > 100) {
            brightnessPercent = 100;
        } else if (brightnessPercent < 0) {
            brightnessPercent = 0;
        }
        mIvVolumeBright.setBackgroundResource(R.mipmap.ic_brightness);
        mPbVolumeBright.setMax(100);
        mPbVolumeBright.setProgress(brightnessPercent);
        onCLickUiToggleToClear();
    }

    @Override
    public void dismissBrightnessDialog() {
        super.dismissBrightnessDialog();
        mLlVolumeBright.setVisibility(View.GONE);
    }

    public void startDismissControlViewTimer() {
        cancelDismissControlViewTimer();
        DISMISS_CONTROL_VIEW_TIMER = new Timer();
        mDismissControlViewTimerTask = new DismissControlViewTimerTask();
        DISMISS_CONTROL_VIEW_TIMER.schedule(mDismissControlViewTimerTask, 2500);
    }

    public void cancelDismissControlViewTimer() {
        if (DISMISS_CONTROL_VIEW_TIMER != null) {
            DISMISS_CONTROL_VIEW_TIMER.cancel();
        }
        if (mDismissControlViewTimerTask != null) {
            mDismissControlViewTimerTask.cancel();
        }
    }

    @Override
    public void onAutoCompletion() {
        super.onAutoCompletion();

        cancelDismissControlViewTimer();
    }

    @Override
    public void onCompletion() {
        super.onCompletion();
        cancelDismissControlViewTimer();
    }

    public void dismissControlView() {
        if (currentState != CURRENT_STATE_NORMAL
                && currentState != CURRENT_STATE_ERROR
                && currentState != CURRENT_STATE_AUTO_COMPLETE) {
            if (getContext() != null && getContext() instanceof Activity) {
                ((Activity) getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bottomContainer.setVisibility(View.INVISIBLE);
                        topContainer.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }
    }

    public class DismissControlViewTimerTask extends TimerTask {

        @Override
        public void run() {
            dismissControlView();
        }
    }
}
