package com.readyidu.robot.component.ijk.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.readyidu.robot.R;
import com.readyidu.robot.component.ijk.base.MediaPlayerParams;
import com.readyidu.robot.component.ijk.base.MediaQualityInfo;
import com.readyidu.robot.component.ijk.listener.VideoAllCallBack;
import com.readyidu.robot.component.ijk.utils.NetWorkUtils;
import com.readyidu.robot.component.ijk.utils.WindowUtils;
import com.readyidu.robot.component.music.utils.PlayerUtils;
import com.readyidu.robot.component.rxbus.RxBus;
import com.readyidu.robot.event.PlayCompleteEvent;
import com.readyidu.robot.event.tv.TvEvent;
import com.readyidu.robot.model.business.tv.CommonChannel;
import com.readyidu.robot.model.business.tv.TvChannel;
import com.readyidu.robot.model.business.tv.TvSourceDetail;
import com.readyidu.robot.ui.tv.adapter.land.SourceAdapter;
import com.readyidu.robot.ui.tv.adapter.land.TvChannelLandAdapter;
import com.readyidu.robot.ui.tv.adapter.land.TvClassifyLandAdapter;
import com.readyidu.robot.ui.tv.adapter.land.TvClassifySecondaryLandAdapter;
import com.readyidu.robot.ui.tv.utils.TvDataTransform;
import com.readyidu.robot.utils.log.LogUtils;
import com.readyidu.robot.utils.view.MeasureUtil;
import com.readyidu.robot.utils.view.ToastUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static android.widget.SeekBar.OnSeekBarChangeListener;
import static tv.danmaku.ijk.media.player.IMediaPlayer.OnInfoListener;

/**
 * @author Wlq
 * @description 视频播放组件
 * @date 2017/11/25 下午5:44
 */
public class IjkPlayerView extends FrameLayout implements View.OnClickListener {

    // 进度条最大值
    private static final int MAX_VIDEO_SEEK = 1000;
    // 默认隐藏控制栏时间
    private static final int DEFAULT_HIDE_TIMEOUT = 10 * 1000;
    // 更新进度消息
    private static final int MSG_UPDATE_SEEK = 10086;
    // 尝试重连消息
    private static final int MSG_TRY_RELOAD = 10088;
    // 无效变量
    private static final int INVALID_VALUE = -1;

    // 原生的IjkPlayer
    private IjkVideoView mVideoView;
    // 加载
    private View mRlLoading;
    private ImageView mIvRePlay;
    private ProgressBar mPbLoading;
    private TextView mTvRePlay;
    // 全屏下的标题
    private TextView mTvTitle;
    // 窗口模式的TopBar
    private RelativeLayout mWindowTopBar;
    // 全屏下的TopBar
    private RelativeLayout mFullscreenTopBar;
    // 播放键
    private ImageView mIvPlay;
    // 当前时间
    private TextView mTvCurTime;
    // 进度条
    private SeekBar mPlayerSeek;
    // 结束时间
    private TextView mTvEndTime;
    // 全屏切换按钮
    private ImageView mIvFullscreen;
    // BottomBar
    private LinearLayout mLlBottomBar;
    // 整个视频框架布局
    private FrameLayout mFlVideoBox;
    // 关联的Activity
    private AppCompatActivity mAttachActivity;

    private TextView mTvChangeSource;

    private LinearLayout mLlSeekBar;

    public boolean mIsNetConnected; //网络是否可用

    private View mFastForwardLayout;    //快进、快退view

