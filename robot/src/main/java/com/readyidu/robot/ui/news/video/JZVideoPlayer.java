package com.readyidu.robot.ui.news.video;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.readyidu.baiduvoice.laster.FloatViewInstance;
import com.readyidu.robot.AppConfig;
import com.readyidu.robot.R;
import com.readyidu.robot.component.ijk.utils.NetWorkUtils;
import com.readyidu.robot.component.rxbus.RxBus;
import com.readyidu.robot.db.VideoNewsDbDao;
import com.readyidu.robot.event.news.VideoNewsEvent;
import com.readyidu.robot.ui.tv.utils.TvDataTransform;
import com.readyidu.robot.ui.tv.view.NetworkWarnDialog;
import com.readyidu.robot.utils.data.ArithmeticUtils;
import com.readyidu.robot.utils.log.LogUtils;
import com.readyidu.robot.utils.net.NetworkUtils;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by Nathen on 16/7/30.
 */
public abstract class JZVideoPlayer extends FrameLayout implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, View.OnTouchListener {

    public static final String TAG = "JZVideoPlayer";
    public static final int THRESHOLD = 80;
    public static final int FULL_SCREEN_NORMAL_DELAY = 300;

    public static final int SCREEN_WINDOW_LIST = 1;
    public static final int SCREEN_WINDOW_FULLSCREEN = 2;

    public static final int CURRENT_STATE_NORMAL = 0;
    public static final int CURRENT_STATE_PREPARING = 1;
    public static final int CURRENT_STATE_PREPARING_CHANGING_URL = 2;
    public static final int CURRENT_STATE_PLAYING = 3;
    public static final int CURRENT_STATE_PAUSE = 5;
    public static final int CURRENT_STATE_AUTO_COMPLETE = 6;
    public static final int CURRENT_STATE_ERROR = 7;