    // 音量控制
    private AudioManager mAudioManager;
    //视频焦点判断辅助参数
    private boolean isTvPlaying;
    // 是否显示控制栏
    private boolean mIsShowBar = true;
    // 是否全屏
    private boolean mIsFullscreen;
    // 是否播放结束
    private boolean mIsPlayComplete = false;
    // 是否正在拖拽进度条
    private boolean mIsSeeking;
    // 目标进度
    private long mTargetPosition = INVALID_VALUE;
    // 当前进度
    private int mCurPosition = INVALID_VALUE;
    // 初始高度
    private int mInitHeight;
    // 屏幕宽/高度
    private int mWidthPixels;
    // 屏幕UI可见性
    private int mScreenUiVisibility;
    // 进来还未播放
    private boolean mIsNeverPlay = true;
    // 记录按退出全屏时间
    private long mExitTime = 0;
    // 异常中断时的播放进度
    private int mInterruptPosition;
    private boolean mIsReady = false;
    private int videoType;      //播放器类型
    private boolean isMovie;    //是否为点播电影
    private boolean isReplay;   //是否重播
    private boolean netDiscontinue;
    private boolean noSource;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_UPDATE_SEEK) {
                if (mVideoView.isPlaying()) {
                    final int pos = _setProgress();
                    int max = mVideoView.getDuration();
                    currentPosition = mVideoView.getCurrentPosition();

                    if (max > currentPosition || !isMovie) {
                        if (!mIsSeeking) {
                            // 这里会重复发送MSG，已达到实时更新 Seek 的效果
                            msg = obtainMessage(MSG_UPDATE_SEEK);
                            sendMessageDelayed(msg, 1000 - (pos % 1000));
                        }
                    } else {
                        onComplete();
                    }
                }
            } else if (msg.what == MSG_TRY_RELOAD) {
                if (mIsNetConnected) {
                    reload();
                }
                msg = obtainMessage(MSG_TRY_RELOAD);
                sendMessageDelayed(msg, 3000);
            }
        }
    };

    private VideoAllCallBack mVideoAllCallBack;
    private TextView txtShowList;
    private View mVolumeAndBrightTipView;   //音量&亮度教学页

    private ViewGroup llTvPlayLandSelect;
    private RecyclerView rvTvLandClassify;
    private RecyclerView rvTvLandChannel;
    private TvClassifyLandAdapter classifyLandAdapter;
    private TvChannelLandAdapter tvChannelLandAdapter;
    private RecyclerView recyclerSource;
    private SourceAdapter sourceAdapter;
    private ImageView imgProgress;
    private ProgressBar mProgress;
    private View containerProgress;
    private RecyclerView rvTvLandClassifySecondary;
    private View containerTypeSwitch;
    private TvClassifySecondaryLandAdapter classifySecondLandAdapter;
    private TextView txtLive;
    private TextView txtMovie;
    private View container_loading;
    private View txt_empty;
    private View loading_list;
    private View txtTip;
    private View txtReload;
    private View rl_reload_replay;

    public IjkPlayerView(Context context) {
        this(context, null);
    }

    public IjkPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        _initView(context);
    }

    private void _initView(Context context) {
        if (context instanceof AppCompatActivity) {
            mAttachActivity = (AppCompatActivity) context;
        } else {
            throw new IllegalArgumentException("Context must be AppCompatActivity");
        }
        View.inflate(context, R.layout.layout_player_view, this);
        mVideoView = (IjkVideoView) findViewById(R.id.video_view);
        rl_reload_replay = findViewById(R.id.rl_reload_replay);
        mRlLoading = findViewById(R.id.fl_loading);
        mTvRePlay = (TextView) findViewById(R.id.tv_reload_replay);
        mIvRePlay = (ImageView) findViewById(R.id.iv_reload_replay);
        mPbLoading = (ProgressBar) findViewById(R.id.tv_pb_loading);
        ImageView mIvBack = (ImageView) findViewById(R.id.iv_back);
        FrameLayout mFlLayout = (FrameLayout) findViewById(R.id.fl_left);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvTitle.setText("暂无节目信息");
        mFullscreenTopBar = (RelativeLayout) findViewById(R.id.fullscreen_top_bar);
        View mIvBackWindow = findViewById(R.id.fl_left_window);
        mWindowTopBar = (RelativeLayout) findViewById(R.id.window_top_bar);
        mIvPlay = (ImageView) findViewById(R.id.iv_play);
        mTvCurTime = (TextView) findViewById(R.id.tv_cur_time);
        mPlayerSeek = (SeekBar) findViewById(R.id.player_seek);
        mTvEndTime = (TextView) findViewById(R.id.tv_end_time);
        mIvFullscreen = (ImageView) findViewById(R.id.iv_fullscreen);
        mLlBottomBar = (LinearLayout) findViewById(R.id.ll_bottom_bar);
        mFlVideoBox = (FrameLayout) findViewById(R.id.fl_video_box);
        mTvChangeSource = (TextView) findViewById(R.id.land_change_source);
        mLlSeekBar = (LinearLayout) findViewById(R.id.ll_video_play_seek_bar);
        txtShowList = (TextView) findViewById(R.id.txt_show_list);


        // FIXME: 2017/11/29 列表控件初始化
        {
            llTvPlayLandSelect = (ViewGroup) findViewById(R.id.ll_tv_play_land_select);

            rvTvLandClassify = (RecyclerView) findViewById(R.id.rv_tv_land_classify);
            rvTvLandClassify.setLayoutManager(new LinearLayoutManager(mAttachActivity));
            classifyLandAdapter = new TvClassifyLandAdapter(mAttachActivity, TvDataTransform.getmTvTypes());
            rvTvLandClassify.setAdapter(classifyLandAdapter);

            container_loading = findViewById(R.id.container_loading);
            txt_empty = findViewById(R.id.txt_empty);
            loading_list = findViewById(R.id.loading_list);
            containerTypeSwitch = findViewById(R.id.container_type_switch);
            txtLive = (TextView) findViewById(R.id.txt_live);
            txtMovie = (TextView) findViewById(R.id.txt_movie);
            txtLive.setOnClickListener(this);
            txtMovie.setOnClickListener(this);

            rvTvLandClassifySecondary = (RecyclerView) findViewById(R.id.rv_tv_land_classify_secondary);
            rvTvLandClassifySecondary.setLayoutManager(new LinearLayoutManager(mAttachActivity));
            classifySecondLandAdapter = new TvClassifySecondaryLandAdapter(mAttachActivity, TvDataTransform.getClassifyChannelSecondary());
            rvTvLandClassifySecondary.setAdapter(classifySecondLandAdapter);

            rvTvLandChannel = (RecyclerView) findViewById(R.id.rv_tv_land_channel);
            rvTvLandChannel.setLayoutManager(new LinearLayoutManager(mAttachActivity));
            tvChannelLandAdapter = new TvChannelLandAdapter(mAttachActivity, TvDataTransform.getCurrentTvChannels());
            rvTvLandChannel.setAdapter(tvChannelLandAdapter);


        }

        // FIXME: 2017/12/4 切源列表控件初始化
        {
            recyclerSource = (RecyclerView) findViewById(R.id.recycler_source);
            recyclerSource.setLayoutManager(new LinearLayoutManager(mAttachActivity));
            sourceAdapter = new SourceAdapter(mAttachActivity, TvDataTransform.getmTvSources());
            recyclerSource.setAdapter(sourceAdapter);
        }


        {
            containerProgress = findViewById(R.id.container_progress);
            imgProgress = (ImageView) findViewById(R.id.img_progress);
            mProgress = (ProgressBar) findViewById(R.id.progress);
        }
        //加载中  播放错误
        {
            txtReload = findViewById(R.id.txt_reload);
            txtTip = findViewById(R.id.txt_tip);
            txtReload.setOnClickListener(this);
        }

        if (mIsFullscreen) {
            mFullscreenTopBar.setVisibility(View.VISIBLE);
            mWindowTopBar.setVisibility(View.GONE);
        } else {
            mFullscreenTopBar.setVisibility(View.GONE);
            mWindowTopBar.setVisibility(View.VISIBLE);
        }
        setTitleParams();

        _initMediaQuality();
        mIvPlay.setOnClickListener(this);
        mIvBack.setOnClickListener(this);
        mFlLayout.setOnClickListener(this);
        mIvFullscreen.setOnClickListener(this);
        mIvBackWindow.setOnClickListener(this);
        mTvChangeSource.setOnClickListener(this);
        txtShowList.setOnClickListener(this);
        mRlLoading.setOnClickListener(this);

        mFlVideoBox.setOnTouchListener(mPlayerTouchListener);
        _hideAllView();
    }

    /**
     * 设置标题位置样式
     */
    private void setTitleParams() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mTvTitle.getLayoutParams();
        if (isFullscreen()) {
            layoutParams.setMargins(MeasureUtil.dip2px(
                    mAttachActivity, 40),
                    0,
                    MeasureUtil.dip2px(mAttachActivity, 15),
                    0);
        } else {
            layoutParams.setMargins(MeasureUtil.dip2px(
                    mAttachActivity, 40),
                    MeasureUtil.dip2px(mAttachActivity, 23),
                    MeasureUtil.dip2px(mAttachActivity, 15),
                    0);
        }
        mTvTitle.setLayoutParams(layoutParams);
    }

    /**
     * 初始化
     */
    private void _initMediaPlayer() {

        // 声音
        mAudioManager = (AudioManager) mAttachActivity.getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // 进度
        mPlayerSeek.setMax(MAX_VIDEO_SEEK);
        mPlayerSeek.setOnSeekBarChangeListener(mSeekListener);
        // 视频监听
        mVideoView.setOnInfoListener(mInfoListener);
        mVideoView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                LogUtils.i("onError:" + i + ":" + i1);
                return false;
            }
        });
        // 触摸控制
        mGestureDetector = new GestureDetector(mAttachActivity, mPlayerGestureListener);
        mFlVideoBox.setClickable(true);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mInitHeight == 0) {
            mInitHeight = getHeight();
            mWidthPixels = getResources().getDisplayMetrics().widthPixels;
        }
    }

    public void setVideoCallBack(VideoAllCallBack videoCallBack) {
        this.mVideoAllCallBack = videoCallBack;
    }

    /**============================ 外部调用接口 ============================*/

    /**
     * Activity.onResume() 里调用
     */
    public void onResume() {
        mVideoView.resume();
        mIvPlay.setSelected(true);
    }

    /**
     * Activity.onPause() 里调用
     */
    public void onPause() {
        mCurPosition = mVideoView.getCurrentPosition();
        mVideoView.pause();
        mIvPlay.setSelected(false);
    }

    /**
     * Activity.onDestroy() 里调用
     *
     * @return 返回播放进度
     */
    public int onDestroy() {
        // 记录播放进度
        int curPosition = mVideoView.getCurrentPosition();
        mVideoView.destroy();
        IjkMediaPlayer.native_profileEnd();
        mHandler.removeMessages(MSG_TRY_RELOAD);
        mHandler.removeMessages(MSG_UPDATE_SEEK);

        // 关闭屏幕常亮
        mAttachActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        return curPosition;
    }

    public void setNoSource(boolean noSource) {
        this.noSource = noSource;
    }

    /**
     * 回退，全屏时退回竖屏
     */
    public boolean onBackPressed() {
        if (mIsFullscreen) {
            LogUtils.i("点击返回键退出全屏");
            mAttachActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            return true;
        }
        return false;
    }

    /**
     * 设置播放器类型
     *
     * @param playerType
     */
    public IjkPlayerView setPlayerType(int playerType) {

        this.videoType = playerType;
        return this;
    }

    /**
     * 初始化，必须要先调用
     */
    public IjkPlayerView init() {
        _initMediaPlayer();
        return this;
    }

    /**
     * 切换视频
     *
     * @param url
     */
    public IjkPlayerView switchVideoPath(String url) {
        return switchVideoPath(Uri.parse(url), videoType);
    }

    /**
     * 切换视频
     *
     * @param uri
     */
    public IjkPlayerView switchVideoPath(Uri uri, int videoType) {
        if (mQualityData != null) {
            mQualityData.clear();
            mQualityData = null;
        }
        reset();
        return setVideoPath(uri, videoType);
    }

    /**
     * 设置播放资源
     *
     * @param url
     */
    public IjkPlayerView setVideoPath(String url, int videoType) {
        return setVideoPath(Uri.parse(url), videoType);
    }

    /**
     * 设置播放资源
     *
     * @param url
     */
    public IjkPlayerView setVideoPath(String url, Map<String, String> headers, int videoType) {
        return setVideoPath(Uri.parse(url), headers, videoType);
    }

    /**
     * 设置播放资源
     *
     * @param uri
     */
    public IjkPlayerView setVideoPath(Uri uri, int videoType) {
        mVideoView.setVideoURI(uri, videoType);
        if (mCurPosition != INVALID_VALUE) {
            seekTo(mCurPosition);
            mCurPosition = INVALID_VALUE;
        } else {
            seekTo(0);
        }
        return this;
    }

    /**
     * 设置播放资源
     *
     * @param uri
     */
    public IjkPlayerView setVideoPath(Uri uri, Map<String, String> headers, int videoType) {
        mVideoView.setVideoURI(uri, headers, videoType);
        return this;
    }

    /**
     * 设置标题，全屏的时候可见
     *
     * @param title
     */
    public IjkPlayerView setTitle(String title) {
//        mTvTitle.setText(title);
        return this;
    }

    /**
     * 设置是否为点播
     *
     * @param isMovie
     */
    public IjkPlayerView setIsMovie(boolean isMovie) {
        this.isMovie = isMovie;
        if (isMovie) {
            mLlSeekBar.setVisibility(View.VISIBLE);
        } else {
            mLlSeekBar.setVisibility(View.INVISIBLE);
        }

        return this;
    }

    public boolean isMovie() {
        return isMovie;
    }

    /**
     * 设置横竖屏  播放控件UI
     */
    public IjkPlayerView setPlayerControlUI() {
        if (mIsFullscreen) {    //横屏
            if (isMovie) {  //点播
                mLlSeekBar.setVisibility(View.VISIBLE);
                mIvFullscreen.setVisibility(View.VISIBLE);
//                mTvChangeSource.setVisibility(TvDataTransform.isSitcomDemand() ? View.GONE : View.VISIBLE);
                txtShowList.setVisibility(View.VISIBLE);
            } else {    //直播
                mLlSeekBar.setVisibility(View.INVISIBLE);
                mIvFullscreen.setVisibility(View.VISIBLE);
//                mTvChangeSource.setVisibility(View.VISIBLE);
                txtShowList.setVisibility(View.VISIBLE);
            }
            mTvChangeSource.setVisibility(TvDataTransform.isSitcomDemand() ? View.GONE : View.VISIBLE);
        } else {    //竖屏
            if (isMovie) {
                mLlBottomBar.setVisibility(View.VISIBLE);
                mLlSeekBar.setVisibility(View.VISIBLE);
//                mTvChangeSource.setVisibility(TvDataTransform.isSitcomDemand() ? View.GONE : View.VISIBLE);
                txtShowList.setVisibility(View.GONE);
            } else {
                mLlBottomBar.setVisibility(View.VISIBLE);
                mLlSeekBar.setVisibility(View.INVISIBLE);
//                mTvChangeSource.setVisibility(View.VISIBLE);
                txtShowList.setVisibility(View.GONE);
            }
            mTvChangeSource.setVisibility(TvDataTransform.isSitcomDemand() ? View.GONE : View.VISIBLE);
            mFullscreenTopBar.setVisibility(View.GONE);
            mIvFullscreen.setVisibility(View.VISIBLE);
        }
        return this;
    }

    /**
     * 设置视频加载状态
     */
    public IjkPlayerView setLoadingPlayUI(boolean isReload) {
        // FIXME: 2017/12/5 视频播放错误
        rl_reload_replay.setVisibility(View.VISIBLE);
        mRlLoading.setEnabled(true);
        if (mIsNetConnected) {
            if (isReload) { //重新加载
                mTvRePlay.setText("该频道暂时无法播放，请稍后再试");
                mIvRePlay.setVisibility(View.GONE);
                txtReload.setVisibility(VISIBLE);
                txtTip.setVisibility(GONE);
                mPbLoading.setVisibility(View.GONE);
            } else {    //重播
                mTvRePlay.setText("重播");
                mPbLoading.setVisibility(View.GONE);
                mIvRePlay.setVisibility(View.VISIBLE);
                txtReload.setVisibility(GONE);
                txtTip.setVisibility(GONE);
                mIvRePlay.setBackgroundResource(R.drawable.ic_replay);
            }
        } else {
            mTvRePlay.setText("该频道暂时无法播放，请稍后再试");
            mIvRePlay.setVisibility(View.GONE);
            txtReload.setVisibility(VISIBLE);
            txtTip.setVisibility(GONE);
            mPbLoading.setVisibility(View.VISIBLE);
        }
        return this;
    }

    /**
     * 视频开始loading
     */
    public IjkPlayerView startPlayLoading() {
        mRlLoading.setEnabled(false);
        rl_reload_replay.setVisibility(View.VISIBLE);
        mIvRePlay.setVisibility(View.GONE);
        mPbLoading.setVisibility(View.VISIBLE);
        mTvRePlay.setText("努力加载中...");
        txtTip.setVisibility(VISIBLE);
        txtReload.setVisibility(GONE);
        mPlayerSeek.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mIvPlay.setEnabled(false);
        mIvPlay.setSelected(false);
        return this;
    }

    /**
     * 视频停止loading
     */
    public IjkPlayerView cancelPlayLoading() {
        rl_reload_replay.setVisibility(View.GONE);
        mIvPlay.setEnabled(true);
        mIvPlay.setSelected(true);
        mPlayerSeek.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        return this;
    }

    /**
     * 获取是否全屏
     *
     * @return
     */
    public boolean isFullscreen() {
        return mIsFullscreen;
    }

    /**
     * 开始播放
     *
     * @return
     */
    public void start() {
        if (mIsPlayComplete) {
            mIsPlayComplete = false;
        }
        if (!mVideoView.isPlaying()) {
            mIvPlay.setSelected(true);
            mVideoView.start();
            // 更新进度
            mHandler.sendEmptyMessage(MSG_UPDATE_SEEK);
        }
        if (mIsNeverPlay) {
            mIsNeverPlay = false;
            mPbLoading.setVisibility(VISIBLE);
            startPlayLoading();
            mIsShowBar = false;
            _hideAllView();
        }
        // 视频播放时开启屏幕常亮
        mAttachActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * 重新开始
     */
    public void reload() {
        startPlayLoading();
        if (mIsReady) {
            // 确保网络正常时
            if (NetWorkUtils.isNetworkAvailable(mAttachActivity)) {
                Uri uri = mVideoView.getUri();
                LogUtils.e("uri:" + uri);
                mVideoView.reload();
                mVideoView.start();
                if (mInterruptPosition > 0) {
                    seekTo(mInterruptPosition);
                    mInterruptPosition = 0;
                }
            }
        } else {
            mVideoView.release(false);
            mVideoView.setRender(IjkVideoView.RENDER_TEXTURE_VIEW);
            start();
        }
        // 更新进度
        mHandler.removeMessages(MSG_UPDATE_SEEK);
        mHandler.sendEmptyMessage(MSG_UPDATE_SEEK);
    }

    /**
     * 是否正在播放
     *
     * @return
     */
    public boolean isPlaying() {
        return mVideoView.isPlaying();
    }

    /**
     * 暂停
     */
    public void pause() {
        mIvPlay.setSelected(false);
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
        }
        // 视频暂停时关闭屏幕常亮
        mAttachActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * 跳转
     *
     * @param position 位置
     */
    public void seekTo(int position) {
        if (isMovie) {
            mVideoView.seekTo(position);
        }
    }

    /**
     * 停止
     */
    public void stop() {
        pause();
        mVideoView.stopPlayback();
    }

    /**
     * 重置状态
     */
    public void reset() {
        if (!isNetReload) {
            mTvCurTime.setText("00:00:00");
            mTvEndTime.setText("00:00:00");
            mPlayerSeek.setProgress(0);
        }

        mIsNeverPlay = true;
        mCurPosition = 0;
        stop();
        mVideoView.setRender(IjkVideoView.RENDER_TEXTURE_VIEW);
    }

    /**
     * 隐藏视图Runnable
     */
    private Runnable mHideBarRunnable = new Runnable() {
        @Override
        public void run() {
            Flowable.timer(100, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            _hideAllView();
                        }
                    });
        }
    };

    /**
     * 隐藏除视频外所有视图
     */
    private void _hideAllView() {
        mTvChangeSource.setVisibility(View.GONE);
        txtShowList.setVisibility(View.GONE);
        mFullscreenTopBar.setVisibility(View.GONE);
        mTvTitle.setVisibility(GONE);
        mLlBottomBar.setVisibility(View.GONE);
        mIsShowBar = false;
    }

    /**
     * 设置控制栏显示或隐藏
     *
     * @param isShowBar
     */
    private void _setControlBarVisible(boolean isShowBar) {
        mLlBottomBar.setVisibility(isShowBar ? View.VISIBLE : View.GONE);
        mTvChangeSource.setVisibility(isShowBar ? (TvDataTransform.isSitcomDemand() ? View.GONE : View.VISIBLE) : View.GONE);
        mTvTitle.setVisibility(VISIBLE);

        // 全屏切换显示的控制栏不一样
        if (mIsFullscreen) {
            txtShowList.setVisibility(isShowBar ? View.VISIBLE : View.GONE);
            mFullscreenTopBar.setVisibility(isShowBar ? View.VISIBLE : View.GONE);
            mWindowTopBar.setVisibility(View.GONE);
        } else {
            mWindowTopBar.setVisibility(View.VISIBLE);
            mFullscreenTopBar.setVisibility(View.GONE);
        }
        setTitleParams();
    }

    /**
     * 显示控制栏
     *
     * @param timeout 延迟隐藏时间
     */
    private void _showControlBar(int timeout) {
        if (!mIsShowBar) {
            _setProgress();
            mIsShowBar = true;
        }
        _setControlBarVisible(true);
        mHandler.sendEmptyMessage(MSG_UPDATE_SEEK);
        // 先移除隐藏控制栏 Runnable，如果 timeout=0 则不做延迟隐藏操作
        mHandler.removeCallbacks(mHideBarRunnable);
        if (timeout != 0) {
            mHandler.postDelayed(mHideBarRunnable, timeout);
        }
    }

    /**
     * 切换播放状态，点击播放按钮时
     */
    public IjkPlayerView _togglePlayStatus() {
        if (mVideoView.isPlaying()) {
            pause();
        } else {
            start();
        }
        return this;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.fl_left) {
            mVideoAllCallBack.onClickQuitFullscreen();
            setPlayerControlUI();
            mIsFullscreen = false;
            mAttachActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        } else if (id == R.id.fl_left_window) {
            mVideoAllCallBack.onActivityFinish();
        } else if (id == R.id.iv_play) {
            if (!mIsNetConnected) {
                Toast.makeText(mAttachActivity, "当前网络不可用，请查看网络", Toast.LENGTH_SHORT).show();
                return;
            }
            if (noSource) {
                return;
            }
            _togglePlayStatus();
        } else if (id == R.id.iv_fullscreen) {
            _toggleFullScreen();
        } else if (id == R.id.txt_reload || id == R.id.fl_loading) {
            if (!mIsNetConnected) {
                Toast.makeText(mAttachActivity, "当前网络不可用，请检查网络", Toast.LENGTH_SHORT).show();
                return;
            }

            if (noSource) {
                startPlayLoading();

                Flowable.timer(1000, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                setLoadingPlayUI(true);
                            }
                        });
                return;
            }
            if (isReplay) {
                isReplay = false;
                mHandler.removeCallbacks(mHideBarRunnable);
                if (mVideoView.getDuration() == -1) {
                    startPlayLoading();
                    mPlayerSeek.setProgress(0);
                    mFlVideoBox.setEnabled(false);
                    // 更新进度
                    mVideoView.release(false);
                    mVideoView.setRender(IjkVideoView.RENDER_TEXTURE_VIEW);
                    start();
                } else {
                    start();
                }
            } else {
                RxBus.getInstance().send(new PlayCompleteEvent());
            }
        } else if (id == R.id.land_change_source) {//显示源列表
            changePlaySource();
        } else if (id == R.id.txt_show_list) {//显示节目列表
            showList(true);
        } else if (id == R.id.txt_live) {//点击轮播按钮
            TvDataTransform.setKindPositionTemp(0);
            rvTvLandChannel.stopScroll();
            RxBus.getInstance().send(new TvEvent.ChannelChangedEvent(TvDataTransform.getClassifyPositionTemp(), 0));
        } else if (id == R.id.txt_movie) {//点击点播按钮
            TvDataTransform.setKindPositionTemp(1);
            rvTvLandChannel.stopScroll();
            RxBus.getInstance().send(new TvEvent.ChannelChangedEvent(TvDataTransform.getClassifyPositionTemp(), 1));
        }
    }

    /**
     * 节目列表是否显示   经常以此区分是否在横竖屏《操作列表》的差异化处理
     *
     * @return
     */
    public boolean isShowList() {
        return llTvPlayLandSelect.getVisibility() == VISIBLE;
    }

    /**
     * 显示列表操作
     *
     * @param yes
     */
    public void showList(boolean yes) {
        if (yes) {
            //使临时变量与全局变量一致
            TvDataTransform.setClassifyPositionTemp();
            TvDataTransform.setKindPositionTemp();
            TvDataTransform.setClassifyPositionSecondTemp();
            //更新数据
            rvTvLandChannel.stopScroll();
            RxBus.getInstance().send(new TvEvent.ChannelChangedEvent(TvDataTransform.getClassifyPosition()));
            _hideAllView();
            txtShowList.setVisibility(GONE);
            llTvPlayLandSelect.setVisibility(VISIBLE);
            changedListData();
        } else {
            rvTvLandChannel.stopScroll();
            RxBus.getInstance().send(new TvEvent.ChannelChangedEvent(TvDataTransform.getClassifyPosition(), TvDataTransform.getKindPositionTemp()));
            llTvPlayLandSelect.setVisibility(GONE);
        }
    }

    /**
     * 获取频道列表的列表
     *
     * @return
     */
    public RecyclerView getRvTvLandChannel() {
        return rvTvLandChannel;
    }

    /**
     * 获取标题控件
     *
     * @return
     */
    public TextView getmTvTitle() {
        return mTvTitle;
    }

    /**
     * 点击横屏分类时加载中节目展示
     */
    public void loadList() {
        containerTypeSwitch.setVisibility(GONE);
        rvTvLandClassifySecondary.setVisibility(GONE);
        rvTvLandChannel.setVisibility(GONE);
        container_loading.setVisibility(VISIBLE);
        txt_empty.setVisibility(GONE);
        loading_list.setVisibility(VISIBLE);
    }

    /**
     * 点击电视剧点播是需要单独获取剧集，需要展示加载中的控件 并且只隐藏频道列表
     */
    public void loadSitcomList() {
        container_loading.setVisibility(VISIBLE);
        txt_empty.setVisibility(GONE);
        loading_list.setVisibility(VISIBLE);
        rvTvLandChannel.setVisibility(GONE);
    }

    public void loadSitcomListErr() {
        rvTvLandChannel.setVisibility(GONE);
        container_loading.setVisibility(VISIBLE);
        txt_empty.setVisibility(VISIBLE);
        loading_list.setVisibility(GONE);
    }

    /**
     * 横屏请求数据是请求失败事调用
     */
    public void listNoData() {
        containerTypeSwitch.setVisibility(GONE);
        rvTvLandClassifySecondary.setVisibility(GONE);
        rvTvLandChannel.setVisibility(GONE);
        container_loading.setVisibility(VISIBLE);
        txt_empty.setVisibility(VISIBLE);
        loading_list.setVisibility(GONE);
    }

    /**
     * 改变分类、节目等 列表数据
     */
    public void changedListData() {
        if (llTvPlayLandSelect.getVisibility() == VISIBLE) {
            //处理共有逻辑  比如隐藏加载控件  显示节目列表等操作
            container_loading.setVisibility(GONE);
            rvTvLandChannel.setVisibility(VISIBLE);
            rvTvLandClassifySecondary.getLayoutParams().width = mAttachActivity.getResources().getDimensionPixelSize(R.dimen.size_100);
            rvTvLandChannel.getLayoutParams().width = mAttachActivity.getResources().getDimensionPixelSize(R.dimen.size_140);
            classifyLandAdapter.notifyDataSetChanged();
            tvChannelLandAdapter.notifyDataSetChanged();

            //定位位置
            int classifyPosition = TvDataTransform.getClassifyPositionTemp();
            int classifyPosition1 = TvDataTransform.getClassifyPosition();

            int channelPosition = TvDataTransform.getChannelPosition();

            rvTvLandClassify.scrollToPosition(classifyPosition);
            if (classifyPosition != classifyPosition1
                    || TvDataTransform.getKindPosition() != TvDataTransform.getKindPositionTemp()
                    || TvDataTransform.getClassifyPositionSecond() != TvDataTransform.getClassifyPositionSecondTemp()) {
                rvTvLandChannel.scrollToPosition(0);
            } else {
                rvTvLandChannel.scrollToPosition(channelPosition);
            }

            CommonChannel commonChannel = TvDataTransform.getmAllChannels().get(classifyPosition);
            int kindPosition = TvDataTransform.getKindPositionTemp();

            if (commonChannel != null) {
                //判断kind分类所处于的位置并展示控件
                if (kindPosition == 0) {
                    txtLive.setTextColor(getResources().getColor(R.color.theme_color));
                    txtMovie.setTextColor(Color.WHITE);
                } else if (kindPosition == 1) {
                    txtLive.setTextColor(Color.WHITE);
                    txtMovie.setTextColor(getResources().getColor(R.color.theme_color));
                } else {
                    txtLive.setTextColor(Color.WHITE);
                    txtMovie.setTextColor(Color.WHITE);
                }

                switch (commonChannel.getType()) {//客户端渲染类型  -2 自定义源  -1 电视直播（没有授权） 0：央视卫视 1：地方 2：电影 3：少儿 4：电视剧
                    case -2:
                        containerTypeSwitch.setVisibility(GONE);

                        rvTvLandClassifySecondary.setVisibility(GONE);
                        break;
                    case 0://央视隐藏kind切换控件和二级分类控件
                        containerTypeSwitch.setVisibility(GONE);

                        rvTvLandClassifySecondary.setVisibility(GONE);
                        break;
                    case 1://地方隐藏kind切换控件
                        containerTypeSwitch.setVisibility(GONE);

                        classifySecondLandAdapter.notifyDataSetChanged();
                        rvTvLandClassifySecondary.setVisibility(VISIBLE);
                        break;
                    case 2://电影、电视剧显示kind切换控件
                    case 4:
                        containerTypeSwitch.setVisibility(VISIBLE);
                        switch (kindPosition) {//轮播是隐藏二级分类列表
                            case 0:
                                rvTvLandClassifySecondary.setVisibility(GONE);
                                break;
                            case 1://点播是显示二级分类列表
                                rvTvLandClassifySecondary.setVisibility(VISIBLE);
                                classifySecondLandAdapter.notifyDataSetChanged();
                                rvTvLandClassifySecondary.getLayoutParams().width = mAttachActivity.getResources().getDimensionPixelSize(R.dimen.size_140);
                                rvTvLandChannel.getLayoutParams().width = mAttachActivity.getResources().getDimensionPixelSize(R.dimen.size_100);
                                break;
                        }
                        break;
                    case 3://少儿  显示kind切换按钮  隐藏二级分类列表  两种分类都是只显示节目列表
                        containerTypeSwitch.setVisibility(VISIBLE);
                        rvTvLandClassifySecondary.setVisibility(GONE);
                        break;
                    default:
                        break;
                }

                //判断两个列表是否都存在数据  否则隐藏kind切换按钮
                ArrayList<TvChannel> tvLives = commonChannel.getTvLives();
                if (tvLives != null && tvLives.size() > 0) {
                    txtLive.setText(tvLives.get(0).g + "");
                    txtLive.setVisibility(VISIBLE);
                } else {
                    txtLive.setVisibility(GONE);
                    containerTypeSwitch.setVisibility(GONE);
                }
                ArrayList<TvChannel> onDemands = commonChannel.getOnDemands();

                if (onDemands != null && onDemands.size() > 0) {
                    txtMovie.setText(onDemands.get(0).g + "");
                    txtMovie.setVisibility(VISIBLE);
                } else {
                    containerTypeSwitch.setVisibility(GONE);
                    txtMovie.setVisibility(GONE);
                }
                //tvLives列表是必须要有的  没有的话说明一个数据都没有  隐藏除了分类列表外的索引列表
                if (tvLives == null || tvLives.size() < 1) {
                    containerTypeSwitch.setVisibility(GONE);
                    rvTvLandClassifySecondary.setVisibility(GONE);
                    rvTvLandChannel.setVisibility(GONE);
                }
                if (commonChannel.getType() == -2) {
                    rvTvLandChannel.setVisibility(VISIBLE);
                }
            }
        }
    }

    private int sourceHeight = getResources().getDimensionPixelSize(R.dimen.common_height);
    private int sourcePaddingTop = getResources().getDimensionPixelSize(R.dimen.edge_size);

    /**
     * 切换播放源列表显示   数据少的时候居中显示
     */
    public void changePlaySource() {
        _hideAllView();
        ArrayList<TvSourceDetail> tvSources = TvDataTransform.getmTvSources();
        if (tvSources == null || tvSources.size() < 1) {
            return;
        }

        int height = getHeight();
        int i = height - 2 * sourcePaddingTop;
        int i1 = i - sourceHeight * tvSources.size();
        if (i1 > 0) {//源数量少的话居中显示
            recyclerSource.setPadding(0, sourcePaddingTop + i1 / 2, 0, sourcePaddingTop + i1 / 2);
        } else {
            recyclerSource.setPadding(0, sourcePaddingTop, 0, sourcePaddingTop);
        }

        recyclerSource.setVisibility(VISIBLE);

        int sourcePosition = TvDataTransform.getSourcePosition();
        recyclerSource.scrollToPosition(sourcePosition);
        sourceAdapter.notifyDataSetChanged();
    }

    /**==================== 屏幕翻转/切换处理 ====================*/
    /**
     * 全屏切换，点击全屏按钮
     */
    public void _toggleFullScreen() {
        if (WindowUtils.getScreenOrientation(mAttachActivity) == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                || WindowUtils.getScreenOrientation(mAttachActivity) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {

            quitFullscreen();
            mVideoAllCallBack.onClickQuitFullscreen();

        } else if (WindowUtils.getScreenOrientation(mAttachActivity) == ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
                || WindowUtils.getScreenOrientation(mAttachActivity) == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {

            setFullscreen();
            mVideoAllCallBack.onClickEnterFullscreen();

        }
        setPlayerControlUI();
    }

    /**
     * 设置全屏
     */
    private void setFullscreen() {
        mIsFullscreen = true;
        setTitleParams();
        mWindowTopBar.setVisibility(GONE);
        mAttachActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
//        Flowable.timer(500, TimeUnit.MILLISECONDS)
//                .subscribe(new Consumer<Long>() {
//                    @Override
//                    public void accept(Long aLong) throws Exception {
        mAttachActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
//                    }
//                });

        disMissSelectSourceView();

        if (TvDataTransform.getIsShowVolumeBrightTip()) {
            if (mVolumeAndBrightTipView != null) {
                removeView(mVolumeAndBrightTipView);
            }
            mVolumeAndBrightTipView = mAttachActivity.getLayoutInflater().inflate(R.layout.layout_touch_volume_bright, null);
            addView(mVolumeAndBrightTipView);
            mVolumeAndBrightTipView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    removeView(mVolumeAndBrightTipView);
                    TvDataTransform.putIsShowVolumeBrightTip(false);
                    return false;
                }
            });
        }
    }

    /**
     * 退出全屏
     */
    private void quitFullscreen() {
        mIsFullscreen = false;
        if (null != mVolumeAndBrightTipView) {
            removeView(mVolumeAndBrightTipView);
        }
        setTitleParams();
        mWindowTopBar.setVisibility(VISIBLE);
        mAttachActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);
//        Flowable.timer(500, TimeUnit.MILLISECONDS)
//                .subscribe(new Consumer<Long>() {
//                    @Override
//                    public void accept(Long aLong) throws Exception {
        mAttachActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
//                    }
//                });
    }

    /**
     * 自动进入全屏
     */
    public void autoEnterFullScreen() {
        if (!mIsFullscreen) {
            setFullscreen();
//            Flowable.timer(300, TimeUnit.MILLISECONDS)
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Consumer<Long>() {
//                        @Override
//                        public void accept(Long aLong) throws Exception {
            mVideoAllCallBack.onEnterFullscreen();
//                        }
//                    });
            setPlayerControlUI();
        }
    }

    /**
     * 自动退出全屏
     */
    public boolean autoQuitFullScreen() {

        if (llTvPlayLandSelect.getVisibility() == VISIBLE) {
            showList(false);
            return false;
        }
        if (disMissSelectSourceView()) {
            return false;
        }

        if (mIsFullscreen) {
            quitFullscreen();
            mVideoAllCallBack.onQuitFullscreen();
            setPlayerControlUI();
        }
        return true;
    }

    /**
     * 判断是否可以退出全屏  （当切换列表  频道列表显示的是取药先隐藏这些空间）
     *
     * @return
     */
    public boolean canFinish() {
        return !disMissSelectSourceView();
    }

    /**
     * 隐藏切源空间
     *
     * @return
     */
    public boolean disMissSelectSourceView() {
        if (recyclerSource.getVisibility() == VISIBLE) {
            recyclerSource.setVisibility(GONE);
            return true;
        }
        return false;
    }

    /**
     * 设置全屏或窗口模式
     *
     * @param isFullscreen
     */
    private void _setFullScreen(boolean isFullscreen) {
        mIsFullscreen = isFullscreen;
        // 处理弹幕相关视图
        _handleActionBar(isFullscreen);
        _changeHeight(isFullscreen);
        mIvFullscreen.setSelected(isFullscreen);
        mHandler.post(mHideBarRunnable);
        if (isFullscreen) {
            mTvChangeSource.setVisibility(isFullscreen ? (TvDataTransform.isSitcomDemand() ? GONE : VISIBLE) : GONE);
            txtShowList.setVisibility(isFullscreen ? View.VISIBLE : GONE);
        }
    }

    /**
     * 隐藏/显示 ActionBar
     *
     * @param isFullscreen
     */
    private void _handleActionBar(boolean isFullscreen) {
        ActionBar supportActionBar = mAttachActivity.getSupportActionBar();
        if (supportActionBar != null) {
            if (isFullscreen) {
                supportActionBar.hide();
            } else {
                supportActionBar.show();
            }
        }
    }

    /**
     * 改变视频布局高度
     *
     * @param isFullscreen
     */
    private void _changeHeight(boolean isFullscreen) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (isFullscreen) {
            // 高度扩展为横向全屏
            layoutParams.height = mWidthPixels;
        } else {
            // 还原高度
            layoutParams.height = mInitHeight;
        }
        setLayoutParams(layoutParams);
    }