    public static final String URL_KEY_DEFAULT = "URL_KEY_DEFAULT";//当播放的地址只有一个的时候的key
    public static final int VIDEO_IMAGE_DISPLAY_TYPE_FILL_PARENT = 1;
    public static final int VIDEO_IMAGE_DISPLAY_TYPE_FILL_SCROP = 2;
    public static final int VIDEO_IMAGE_DISPLAY_TYPE_ORIGINAL = 3;
    public static boolean ACTION_BAR_EXIST = true;
    public static boolean TOOL_BAR_EXIST = true;
    public static boolean SAVE_PROGRESS = true;
    public static int VIDEO_IMAGE_DISPLAY_TYPE = 0;
    public static long CLICK_QUIT_FULLSCREEN_TIME = 0;
    public static long lastAutoFullscreenTime = 0;
    public static AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {//是否新建个class，代码更规矩，并且变量的位置也很尴尬
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
            }
        }
    };
    protected static JZUserAction jzUserAction;
    protected static Timer mUpdateProgressTimer;
    public int currentState = -1;
    public int currentScreen = -1;
    public Object[] objects = null;
    public long seekToInAdvance = 0;
    public RelativeLayout mRlThumb;
    public ImageView thumbImageView;
    public RelativeLayout mRlAlreadyLook;
    public ImageView mIvAlreadyLookThumb;
    public RelativeLayout mRlLoading;
    public RelativeLayout mRlReload;
    public TextView mTvReload;
    public ImageView mIvReloadThumb;
    public RelativeLayout mRlReplay;
    public ImageView mIvReplayThumb;
    public SeekBar progressBar;
    public ImageView fullscreenButton;
    public ImageView playPauseButton;
    public TextView mTvCurrentTime, mTvTotalTime;
    public ViewGroup textureViewContainer;
    public ViewGroup topContainer, bottomContainer;
    public LinearLayout mLlVolumeBright;
    public ImageView mIvVolumeBright;
    public ProgressBar mPbVolumeBright;
    public int widthRatio = 0;
    public int heightRatio = 0;
    public Object[] dataSourceObjects;  //这个参数原封不动直接通过JZMeidaManager传给JZMediaInterface。
    public int currentUrlMapIndex = 0;
    public int videoRotation = 0;
    protected int mScreenWidth;
    protected int mScreenHeight;
    protected AudioManager mAudioManager;
    protected Handler mHandler;
    protected ProgressTimerTask mProgressTimerTask;
    protected float mDownX;
    protected float mDownY;
    protected boolean mChangeVolume;
    protected boolean mChangeBrightness;
    protected int mGestureDownVolume;
    protected float mGestureDownBrightness;
    private boolean isPause;    //是否已切换到后台

    public AppCompatActivity mAttachActivity;  //关联的Activity
    public String mVideoNewsId;
    public static boolean isAlreadyPause;       //已经暂停

    public JZVideoPlayer(Context context) {
        super(context);
        init(context);
    }

    public JZVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public abstract int getLayoutId();

    public void init(Context context) {
        View.inflate(context, getLayoutId(), this);
        if (context instanceof AppCompatActivity) {
            mAttachActivity = (AppCompatActivity) context;
        } else {
            throw new IllegalArgumentException("Context must be AppCompatActivity");
        }
        fullscreenButton = (ImageView) findViewById(R.id.fullscreen);
        playPauseButton = (ImageView) findViewById(R.id.iv_play_pause);
        progressBar = (SeekBar) findViewById(R.id.bottom_seek_progress);
        mTvCurrentTime = (TextView) findViewById(R.id.current);
        mTvTotalTime = (TextView) findViewById(R.id.total);
        bottomContainer = (ViewGroup) findViewById(R.id.layout_bottom);
        textureViewContainer = (ViewGroup) findViewById(R.id.surface_container);
        topContainer = (ViewGroup) findViewById(R.id.layout_top);
        mLlVolumeBright = (LinearLayout) findViewById(R.id.ll_volume_bright);
        mIvVolumeBright = (ImageView) findViewById(R.id.iv_volume_bright);
        mPbVolumeBright = (ProgressBar) findViewById(R.id.pb_volume_bright);

        findViewById(R.id.rl_fullscreen).setOnClickListener(this);
        findViewById(R.id.rl_video_news_play_pause).setOnClickListener(this);
        progressBar.setOnSeekBarChangeListener(this);
        bottomContainer.setOnClickListener(this);
        textureViewContainer.setOnClickListener(this);
        textureViewContainer.setOnTouchListener(this);

        mScreenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getContext().getResources().getDisplayMetrics().heightPixels;
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mHandler = new Handler();

        sensorManager = (SensorManager) mAttachActivity.getSystemService(Context.SENSOR_SERVICE);
        sensorEventListener = new JZAutoFullscreenListener();
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    /**
     * 设置当前正在播放的新闻id
     *
     * @param videoNewsId
     */
    public void setVideoNewsId(String videoNewsId) {
        this.mVideoNewsId = videoNewsId;
    }

    public void setIsPause(boolean isPause) {
        this.isPause = isPause;
    }

    public boolean getIsPause() {
        return this.isPause;
    }

    /**
     * 开启语音唤醒
     */
    public void startWakeUp() {
        try {
            LogUtils.i("startWakeUp", "JZVideoPlayer");
            FloatViewInstance.startWakeUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 关闭音唤醒
     */
    public void stopWakeUp() {
        try {
            LogUtils.i("startWakeUp_stopWakeUp", "JZVideoPlayer");
            FloatViewInstance.stopWakeUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void releaseAllVideos() {
        if ((System.currentTimeMillis() - CLICK_QUIT_FULLSCREEN_TIME) > FULL_SCREEN_NORMAL_DELAY) {
            JZVideoPlayerManager.completeAll();
            JZMediaManager.instance().releaseMediaPlayer();
        }
    }

    public static boolean backPress() {
        LogUtils.i(TAG, "backPress");
        if (JZVideoPlayerManager.getSecondFloor() != null) {
            CLICK_QUIT_FULLSCREEN_TIME = System.currentTimeMillis();
            JZVideoPlayerManager.getFirstFloor().playOnThisJzvd();
            //TODO
//            if (JZUtils.dataSourceObjectsContainsUri(JZVideoPlayerManager.getFirstFloor().dataSourceObjects,
//                    JZMediaManager.getCurrentDataSource())) {
//
//            } else {
//                JZVideoPlayerManager.getFirstFloor().clearFloatScreen();
//                JZMediaManager.instance().releaseMediaPlayer();
//                JZVideoPlayerManager.completeAll();
//            }
//            //直接退出全屏和小窗
//            JZVideoPlayerManager.getFirstFloor().currentState = JZVideoPlayerManager.getSecondFloor().currentState;
//            JZVideoPlayerManager.getFirstFloor().currentUrlMapIndex = JZVideoPlayerManager.getSecondFloor().currentUrlMapIndex;
//            JZVideoPlayerManager.getFirstFloor().clearFloatScreen();
//            JZVideoPlayerManager.getFirstFloor().setState(JZVideoPlayerManager.getFirstFloor().currentState);
//            JZVideoPlayerManager.getFirstFloor().addTextureView();
            return true;
        }
        return false;
    }

    /**
     * 退出全屏和小窗
     */
    public void playOnThisJzvd() {
        //清空全屏和小窗的jzvd
        currentState = JZVideoPlayerManager.getSecondFloor().currentState;
        currentUrlMapIndex = JZVideoPlayerManager.getSecondFloor().currentUrlMapIndex;
        clearFloatScreen();

        //在本jzvd上播放
        setState(currentState);
        addTextureView();
        if (currentState != CURRENT_STATE_AUTO_COMPLETE) {
            registerSensorManager();
        }
        LogUtils.e("AAAAAAAAAAAAA playOnThisJzvd" + this);
        JZUtils.putIsClickFullScreen(false);
        RxBus.getInstance().send(new VideoNewsEvent.QuitFullScreenEvent());
    }

    public void clearFloatScreen() {
        JZUtils.setRequestedOrientation(getContext(), ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        showSupportActionBar(getContext());
        //TODO
//        JZVideoPlayer currJzvd = JZVideoPlayerManager.getCurrentJzvd();
//        currJzvd.textureViewContainer.removeView(JZMediaManager.textureView);

        ViewGroup vp = (ViewGroup) (JZUtils.scanForActivity(getContext())).findViewById(Window.ID_ANDROID_CONTENT);
//        vp.removeView(currJzvd);
        JZVideoPlayer floatJzvd = vp.findViewById(R.id.jz_fullscreen_id);
        if (floatJzvd != null) {
            vp.removeView(floatJzvd);
            if (floatJzvd.textureViewContainer != null)
                floatJzvd.textureViewContainer.removeView(JZMediaManager.textureView);
        }
        JZVideoPlayerManager.setSecondFloor(null);
    }

    @SuppressLint("RestrictedApi")
    public static void showSupportActionBar(Context context) {
        if (ACTION_BAR_EXIST && JZUtils.getAppCompActivity(context) != null) {
            ActionBar ab = JZUtils.getAppCompActivity(context).getSupportActionBar();
            if (ab != null) {
                ab.setShowHideAnimationEnabled(false);
                ab.show();
            }
        }
        if (TOOL_BAR_EXIST) {
            JZUtils.getWindow(context).clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @SuppressLint("RestrictedApi")
    public static void hideSupportActionBar(Context context) {
        if (ACTION_BAR_EXIST && JZUtils.getAppCompActivity(context) != null) {
            ActionBar ab = JZUtils.getAppCompActivity(context).getSupportActionBar();
            if (ab != null) {
                ab.setShowHideAnimationEnabled(false);
                ab.hide();
            }
        }
        if (TOOL_BAR_EXIST) {
            JZUtils.getWindow(context).setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public void setUp(String url, int screen, Object... objects) {
        LinkedHashMap map = new LinkedHashMap();
        map.put(URL_KEY_DEFAULT, url);
        Object[] dataSourceObjects = new Object[1];
        dataSourceObjects[0] = map;
        setUp(dataSourceObjects, 0, screen, objects);
    }

    public void setUp(Object[] dataSourceObjects, int defaultUrlMapIndex, int screen, Object... objects) {
        if (this.dataSourceObjects != null && JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex) != null &&
                JZUtils.getCurrentFromDataSource(this.dataSourceObjects, currentUrlMapIndex).
                        equals(JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex))) {
            return;
        }
        if (isCurrentJZVD() && JZUtils.dataSourceObjectsContainsUri(dataSourceObjects, JZMediaManager.getCurrentDataSource())) {
            long position = 0;
            try {
                position = JZMediaManager.getCurrentPosition();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            if (position != 0) {
                JZUtils.saveProgress(getContext(), JZMediaManager.getCurrentDataSource(), position);
            }
            JZMediaManager.instance().releaseMediaPlayer();
        }
        this.dataSourceObjects = dataSourceObjects;
        this.currentUrlMapIndex = defaultUrlMapIndex;
        this.currentScreen = screen;
        this.objects = objects;
        onStateNormal();
    }

    /**
     * 显示wifi弹框警告
     */
    public void showWifiWarnDialog() {
        if (TvDataTransform.getVideoNewsIsShowWifiDialog()) {
            if (null == networkDialog) {
                networkDialog = new NetworkWarnDialog(mAttachActivity, new NetworkWarnDialog.OnSelectListener() {
                    @Override
                    public void selectType(boolean confirm) {
                        if (confirm) {      //确定使用移动网络观看视频
                            onVideoRePlaying();
                            LogUtils.e("AAAAA showWifiWarnDialog");
                            TvDataTransform.putVideoNewsIsShowWifiDialog(false);
                        } else {
                            RxBus.getInstance().send(new VideoNewsEvent.CancelWifiWarnEvent());
                        }
                    }
                });
                onVideoPause();
                networkDialog.show();
            }
        } else {
            onVideoRePlaying();
            LogUtils.e("AAAAA showWifiWarnDialog false");
        }
    }

    /**
     * 显示wifi弹框警告
     */
    public void showWifiWarnDialogAfterClick(final int type) {
        if (null == networkDialog) {
            networkDialog = new NetworkWarnDialog(mAttachActivity, new NetworkWarnDialog.OnSelectListener() {
                @Override
                public void selectType(boolean confirm) {
                    if (confirm) {      //确定使用移动网络观看视频
                        switch (type) {
                            case 0:
                                onEvent(JZUserActionStandard.ON_CLICK_START_THUMB);
                                startVideo();
                                break;
                            case 1:
                                initTextureView();
                                addTextureView();
                                JZMediaManager.setDataSource(dataSourceObjects);
                                JZMediaManager.setCurrentDataSource(JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex));
                                onStatePreparing();
                                onEvent(JZUserAction.ON_CLICK_START_ERROR);
                                break;
                            case 2:
                                playPauseButton.setSelected(true);
                                isAlreadyPause = false;
                                startVideo();
                                onEvent(JZUserAction.ON_CLICK_START_ICON);
                                break;
                            case 3:
                                onEvent(JZUserAction.ON_CLICK_START_AUTO_COMPLETE);
                                startVideo();
                                playPauseButton.setSelected(true);
                                isAlreadyPause = false;
                                break;
                            case 4:
                                playPauseButton.setSelected(true);
                                isAlreadyPause = false;
                                startVideo();
                                onEvent(JZUserAction.ON_CLICK_START_ICON);
                                break;
                        }
                        TvDataTransform.putVideoNewsIsShowWifiDialog(false);
                        networkDialog.dismiss();
                        networkDialog = null;
                    } else {
                        RxBus.getInstance().send(new VideoNewsEvent.CancelWifiWarnEvent());
                    }
                }
            });
            networkDialog.show();
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.rl_already_look || i == R.id.rl_replay_layout) {   //已观看&重播
            if (!NetWorkUtils.isNetworkAvailable(mAttachActivity)) {
                showToast("网络连接断开");
                return;
            }

            if (currentState == CURRENT_STATE_NORMAL) {
                if (TvDataTransform.getVideoNewsIsShowWifiDialog() && NetWorkUtils.isMobileConnected(mAttachActivity)) {
                    showWifiWarnDialogAfterClick(2);
                } else {
                    playPauseButton.setSelected(true);
                    isAlreadyPause = false;
                    startVideo();
                    onEvent(JZUserAction.ON_CLICK_START_ICON);
                }
            } else if (currentState == CURRENT_STATE_AUTO_COMPLETE) {
                if (TvDataTransform.getVideoNewsIsShowWifiDialog() && NetWorkUtils.isMobileConnected(mAttachActivity)) {
                    showWifiWarnDialogAfterClick(3);
                } else {
                    onEvent(JZUserAction.ON_CLICK_START_AUTO_COMPLETE);
                    startVideo();
                    playPauseButton.setSelected(true);
                    isAlreadyPause = false;
                }
            } else if (currentState == CURRENT_STATE_PAUSE) {
                if (NetworkUtils.isMobileConnected(mAttachActivity)) {
                    showWifiWarnDialog();
                } else {
                    onVideoRePlaying();
                    LogUtils.e("AAAAA clikc");
                }
            }
        } else if (i == R.id.rl_fullscreen) {       //点击全屏按钮
            if (currentState == CURRENT_STATE_AUTO_COMPLETE) return;
            if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {    //回到竖屏
                backPress();
                if (currentState == CURRENT_STATE_PAUSE) {     //视频已暂停
                    startWakeUp();
                }
            } else {
                JZUtils.putIsClickFullScreen(true);
                onEvent(JZUserAction.ON_ENTER_FULLSCREEN);
                unregisterSensorManager();
                RxBus.getInstance().send(new VideoNewsEvent.ClickFullScreenEvent());
                stopWakeUp();
                JZUtils.setRequestedOrientation(getContext(), ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                startWindowFullscreen();
            }
        } else if (i == R.id.rl_video_news_play_pause) {   //播放&暂停
            if (!NetWorkUtils.isNetworkAvailable(mAttachActivity)) {
                showToast("网络连接断开");
                return;
            }

            if (currentState == CURRENT_STATE_NORMAL) {
                if (TvDataTransform.getVideoNewsIsShowWifiDialog() && NetWorkUtils.isMobileConnected(mAttachActivity)) {
                    showWifiWarnDialogAfterClick(4);
                } else {
                    playPauseButton.setSelected(true);
                    isAlreadyPause = false;
                    startVideo();
                    onEvent(JZUserAction.ON_CLICK_START_ICON);
                }
            } else if (currentState == CURRENT_STATE_PLAYING) {     //点击暂停
                onEvent(JZUserAction.ON_CLICK_PAUSE);
                onVideoPause();
                RxBus.getInstance().send(new VideoNewsEvent.PlayerClickPauseEvent());
            } else if (currentState == CURRENT_STATE_PAUSE) {   //点击恢复播放
                if (NetworkUtils.isMobileConnected(mAttachActivity)) {
                    showWifiWarnDialog();
                } else {
                    onVideoRePlaying();
                    LogUtils.e("AAAAA rl_video_news_play_pause");
                }
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        int id = v.getId();
        if (id == R.id.surface_container) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = x;
                    mDownY = y;
                    mChangeVolume = false;
                    mChangeBrightness = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float deltaX = x - mDownX;
                    float deltaY = y - mDownY;
                    float absDeltaX = Math.abs(deltaX);
                    float absDeltaY = Math.abs(deltaY);
                    if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
                        if (!mChangeVolume && !mChangeBrightness) {
                            if (absDeltaX > THRESHOLD || absDeltaY > THRESHOLD) {
                                if (absDeltaX < THRESHOLD) {
                                    int curWidth = JZUtils.getCurrentScreenLand(JZUtils.getAppCompActivity(getContext()))
                                            ? mScreenHeight : mScreenWidth;
                                    if (mDownX < curWidth * 0.5f) { //左侧改变亮度
                                        mChangeBrightness = true;
                                        WindowManager.LayoutParams lp = JZUtils.getWindow(getContext()).getAttributes();
                                        if (lp.screenBrightness < 0) {
                                            try {
                                                mGestureDownBrightness = Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
                                            } catch (Settings.SettingNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            mGestureDownBrightness = lp.screenBrightness * 255;
                                        }
                                    } else {//右侧改变声音
                                        mChangeVolume = true;
                                        mGestureDownVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                    }
                                }
                            }
                        }
                    }
                    if (mChangeVolume) {
                        deltaY = -deltaY;
                        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                        int deltaV = (int) (max * deltaY * 3 / mScreenHeight);
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                                mGestureDownVolume + deltaV, 0);
                        //dialog中显示百分比
                        int volumePercent = (int) (mGestureDownVolume * 100 / max + deltaY * 3 * 100 / mScreenHeight);
                        showVolumeDialog(-deltaY, volumePercent);
                    }

                    if (mChangeBrightness) {
                        deltaY = -deltaY;
                        int deltaV = (int) (255 * deltaY * 3 / mScreenHeight);
                        WindowManager.LayoutParams params = JZUtils.getWindow(getContext()).getAttributes();
                        if (((mGestureDownBrightness + deltaV) / 255) >= 1) {//这和声音有区别，必须自己过滤一下负值
                            params.screenBrightness = 1;
                        } else if (((mGestureDownBrightness + deltaV) / 255) <= 0) {
                            params.screenBrightness = 0.01f;
                        } else {
                            params.screenBrightness = (mGestureDownBrightness + deltaV) / 255;
                        }
                        JZUtils.getWindow(getContext()).setAttributes(params);
                        //dialog中显示百分比
                        int brightnessPercent = (int) (mGestureDownBrightness * 100 / 255 + deltaY * 3 * 100 / mScreenHeight);
                        showBrightnessDialog(brightnessPercent);
//                        mDownY = y;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    dismissVolumeDialog();
                    dismissBrightnessDialog();
                    if (mChangeVolume) {
                        onEvent(JZUserAction.ON_TOUCH_SCREEN_SEEK_VOLUME);
                    }
                    startProgressTimer();
                    break;
            }
        }
        return false;
    }

    public void startVideo() {
        JZVideoPlayerManager.completeAll();
        initTextureView();
        addTextureView();
        AudioManager mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        JZUtils.scanForActivity(getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        JZMediaManager.setDataSource(dataSourceObjects);
        JZMediaManager.setCurrentDataSource(JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex));
        onStatePreparing();
        JZVideoPlayerManager.setFirstFloor(this);
    }

    public void onPrepared() {
        if (seekToInAdvance != 0) {
            JZMediaManager.seekTo(seekToInAdvance);
            seekToInAdvance = 0;
        } else {
            long position = JZUtils.getSavedProgress(getContext(), JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex));
            if (position != 0) {
                JZMediaManager.seekTo(position);
            }
        }
        onStatePlaying();
        RxBus.getInstance().send(new VideoNewsEvent.PlayingEvent());
        stopWakeUp();
        registerNetworkReceiver();
        registerSensorManager();
        LogUtils.e("AAAAAAAAAAAAA onPrepared" + this);
    }

    public void setState(int state) {
        switch (state) {
            case CURRENT_STATE_NORMAL:
                onStateNormal();
                break;
            case CURRENT_STATE_PREPARING:
                onStatePreparing();
                break;
            case CURRENT_STATE_PREPARING_CHANGING_URL:
                onStatePreparingChangingUrl(0, seekToInAdvance);
                break;
            case CURRENT_STATE_PLAYING:
                onStatePlaying();
                break;
            case CURRENT_STATE_PAUSE:
                onStatePause();
                break;
            case CURRENT_STATE_ERROR:
                onStateError();
                break;
            case CURRENT_STATE_AUTO_COMPLETE:
                onStateAutoComplete();
                break;
        }
    }

    public void onStateNormal() {
        currentState = CURRENT_STATE_NORMAL;
        LogUtils.i(TAG, "onStateNormal_cancelProgressTimer");
        cancelProgressTimer();
        setPlayStateUI(currentState);
    }

    public void onStatePreparing() {
        currentState = CURRENT_STATE_PREPARING;
        resetProgressAndTime();
        setPlayStateUI(currentState);
    }

    public void onStatePreparingChangingUrl(int urlMapIndex, long seekToInAdvance) {
        currentState = CURRENT_STATE_PREPARING_CHANGING_URL;
        this.currentUrlMapIndex = urlMapIndex;
        this.seekToInAdvance = seekToInAdvance;
        JZMediaManager.setDataSource(dataSourceObjects);
        JZMediaManager.setCurrentDataSource(JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex));
        JZMediaManager.instance().prepare();
        setPlayStateUI(currentState);
    }

    public void onStatePlaying() {
        currentState = CURRENT_STATE_PLAYING;
        startProgressTimer();
        setPlayStateUI(currentState);
    }

    public void onStatePause() {
        currentState = CURRENT_STATE_PAUSE;
        startProgressTimer();
        setPlayStateUI(currentState);
    }

    public void onStateError() {
        currentState = CURRENT_STATE_ERROR;
        cancelProgressTimer();
        setPlayStateUI(currentState);
    }

    public void onStateAutoComplete() {
        currentState = CURRENT_STATE_AUTO_COMPLETE;
        cancelProgressTimer();
        progressBar.setProgress(100);
        mTvCurrentTime.setText(mTvTotalTime.getText());
        setPlayStateUI(currentState);
    }

    public void onInfo(int what, int extra) {
        LogUtils.i(TAG, "onInfo:" + what);
        switch (what) {
            case IMediaPlayer.MEDIA_INFO_BUFFERING_START: //缓冲开始：701
            case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH: //703
                currentState = CURRENT_STATE_PREPARING;
                setPlayStateUI(currentState);
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_END: //缓冲结束：702
                if (currentState == CURRENT_STATE_PAUSE || currentState == CURRENT_STATE_PREPARING) {
                    if (isPause) {
                        onVideoPause();
                    } else {
                        onVideoRePlaying();
                        LogUtils.e("AAAAA MEDIA_INFO_BUFFERING_END");
                    }
                }
                currentState = CURRENT_STATE_PLAYING;
                setPlayStateUI(currentState);
                break;
        }
    }

    /**
     * 设置播放状态UI
     *
     * @param state
     */
    public void setPlayStateUI(int state) {
        LogUtils.i(TAG, "state:" + state);
        switch (state) {
            case CURRENT_STATE_PREPARING:       //1加载中
                stopWakeUp();
                playPauseButton.setSelected(false);
                break;
            case CURRENT_STATE_NORMAL:          //0初始化状态
            case CURRENT_STATE_PAUSE:           //5暂停
            case CURRENT_STATE_AUTO_COMPLETE:   //6播放完成
            case CURRENT_STATE_ERROR:           //7播放错误
                playPauseButton.setSelected(false);
                break;
            case CURRENT_STATE_PLAYING:         //3播放中
                playPauseButton.setSelected(true);
                break;
        }
        boolean isLook = VideoNewsDbDao.isVideoLooked(mVideoNewsId, AppConfig.APP_USERID);
        mRlThumb.setVisibility(state == 0 ? (isLook ? View.GONE : View.VISIBLE) : View.GONE);
        mRlAlreadyLook.setVisibility(state == 0 ? (isLook ? View.VISIBLE : View.GONE) : View.GONE);
        mRlLoading.setVisibility(state == 1 ? View.VISIBLE : View.GONE);
        mRlReplay.setVisibility(state == 6 ? View.VISIBLE : View.GONE);
        mRlReload.setVisibility(state == 7 ? View.VISIBLE : View.GONE);
    }

    public void onError(int what, int extra) {
        if (what != 38 && extra != -38 && what != -38 && extra != 38 && extra != -19) {
            onStateError();
            if (isCurrentPlay()) {
                JZMediaManager.instance().releaseMediaPlayer();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        if (widthRatio != 0 && heightRatio != 0) {
            int specWidth = MeasureSpec.getSize(widthMeasureSpec);
            int specHeight = (int) ((specWidth * (float) heightRatio) / widthRatio);
            setMeasuredDimension(specWidth, specHeight);

            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(specWidth, MeasureSpec.EXACTLY);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(specHeight, MeasureSpec.EXACTLY);
            getChildAt(0).measure(childWidthMeasureSpec, childHeightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }

    public void onAutoCompletion() {
        Runtime.getRuntime().gc();
        onEvent(JZUserAction.ON_AUTO_COMPLETE);
        onStateAutoComplete();
        if (JZVideoPlayerManager.getSecondFloor() != null) {
            JZVideoPlayerManager.getSecondFloor().unregisterSensorManager();
        }
        if (JZVideoPlayerManager.getFirstFloor() != null) {
            JZVideoPlayerManager.getFirstFloor().unregisterSensorManager();
        }
        unregisterNetworkReceiver();
        if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {   //全屏
            stopWakeUp();
        } else {
            startWakeUp();
        }

        if (!TextUtils.isEmpty(mVideoNewsId)) {
            VideoNewsDbDao.addAlreadyLookVideoNews(mVideoNewsId);
        }
        RxBus.getInstance().send(new VideoNewsEvent.PlayCompleteEvent(true));
        JZUtils.saveProgress(getContext(), JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex),
                0);
    }

    public void onCompletion() {
        if (currentState == CURRENT_STATE_PLAYING || currentState == CURRENT_STATE_PAUSE) {
            long position = getCurrentPositionWhenPlaying();
            JZUtils.saveProgress(getContext(),
                    JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex), position);
        }
        unregisterSensorManager();
        unregisterNetworkReceiver();
        cancelProgressTimer();
        onStateNormal();
        textureViewContainer.removeView(JZMediaManager.textureView);
        JZMediaManager.instance().currentVideoWidth = 0;
        JZMediaManager.instance().currentVideoHeight = 0;

        AudioManager mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
        JZUtils.scanForActivity(getContext()).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        clearFullscreenLayout();
        JZUtils.setRequestedOrientation(getContext(), ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (JZMediaManager.surface != null) JZMediaManager.surface.release();
        if (JZMediaManager.savedSurfaceTexture != null)
            JZMediaManager.savedSurfaceTexture.release();
        JZMediaManager.textureView = null;
        JZMediaManager.savedSurfaceTexture = null;
    }

    public void release() {
        releaseAllVideos();
    }

    public void initTextureView() {
        removeTextureView();
        JZMediaManager.textureView = new JZResizeTextureView(getContext());
        JZMediaManager.textureView.setSurfaceTextureListener(JZMediaManager.instance());
    }

    public void addTextureView() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        textureViewContainer.addView(JZMediaManager.textureView, layoutParams);
    }

    public void removeTextureView() {
        JZMediaManager.savedSurfaceTexture = null;
        if (JZMediaManager.textureView != null && JZMediaManager.textureView.getParent() != null) {
            ((ViewGroup) JZMediaManager.textureView.getParent()).removeView(JZMediaManager.textureView);
        }
    }

    public void clearFullscreenLayout() {
        ViewGroup vp = (ViewGroup) (JZUtils.scanForActivity(getContext()))//.getWindow().getDecorView();
                .findViewById(Window.ID_ANDROID_CONTENT);
        View oldF = vp.findViewById(R.id.jz_fullscreen_id);
        if (oldF != null) {
            vp.removeView(oldF);
        }
        showSupportActionBar(getContext());
    }

    public void onVideoSizeChanged() {
        if (JZMediaManager.textureView != null) {
            if (videoRotation != 0) {
                JZMediaManager.textureView.setRotation(videoRotation);
            }
            JZMediaManager.textureView.setVideoSize(JZMediaManager.instance().currentVideoWidth, JZMediaManager.instance().currentVideoHeight);
        }
    }

    public void startProgressTimer() {
        cancelProgressTimer();
        mUpdateProgressTimer = new Timer();
        mProgressTimerTask = new ProgressTimerTask();
        mUpdateProgressTimer.schedule(mProgressTimerTask, 0, 300);
    }

    public void cancelProgressTimer() {
        if (mUpdateProgressTimer != null) {
            mUpdateProgressTimer.cancel();
        }
        if (mProgressTimerTask != null) {
            mProgressTimerTask.cancel();
        }
    }

    public void setProgressAndText(int progress, long position, long duration) {
        if (progress != 0) progressBar.setProgress(progress);
        if (position != 0) mTvCurrentTime.setText(JZUtils.stringForTime(position));
        mTvTotalTime.setText(JZUtils.stringForTime(duration));
    }

    public void setTimeText(long currentPos, long totalPos) {
        mTvCurrentTime.setText(JZUtils.stringForTime(currentPos));
        mTvTotalTime.setText(JZUtils.stringForTime(totalPos));
    }

    public void setBufferProgress(int bufferProgress) {
        if (bufferProgress != 0) progressBar.setSecondaryProgress(bufferProgress);
    }

    public void resetProgressAndTime() {
        progressBar.setProgress(0);
        progressBar.setSecondaryProgress(0);
        mTvCurrentTime.setText(JZUtils.stringForTime(0));
        mTvTotalTime.setText(JZUtils.stringForTime(0));
    }

    public long getCurrentPositionWhenPlaying() {
        long position = 0;
        //TODO 这块的判断应该根据MediaPlayer来
//        if (JZMediaManager.instance().mediaPlayer == null)
//            return position;//这行代码不应该在这，如果代码和逻辑万无一失的话，心头之恨呐
        if (currentState == CURRENT_STATE_PLAYING ||
                currentState == CURRENT_STATE_PAUSE) {
            try {
                position = JZMediaManager.getCurrentPosition();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                return position;
            }
        }
        return position;
    }

    public long getDuration() {
        long duration = 0;
        //TODO MediaPlayer 判空的问题
//        if (JZMediaManager.instance().mediaPlayer == null) return duration;
        try {
            duration = JZMediaManager.getDuration();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return duration;
        }
        return duration;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        cancelProgressTimer();
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        onEvent(JZUserAction.ON_SEEK_POSITION);
        startProgressTimer();
        if (currentState != CURRENT_STATE_PLAYING && currentState != CURRENT_STATE_PAUSE) return;
        long time = (long) ArithmeticUtils.div(seekBar.getProgress() * getDuration(), 100);
        JZMediaManager.seekTo(time);
        currentState = CURRENT_STATE_PREPARING;
        setPlayStateUI(currentState);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    //重力感应的时候调用的函数，
    public void autoFullscreen(float x) {
        if (isCurrentPlay() && currentScreen != SCREEN_WINDOW_FULLSCREEN) {
            if (x > 0) {
                JZUtils.setRequestedOrientation(getContext(), ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                JZUtils.setRequestedOrientation(getContext(), ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            }
            onEvent(JZUserAction.ON_ENTER_FULLSCREEN);
            stopWakeUp();
            startWindowFullscreen();
        }
    }

    public void autoQuitFullscreen() {
        if ((System.currentTimeMillis() - lastAutoFullscreenTime) > 2500 && isCurrentPlay()
                && currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            LogUtils.i(TAG, "autoQuitFullscreen:" + currentScreen);
            lastAutoFullscreenTime = System.currentTimeMillis();
            backPress();
            if (currentState == CURRENT_STATE_PAUSE || currentState == CURRENT_STATE_AUTO_COMPLETE) {  //视频已暂停
                startWakeUp();
            }
        }
    }

    public void startWindowFullscreen() {
        LogUtils.i(TAG, "startWindowFullscreen:" + currentScreen);
        JZUtils.putLastFullScreenTime(System.currentTimeMillis());
        hideSupportActionBar(getContext());

        ViewGroup vp = (ViewGroup) (JZUtils.scanForActivity(getContext()))
                .findViewById(Window.ID_ANDROID_CONTENT);
        View old = vp.findViewById(R.id.jz_fullscreen_id);
        if (old != null) {
            vp.removeView(old);
        }
        textureViewContainer.removeView(JZMediaManager.textureView);
        try {
            Constructor<JZVideoPlayer> constructor = (Constructor<JZVideoPlayer>) JZVideoPlayer.this.getClass().getConstructor(Context.class);
            JZVideoPlayer jzVideoPlayer = constructor.newInstance(getContext());
            jzVideoPlayer.mVideoNewsId = this.mVideoNewsId;
            jzVideoPlayer.setId(R.id.jz_fullscreen_id);
            LayoutParams lp = new LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            vp.addView(jzVideoPlayer, lp);
            jzVideoPlayer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN);
            jzVideoPlayer.setUp(dataSourceObjects, currentUrlMapIndex, JZVideoPlayerStandard.SCREEN_WINDOW_FULLSCREEN, objects);
            jzVideoPlayer.setState(currentState);
            jzVideoPlayer.addTextureView();
            JZVideoPlayerManager.setSecondFloor(jzVideoPlayer);
            onStateNormal();
            jzVideoPlayer.progressBar.setSecondaryProgress(progressBar.getSecondaryProgress());
            jzVideoPlayer.startProgressTimer();
            CLICK_QUIT_FULLSCREEN_TIME = System.currentTimeMillis();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isCurrentPlay() {
        return isCurrentJZVD()
                && JZUtils.dataSourceObjectsContainsUri(dataSourceObjects, JZMediaManager.getCurrentDataSource());//不仅正在播放的url不能一样，并且各个清晰度也不能一样
    }

    public boolean isCurrentJZVD() {
        return JZVideoPlayerManager.getCurrentJzvd() != null
                && JZVideoPlayerManager.getCurrentJzvd() == this;
    }

    public void onEvent(int type) {
        if (jzUserAction != null && isCurrentPlay() && dataSourceObjects != null) {
            jzUserAction.onEvent(type, JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex), currentScreen, objects);
        }
    }

    public static void setMediaInterface(JZMediaInterface mediaInterface) {
        JZMediaManager.instance().jzMediaInterface = mediaInterface;
    }

    public void onSeekComplete() {
    }

    public void showVolumeDialog(float deltaY, int volumePercent) {
    }

    public void dismissVolumeDialog() {
    }

    public void showBrightnessDialog(int brightnessPercent) {
    }

    public void dismissBrightnessDialog() {
    }

    protected NetworkWarnDialog networkDialog;

    private NetworkConnectChangedReceiver networkReceiver;

    private void registerNetworkReceiver() {
        synchronized (JZVideoPlayer.class) {
            try {
                if (networkReceiver == null) {
                    networkReceiver = new NetworkConnectChangedReceiver();
                    IntentFilter filter = new IntentFilter();
                    filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
                    filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
                    mAttachActivity.registerReceiver(networkReceiver, filter);
                }
            } catch (Exception e) {
                LogUtils.e(e);
            }
        }
    }

    public void unregisterNetworkReceiver() {
        synchronized (JZVideoPlayer.class) {
            try {
                if (null != networkReceiver) {
                    mAttachActivity.unregisterReceiver(networkReceiver);
                    networkReceiver = null;
                }
            } catch (Exception e) {
                LogUtils.e(e);
            }
        }
    }

    /**
     * 网络状态监听
     */
    public class NetworkConnectChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                boolean isConnected = NetWorkUtils.isNetworkAvailable(mAttachActivity);
                setNetConnectedEvent(isConnected);
            }

            if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
                if (NetworkUtils.isMobileConnected(mAttachActivity)) {      //移动数据连接上时
                    showWifiWarnDialog();
                }
            }

            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction()) && ConnectivityManager.TYPE_WIFI == NetworkUtils.getNetWorkType(mAttachActivity)) {   //wifi连接上时
                onVideoRePlaying();
                LogUtils.e("AAAAA TYPE_WIFI");
            }
        }
    }

    public void setNetConnectedEvent(boolean isConnected) {
        if (isConnected) {      //网路连接上
            progressBar.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
            progressBar.setOnSeekBarChangeListener(this);
        } else {                //网络断开
            progressBar.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            onVideoPause();
            setPlayStateUI(7);
        }
    }

    /**
     * 暂停播放视频
     */
    public void onVideoPause() {
        LogUtils.i(TAG, "onVideoPause");
        if (JZVideoPlayerManager.getCurrentJzvd() != null) {
            long position = getCurrentPositionWhenPlaying();
            JZUtils.saveProgress(getContext(),
                    JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex), position);
        }
        if (JZVideoPlayerManager.getSecondFloor() != null) {
            JZVideoPlayerManager.getSecondFloor().unregisterSensorManager();
        }
        if (JZVideoPlayerManager.getFirstFloor() != null) {
            JZVideoPlayerManager.getFirstFloor().unregisterSensorManager();
        }
        unregisterNetworkReceiver();
        if (currentState == CURRENT_STATE_PLAYING) {
            JZMediaManager.pause();
            onStatePause();
        } else {
            if (JZVideoPlayerManager.getCurrentJzvd() != null) {
                JZVideoPlayer jzvd = JZVideoPlayerManager.getCurrentJzvd();
                if (jzvd.currentState == JZVideoPlayer.CURRENT_STATE_AUTO_COMPLETE ||
                        jzvd.currentState == JZVideoPlayer.CURRENT_STATE_NORMAL ||
                        jzvd.currentState == JZVideoPlayer.CURRENT_STATE_ERROR) {
                    JZVideoPlayer.releaseAllVideos();
                } else {
                    jzvd.onStatePause();
                    JZMediaManager.pause();
                }
            }
        }
        currentState = CURRENT_STATE_PAUSE;
        isAlreadyPause = true;
        playPauseButton.setSelected(false);
        if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {   //全屏
            stopWakeUp();
        } else {
            startWakeUp();
        }
    }

    /**
     * 重新播放视频
     */
    public void onVideoRePlaying() {
        if (isPause) {
            onVideoPause();
            unregisterSensorManager();
            return;
        }
        if (JZVideoPlayerManager.getSecondFloor() != null) {
            JZVideoPlayerManager.getSecondFloor().registerSensorManager();
            LogUtils.e("AAAAAAAAAAAAA onVideoRePlaying" + this);
        }
        if (JZVideoPlayerManager.getFirstFloor() != null) {
            JZVideoPlayerManager.getFirstFloor().registerSensorManager();
            LogUtils.e("AAAAAAAAAAAAA onVideoRePlaying" + this);
        }
        registerNetworkReceiver();
        if (JZVideoPlayerManager.getCurrentJzvd() != null && !isPause) {
            if (null != networkDialog && networkDialog.isShowing()) {
                networkDialog.dismiss();
                networkDialog = null;
            }
            currentState = CURRENT_STATE_PLAYING;
            JZMediaManager.start();
            onStatePlaying();
            isAlreadyPause = false;
            onEvent(JZUserAction.ON_CLICK_RESUME);
            RxBus.getInstance().send(new VideoNewsEvent.PlayingEvent());
            stopWakeUp();
            playPauseButton.setSelected(true);
        }
    }

    public void showToast(String msg) {
        Toast.makeText(mAttachActivity, msg, Toast.LENGTH_SHORT).show();
    }

    public class ProgressTimerTask extends TimerTask {
        @Override
        public void run() {
            if (currentState == CURRENT_STATE_PLAYING
                    || currentState == CURRENT_STATE_PAUSE) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        long position = getCurrentPositionWhenPlaying();
                        long duration = getDuration();
                        int progress = (int) (position * 100 / (duration == 0 ? 1 : duration));
                        setProgressAndText(progress, position, duration);
                    }
                });
            }
        }
    }

    private SensorManager sensorManager;
    private JZAutoFullscreenListener sensorEventListener;
    private Sensor accelerometerSensor;
    Disposable registerSensorManager;

    public void registerSensorManager() {
        LogUtils.e("AAAAAAAAAAAAA registerSensorManager" + this);
        if (null != sensorManager) {
            if (!JZUtils.getIsClickFullScreen()) {
                if (null != sensorManager) {
                    sensorManager.unregisterListener(sensorEventListener);
                }
                if (registerSensorManager != null && !registerSensorManager.isDisposed()) {
                    registerSensorManager.dispose();
                }
                registerSensorManager = Observable.timer(2500, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                LogUtils.i(TAG, "registerSensorManager");
                                JZUtils.putIsClickFullScreen(false);
                                sensorManager.registerListener(sensorEventListener, accelerometerSensor,
                                        SensorManager.SENSOR_DELAY_NORMAL);
                            }
                        });
            }
        }
    }

    public void unregisterSensorManager() {
        if (registerSensorManager != null && !registerSensorManager.isDisposed()) {
            registerSensorManager.dispose();
        }
        LogUtils.e("AAAAAAAAAAAAA unregisterSensorManager" + this);
        if (null != sensorManager) {
            LogUtils.i(TAG, "unregisterSensorManager:" + currentScreen);
            sensorManager.unregisterListener(sensorEventListener);
        }
    }

    public class JZAutoFullscreenListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {//可以得到传感器实时测量出来的变化值
            final float x = event.values[SensorManager.DATA_X];
            float y = event.values[SensorManager.DATA_Y];

            LogUtils.e("AAAAAAAAAA JZVideoPlayer:" + JZVideoPlayer.this + "JZAutoFullscreenListener" + this);
            if (((x > -12 && x < -6) || (x < 13 && x > 8)) && (Math.abs(y) < 2.5)) {
                if ((System.currentTimeMillis() - JZUtils.getLastFullScreenTime()) > 3000) {
                    if (JZVideoPlayerManager.getFirstFloorJzvd() != null) {
                        if (JZVideoPlayerManager.getFirstFloorJzvd() == JZVideoPlayer.this) {
                            JZVideoPlayer jzVideoPlayer = JZVideoPlayerManager.getCurrentJzvd();
                            if (jzVideoPlayer.currentState == NewsConstants.CURRENT_STATE_PLAYING) {
                                if (jzVideoPlayer.currentScreen == NewsConstants.SCREEN_WINDOW_FULLSCREEN) {
                                    return;
                                }
                                JZVideoPlayerManager.getFirstFloorJzvd().onEvent(JZUserAction.ON_ENTER_FULLSCREEN);
                                JZVideoPlayerManager.getFirstFloorJzvd().autoFullscreen(x);
                            }
                        } else {
                            unregisterSensorManager();
                        }
                    }
                }
            } else if (((x < 3 && x > 0.2) || (x > -3 && x < -0.2)) && (Math.abs(y) > 5)) {
                if (JZVideoPlayerManager.getSecondFloor() != null) {
                    if (JZVideoPlayerManager.getFirstFloorJzvd() == JZVideoPlayer.this) {
                        JZVideoPlayer jzVideoPlayer = JZVideoPlayerManager.getCurrentJzvd();
                        if (jzVideoPlayer.currentScreen == NewsConstants.SCREEN_WINDOW_LIST
                                || jzVideoPlayer.currentScreen == NewsConstants.SCREEN_WINDOW_DEFAULT) {
                            return;
                        }
                        JZVideoPlayerManager.getSecondFloor().autoQuitFullscreen();
                    } else {
                        unregisterSensorManager();
                    }
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }
}