//    /**
//     * 设置UI沉浸式显示
//     */
//    private void _setUiLayoutFullscreen() {
//        if (Build.VERSION.SDK_INT >= 14) {
//            // 获取关联 Activity 的 DecorView
//            View decorView = mAttachActivity.getWindow().getDecorView();
//            // 沉浸式使用这些Flag
//            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
//                    View.SYSTEM_UI_FLAG_FULLSCREEN |
//                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
//                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
//                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
//                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//            );
//            mAttachActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        }
//    }

    /**
     * 屏幕翻转后的处理，在 Activity.configurationChanged() 调用
     * SYSTEM_UI_FLAG_LAYOUT_STABLE：维持一个稳定的布局
     * SYSTEM_UI_FLAG_FULLSCREEN：Activity全屏显示，且状态栏被隐藏覆盖掉
     * SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN：Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态遮住
     * SYSTEM_UI_FLAG_HIDE_NAVIGATION：隐藏虚拟按键(导航栏)
     * SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION：效果同View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
     * SYSTEM_UI_FLAG_IMMERSIVE：沉浸式，从顶部下滑出现状态栏和导航栏会固定住
     * SYSTEM_UI_FLAG_IMMERSIVE_STICKY：黏性沉浸式，从顶部下滑出现状态栏和导航栏过几秒后会缩回去
     *
     * @param newConfig
     */
    public void configurationChanged(Configuration newConfig) {
        // 沉浸式只能在SDK19以上实现
        if (Build.VERSION.SDK_INT >= 14) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // 获取关联 Activity 的 DecorView
                View decorView = mAttachActivity.getWindow().getDecorView();
                // 保存旧的配置
                mScreenUiVisibility = decorView.getSystemUiVisibility();
                // 沉浸式使用这些Flag
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                );
                _setFullScreen(true);
                mAttachActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                View decorView = mAttachActivity.getWindow().getDecorView();
                // 还原
                decorView.setSystemUiVisibility(mScreenUiVisibility);
                _setFullScreen(false);
                mAttachActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
    }

    /**
     * ============================ Listener ============================
     * 视频播放状态监听
     */
    private OnInfoListener mInfoListener = new OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int status, int extra) {
            _switchStatus(status);
            return true;
        }
    };

    Disposable retry;

    private void cancelRetry() {
        if (retry != null && !retry.isDisposed()) {
            retry.dispose();
        }
    }

    private void startRetry() {
        retry = Flowable.timer(10000, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        retry();
                    }
                });
    }

    /**
     * 视频播放状态处理
     */
    private void _switchStatus(int status) {
        LogUtils.i("IjkPlayerView-status:" + status);
        switch (status) {
            case IMediaPlayer.MEDIA_INFO_BUFFERING_START: //缓冲开始:701
            case MediaPlayerParams.STATE_PREPARING:
                LogUtils.i("state:缓冲开始");
                onNetReload();
                startPlayLoading();
                mHandler.removeMessages(MSG_TRY_RELOAD);
                mVideoAllCallBack.onStartBuffer();
                cancelRetry();
                startRetry();
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_END: //缓冲结束
                LogUtils.i("state:缓冲结束");
                cancelRetry();
                cancelPlayLoading();
                mVideoAllCallBack.onEndBuffer();
                // 更新进度
                mHandler.removeMessages(MSG_UPDATE_SEEK);
                mHandler.sendEmptyMessage(MSG_UPDATE_SEEK);
                if (mVideoView.isPlaying() && mIsNetConnected) {
                    mInterruptPosition = 0;
                    if (!mIvPlay.isSelected()) {
                        // 这里处理断网重连后不会播放情况
                        mVideoView.start();
                        mIvPlay.setSelected(true);
                    }
                }
                break;
            case MediaPlayerParams.STATE_PREPARED:     //缓冲结束，准备播放
                startPlayLoading();
                mHandler.sendEmptyMessage(MSG_UPDATE_SEEK);
                LogUtils.i("state:缓冲结束，准备播放");
                mIsReady = true;
                break;
            case MediaPlayerParams.STATE_PLAYING:   //播放中
                LogUtils.i("state:播放中");
                if (isErr) {
                    LogUtils.e("seekTo:" + currentPosition);
                    mTargetPosition = currentPosition;
                    seekTo((int) mTargetPosition);
                    mTargetPosition = INVALID_VALUE;
                    _setProgress();
                    _showControlBar(DEFAULT_HIDE_TIMEOUT);
                    isErr = false;
                    isNetReload = false;
                }
                cancelPlayLoading();
                mHandler.removeMessages(MSG_TRY_RELOAD);
                // 更新进度
                mHandler.removeMessages(MSG_UPDATE_SEEK);
                mHandler.sendEmptyMessage(MSG_UPDATE_SEEK);
                mVideoAllCallBack.startPlay();
                mFlVideoBox.setEnabled(true);
                break;
            case MediaPlayerParams.STATE_ERROR:     //错误状态
                LogUtils.i("state:播放错误");
                cancelRetry();
                cancelPlayLoading();
                mVideoAllCallBack.stopPlay();
                mInterruptPosition = Math.max(mVideoView.getInterruptPosition(), mInterruptPosition);
                if (isMovie) {
                    setLoadingPlayUI(true);
                } else {
                    mVideoAllCallBack.onPlayError();
                }
                break;
            case MediaPlayerParams.STATE_COMPLETED: //完成状态
                // FIXME: 2017/11/27 点播播放完成
                LogUtils.i("state:播放完成");
                cancelPlayLoading();
                mVideoAllCallBack.stopPlay();

                if (isMovie) {
                    int max = mVideoView.getDuration();
                    currentPosition = mVideoView.getCurrentPosition();
                    if (Math.abs(max - currentPosition) > 3000) {
                        onNetReload();
                    } else {
                        onComplete();
                    }
                    LogUtils.e("currentPosition:" + currentPosition + " max:" + max);
                } else {
                    mVideoAllCallBack.onPlayError();
//                    RxBus.getInstance().send(new PlayCompleteEvent());
                }
                break;
            case MediaPlayerParams.STATE_PAUSED:    //视频pause
                break;
            default:
                break;
        }
    }

    private boolean isNetReload;//是否断网
    private int currentPosition;//当前播放位置
    private boolean isErr;//是否播放出错

    /**
     * 判断是否断过网 断过网的话 设置标记 并且重新加载视频
     */
    private void onNetReload() {
        if (netDiscontinue) {
            isNetReload = true;
            RxBus.getInstance().send(new PlayCompleteEvent());
            isErr = true;
            netDiscontinue = false;
        }
    }

    private void retry() {
        RxBus.getInstance().send(new PlayCompleteEvent());
    }

    //播放完成的操作  包括
    private void onComplete() {
        pause();
        setLoadingPlayUI(false);
        mVideoView.pause();
        mVideoView.destroy();
        mHandler.removeMessages(MSG_UPDATE_SEEK);
        mPlayerSeek.setProgress(0);

        mTvCurTime.setText("00:00:00");
        isReplay = true;
    }

    /**
     * 触摸监听
     */
    private OnTouchListener mPlayerTouchListener = new OnTouchListener() {

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (MotionEventCompat.getActionMasked(event)) {
                case MotionEvent.ACTION_UP:
                    /**
                     * 关闭
                     */
                    disMissSelectSourceView();
                    if (isFullscreen()) {//全屏的话显示频道列表
                        showList(false);
                    }

                    mHandler.removeCallbacks(mHideBarRunnable);
                    if (mIsShowBar) {//如果空间显示的话隐藏   否则就显示
                        _hideAllView();
                    } else {//显示的话延迟10s自动隐藏
                        _setControlBarVisible(true);
                        // 发送延迟隐藏控制栏的操作
                        mHandler.postDelayed(mHideBarRunnable, DEFAULT_HIDE_TIMEOUT);
                        mIsShowBar = true;
                    }
                    _endGesture();
                    break;
            }
            if (isFullscreen()) {
                if (null != mGestureDetector) {
                    if (mGestureDetector.onTouchEvent(event)) {
                        return true;
                    }
                }
            }
            return false;
        }
    };

    /**
     * 更新进度条
     */
    private int _setProgress() {
        if (mVideoView == null || mIsSeeking) {
            return 0;
        }
        // 视频播放的当前进度
        int position = Math.max(mVideoView.getCurrentPosition(), mInterruptPosition);
        // 视频总的时长
        int duration = mVideoView.getDuration();
        if (duration > 0) {
            // 转换为 Seek 显示的进度值
            long pos = (long) MAX_VIDEO_SEEK * position / duration;
            mPlayerSeek.setProgress((int) pos);
        }
        // 获取缓冲的进度百分比，并显示在 Seek 的次进度
        int percent = mVideoView.getBufferPercentage();
        if (!isNetReload) {
            mPlayerSeek.setSecondaryProgress(percent * 10);
            // 更新播放时间
            mTvCurTime.setText(PlayerUtils.transTimeToHMS(position));
            mTvEndTime.setText(PlayerUtils.transTimeToHMS(duration));
        }
        // 返回当前播放进度
        return position;
    }
//
//    /**
//     * 监听是否有外部其他多媒体开始播放
//     */
//    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
//        @Override
//        public void onAudioFocusChange(int focusChange) {
//            switch (focusChange) {
//                case AudioManager.AUDIOFOCUS_GAIN:
//                    if (isTvPlaying) {
//                        onResume();
//                    }
//                    break;
//                case AudioManager.AUDIOFOCUS_LOSS:
//                    mHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//
//                        }
//                    });
//                    break;
//                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
//                    if (null != GSYVideoManager.instance().getMediaPlayer()) {
//                        isTvPlaying = isPlaying();
//                        if (isTvPlaying) {
////                            onPause();
//                        }
//                    }
//                    break;
//                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
//                    break;
//            }
//        }
//    };

    /**
     * SeekBar监听
     */
    private final OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {

        @Override
        public void onStartTrackingTouch(SeekBar bar) {
            LogUtils.e("onStartTrackingTouch");
            if (!mIsNetConnected) {
                Toast.makeText(mAttachActivity, "当前网络不可用，请检查网络", Toast.LENGTH_SHORT).show();
                return;
            }
            mIsSeeking = true;
            _showControlBar(3600000);
            mHandler.removeMessages(MSG_UPDATE_SEEK);
        }

        @Override
        public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
            if (!fromUser) {
                if (isMovie) {
                    if (!isErr) {
                        currentPosition = mVideoView.getCurrentPosition();
                    }
                }
                return;
            }
            if (!mIsNetConnected) {
                Toast.makeText(mAttachActivity, "当前网络不可用，请检查网络", Toast.LENGTH_SHORT).show();
                return;
            }

            long duration = mVideoView.getDuration();
            // 计算目标位置
            mTargetPosition = (duration * progress) / MAX_VIDEO_SEEK;
        }

        @Override
        public void onStopTrackingTouch(SeekBar bar) {
            // FIXME: 2017/11/27 播放进度
            LogUtils.e("onStopTrackingTouch");
            int mTotalPlayTime = mVideoView.getDuration();
            mIsSeeking = false;
            if (mTotalPlayTime < 0) {
                ToastUtil.showToast(getContext(), "请开始播放后再拖动进度条");
            }
            if (noSource) {
                return;
            }
            if (mTargetPosition >= mTotalPlayTime) {    //拉到最后
                if (isMovie) {
                    onComplete();
                }
            } else {
                if (isMovie) {
                    currentPosition = (int) mTargetPosition;
                    pause();
                    LogUtils.e("" + isErr);
                    if (netDiscontinue) {
                        onNetReload();
                    } else {
                        // 视频跳转
                        start();
                        seekTo((int) mTargetPosition);
                        mTargetPosition = INVALID_VALUE;
                        startPlayLoading();
                        _setProgress();
                        _showControlBar(DEFAULT_HIDE_TIMEOUT);
                    }
                }
            }
        }
    };


    /**
     * ============================ 播放清晰度 ============================
     * 依次分别为：流畅、清晰、高清、超清和1080P
     */
    public static final int MEDIA_QUALITY_SMOOTH = 0;
    public static final int MEDIA_QUALITY_MEDIUM = 1;
    public static final int MEDIA_QUALITY_HIGH = 2;
    public static final int MEDIA_QUALITY_SUPER = 3;
    public static final int MEDIA_QUALITY_BD = 4;

    // 保存Video Url
    private SparseArray<String> mVideoSource = new SparseArray<>();
    // 描述信息
    private String[] mMediaQualityDesc;
    // 列表数据
    private List<MediaQualityInfo> mQualityData;
    // 当前选中的分辨率
    private
    @MediaQuality
    int mCurSelectQuality = MEDIA_QUALITY_SMOOTH;

    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
    @IntDef({MEDIA_QUALITY_SMOOTH, MEDIA_QUALITY_MEDIUM, MEDIA_QUALITY_HIGH, MEDIA_QUALITY_SUPER, MEDIA_QUALITY_BD})
    public @interface MediaQuality {
    }

    /**
     * 初始化视频分辨率处理
     */
    private void _initMediaQuality() {
        mMediaQualityDesc = getResources().getStringArray(R.array.media_quality);
    }

    /**
     * 设置视频源
     *
     * @param mediaSmooth 流畅
     * @param mediaMedium 清晰
     * @param mediaHigh   高清
     * @param mediaSuper  超清
     * @param mediaBd     1080P
     */
    public IjkPlayerView setVideoSource(Map<String, String> headers, String mediaSmooth, String mediaMedium, String mediaHigh,
                                        String mediaSuper, String mediaBd) {
        boolean isSelect = true;
        mQualityData = new ArrayList<>();
        if (mediaSmooth != null) {
            mVideoSource.put(MEDIA_QUALITY_SMOOTH, mediaSmooth);
            mQualityData.add(new MediaQualityInfo(MEDIA_QUALITY_SMOOTH, mMediaQualityDesc[MEDIA_QUALITY_SMOOTH], isSelect));
            mCurSelectQuality = MEDIA_QUALITY_SMOOTH;
            isSelect = false;
        }
        if (mediaMedium != null) {
            mVideoSource.put(MEDIA_QUALITY_MEDIUM, mediaMedium);
            mQualityData.add(new MediaQualityInfo(MEDIA_QUALITY_MEDIUM, mMediaQualityDesc[MEDIA_QUALITY_MEDIUM], isSelect));
            if (isSelect) {
                mCurSelectQuality = MEDIA_QUALITY_MEDIUM;
            }
            isSelect = false;
        }
        if (mediaHigh != null) {
            mVideoSource.put(MEDIA_QUALITY_HIGH, mediaHigh);
            mQualityData.add(new MediaQualityInfo(MEDIA_QUALITY_HIGH, mMediaQualityDesc[MEDIA_QUALITY_HIGH], isSelect));
            if (isSelect) {
                mCurSelectQuality = MEDIA_QUALITY_HIGH;
            }
            isSelect = false;
        }
        if (mediaSuper != null) {
            mVideoSource.put(MEDIA_QUALITY_SUPER, mediaSuper);
            mQualityData.add(new MediaQualityInfo(MEDIA_QUALITY_SUPER, mMediaQualityDesc[MEDIA_QUALITY_SUPER], isSelect));
            if (isSelect) {
                mCurSelectQuality = MEDIA_QUALITY_SUPER;
            }
            isSelect = false;
        }
        if (mediaBd != null) {
            mVideoSource.put(MEDIA_QUALITY_BD, mediaBd);
            mQualityData.add(new MediaQualityInfo(MEDIA_QUALITY_BD, mMediaQualityDesc[MEDIA_QUALITY_BD], isSelect));
            if (isSelect) {
                mCurSelectQuality = MEDIA_QUALITY_BD;
            }
        }
        setVideoPath(mVideoSource.get(mCurSelectQuality), headers, videoType);
        return this;
    }

    /**
     * 选择视频源
     *
     * @param quality 分辨率
     *                {@link #MEDIA_QUALITY_SMOOTH,#MEDIA_QUALITY_MEDIUM,#MEDIA_QUALITY_HIGH,#MEDIA_QUALITY_SUPER,#MEDIA_QUALITY_BD}
     * @return
     */
    public IjkPlayerView setMediaQuality(@MediaQuality int quality) {
        if (mCurSelectQuality == quality || mVideoSource.get(quality) == null) {
            return this;
        }
        mCurSelectQuality = quality;
        if (mVideoView.isPlaying()) {
            mCurPosition = mVideoView.getCurrentPosition();
            mVideoView.release(false);
        }
        mVideoView.setRender(IjkVideoView.RENDER_TEXTURE_VIEW);
        setVideoPath(mVideoSource.get(quality), videoType);
        return this;
    }

    public void setNetConnectedEvent(boolean isConnected) {
        if (isConnected) {  //网路连接上
            mPlayerSeek.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
            mPlayerSeek.setOnSeekBarChangeListener(mSeekListener);
        } else {    //网络断开
            netDiscontinue = true;
            mPlayerSeek.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            onPause();
        }
        mIsNetConnected = isConnected;
    }

    /**
     * ============================ 手势控制音量和亮度 ============================
     */
    // 手势控制
    private GestureDetector mGestureDetector;
    // 最大音量
    private int mMaxVolume;
    // 当前音量
    private int mCurVolume = INVALID_VALUE;
    // 当前亮度
    private float mCurBrightness = INVALID_VALUE;
    /**
     * 手势监听
     */
    private GestureDetector.OnGestureListener mPlayerGestureListener = new GestureDetector.SimpleOnGestureListener() {

        // 是否是按下的标识，默认为其他动作，true为按下标识，false为其他动作
        private boolean isDownTouch;
        // 是否声音控制,默认为亮度控制，true为声音控制，false为亮度控制
        private boolean isVolume;
        // 是否横向滑动，默认为纵向滑动，true为横向滑动，false为纵向滑动
        private boolean isLandscape;

        @Override
        public boolean onDown(MotionEvent e) {
            isDownTouch = true;
            return super.onDown(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            try {
                if (!mIsNeverPlay) {
                    float mOldX = e1.getX(), mOldY = e1.getY();
                    float deltaY = mOldY - e2.getY();
                    float deltaX = mOldX - e2.getX();
                    if (isDownTouch) {
                        // 判断左右或上下滑动
                        isLandscape = Math.abs(distanceX) >= Math.abs(distanceY);
                        // 判断是声音或亮度控制
                        isVolume = mOldX > getResources().getDisplayMetrics().widthPixels * 0.5f;
                        isDownTouch = false;
                    }
                    if (isLandscape) {
                        if (isMovie) {
//                        _onProgressSlide(-deltaX / mVideoView.getWidth());
                        }
                    } else {
                        float percent = deltaY / mVideoView.getHeight();
                        if (isVolume) {
                            _onVolumeSlide(percent);
                        } else {
                            _onBrightnessSlide(percent);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return true;
        }
    };

    /**
     * 快进或者快退滑动改变进度，这里处理触摸滑动不是拉动 SeekBar
     *
     * @param percent 拖拽百分比
     */
    private void _onProgressSlide(float percent) {
        int position = mVideoView.getCurrentPosition();
        long duration = mVideoView.getDuration();
        // 单次拖拽最大时间差为100秒或播放时长的1/2
        long deltaMax = Math.min(100 * 1000, duration / 2);
        // 计算滑动时间
        long delta = (long) (deltaMax * percent);
        // 目标位置
        mTargetPosition = delta + position;
        if (mTargetPosition > duration) {
            mTargetPosition = duration;
        } else if (mTargetPosition <= 0) {
            mTargetPosition = 0;
        }
        // 对比当前位置来显示快进或后退
        if (mTargetPosition > position) {
            setFastForward(true, position, duration);
        } else {
            setFastForward(false, position, duration);
        }
    }

    /**
     * 设置快进快退
     *
     * @param isFast
     */
    private void setFastForward(boolean isFast, long position, long duration) {
        mFastForwardLayout = mAttachActivity.getLayoutInflater().inflate(R.layout.layout_fast_forward, null);
        ImageView imageView = (ImageView) mFastForwardLayout.findViewById(R.id.iv_tv_fast_forward);
        TextView textView = (TextView) mFastForwardLayout.findViewById(R.id.tv_tv_fast_forward_time);
        SeekBar seekBar = (SeekBar) mFastForwardLayout.findViewById(R.id.sb_tv_fast_forward);

        String textStr = "<font color=\"#FF0000\">" + PlayerUtils.tran(position) + "</font>/" +
                PlayerUtils.tran(duration);
        if (isFast) {
            imageView.setImageResource(R.drawable.ic_fast_forward);
        } else {
            imageView.setImageResource(R.drawable.ic_rewind);
        }
        textView.setText(Html.fromHtml(textStr));
        long pos = (long) MAX_VIDEO_SEEK * position / duration;
        seekBar.setProgress((int) pos);
    }

    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    private void _onVolumeSlide(float percent) {
        if (mCurVolume == INVALID_VALUE) {
            mCurVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mCurVolume < 0) {
                mCurVolume = 0;
            }
        }
        int index = (int) (percent * mMaxVolume) + mCurVolume;
        if (index > mMaxVolume) {
            index = mMaxVolume;
        } else if (index < 0) {
            index = 0;
        }
        // 变更声音
        setProgress(true, mMaxVolume, index);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);
    }

    /**
     * 滑动改变亮度大小
     *
     * @param percent
     */
    private void _onBrightnessSlide(float percent) {
        if (mCurBrightness < 0) {
            mCurBrightness = mAttachActivity.getWindow().getAttributes().screenBrightness;
            if (mCurBrightness < 0.0f) {
                mCurBrightness = 0.5f;
            } else if (mCurBrightness < 0.01f) {
                mCurBrightness = 0.01f;
            }
        }
        WindowManager.LayoutParams attributes = mAttachActivity.getWindow().getAttributes();
        attributes.screenBrightness = mCurBrightness + percent;
        if (attributes.screenBrightness > 1.0f) {
            attributes.screenBrightness = 1.0f;
        } else if (attributes.screenBrightness < 0.01f) {
            attributes.screenBrightness = 0.01f;
        }
        setProgress(false, 1, attributes.screenBrightness);
        mAttachActivity.getWindow().setAttributes(attributes);
    }


    Disposable subscribe;

    /**
     * 设置 声音  亮度进度
     *
     * @param isVolume 是否是设置声音的
     * @param max      最大值
     * @param progress 当前值
     */
    private void setProgress(boolean isVolume, float max, float progress) {
        containerProgress.setVisibility(VISIBLE);
        if (isVolume) {
            imgProgress.setImageResource(R.mipmap.ic_volume_on);
        } else {
            imgProgress.setImageResource(R.mipmap.ic_brightness);
        }

        mProgress.setMax((int) (max * 100));
        mProgress.setProgress((int) (progress * 100));

        if (subscribe != null && !subscribe.isDisposed()) {
            subscribe.dispose();
        }
        subscribe = Flowable.timer(700, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        containerProgress.setVisibility(GONE);
                    }
                });
    }

    /**
     * 手势结束调用
     */
    private void _endGesture() {
        mCurVolume = INVALID_VALUE;
        mCurBrightness = INVALID_VALUE;
//        if (null != mFastForwardLayout) {
//            removeView(mFastForwardLayout);
//        }
//        if (isMovie) {
//            currentPosition = (int) mTargetPosition;
//            pause();
//            LogUtils.e("" + isErr);
//            if (netDiscontinue) {
//                onNetReload();
//            } else {
//                // 视频跳转
//                seekTo((int) mTargetPosition);
//                mTargetPosition = INVALID_VALUE;
//                startPlayLoading();
//                _setProgress();
//                _showControlBar(DEFAULT_HIDE_TIMEOUT);
//                start();
//            }
//        }
    }

    private void showToast(String msg) {
        Toast.makeText(mAttachActivity, msg, Toast.LENGTH_SHORT).show();
    }
}
