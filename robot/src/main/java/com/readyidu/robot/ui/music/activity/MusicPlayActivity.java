package com.readyidu.robot.ui.music.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RotateDrawable;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.readyidu.robot.R;
import com.readyidu.robot.YDRobot;
import com.readyidu.robot.api.NetSubscriber;
import com.readyidu.robot.api.impl.RobotServiceImpl;
import com.readyidu.robot.base.BaseVoiceBarActivity;
import com.readyidu.robot.component.music.MusicConstants;
import com.readyidu.robot.component.music.MusicRecord;
import com.readyidu.robot.component.music.utils.PlayerUtils;
import com.readyidu.robot.component.rxbus.RxBus;
import com.readyidu.robot.event.BdDetailExitEvent;
import com.readyidu.robot.event.CloseMusicListEvent;
import com.readyidu.robot.event.music.MusicEvent;
import com.readyidu.robot.model.BaseModel;
import com.readyidu.robot.model.business.music.Music;
import com.readyidu.robot.ui.music.adapter.MusicFragmentAdapter;
import com.readyidu.robot.ui.music.fragment.MusicPlayFragment1;
import com.readyidu.robot.ui.music.fragment.MusicPlayFragment2;
import com.readyidu.robot.ui.music.view.TipRelativeLayout;
import com.readyidu.robot.utils.data.ArithmeticUtils;
import com.readyidu.robot.utils.data.DataTranUtils;
import com.readyidu.robot.utils.glide.GlideUtils;
import com.readyidu.robot.utils.log.LogUtils;
import com.readyidu.robot.utils.view.DialogUtils;
import com.readyidu.robot.utils.view.MeasureUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * @Autour: wlq
 * @Description: 音乐播放页面
 * @Date: 2017/10/13 20:05
 * @Update: 2017/10/13 20:05
 * @UpdateRemark: 音乐播放页面
 * @Version:
 */
public class MusicPlayActivity extends BaseVoiceBarActivity implements View.OnClickListener {

    private TipRelativeLayout mRlPlayMode;
    private TextView mTvPlayMode;
    private ImageView mIv_music_play_background;
    private View mViewTran;
    private ViewPager mVp_music_play;
    private TextView mTv_vp_point1;
    private TextView mTv_vp_point2;
    private TextView mTv_time_cur;
    private SeekBar mSb_progress;
    private TextView mTv_time_total;
    private Button mBt_music_play_mode;
    private Button mBt_music_play_and_pause;
    private RotateDrawable drawable;
    private CountDownTimer timer;
    private RelativeLayout containerMusic;

    private boolean isSDKOutside;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_music_play;
    }

    @Override
    protected void bindViews() {
        super.bindViews();
        containerMusic = (RelativeLayout) findViewById(R.id.container_music);
        mRlPlayMode = (TipRelativeLayout) findViewById(R.id.rl_music_play_mode);
        mTvPlayMode = (TextView) findViewById(R.id.tv_music_play_mode);
        mIv_music_play_background = (ImageView) findViewById(R.id.iv_music_play_background);
        mVp_music_play = (ViewPager) findViewById(R.id.vp_music_play);
        mViewTran = findViewById(R.id.view_music_tran_background);
        mTv_vp_point1 = (TextView) findViewById(R.id.tv_vp_point1);
        mTv_vp_point2 = (TextView) findViewById(R.id.tv_vp_point2);
        mTv_time_cur = (TextView) findViewById(R.id.tv_time_cur);
        mSb_progress = (SeekBar) findViewById(R.id.sb_tv_play);
        mTv_time_total = (TextView) findViewById(R.id.tv_time_total);
        mBt_music_play_mode = (Button) findViewById(R.id.bt_music_play_mode);
        Button mBtPlayMode = (Button) findViewById(R.id.bt_music_play_up);
        mBt_music_play_and_pause = (Button) findViewById(R.id.bt_music_play_and_pause);
        Button mBt_music_play_next = (Button) findViewById(R.id.bt_music_play_next);
        Button mBt_music_play_list = (Button) findViewById(R.id.bt_music_play_list);

        mBt_music_play_mode.setOnClickListener(this);
        mBtPlayMode.setOnClickListener(this);
        mBt_music_play_and_pause.setOnClickListener(this);
        mBt_music_play_next.setOnClickListener(this);
        mBt_music_play_list.setOnClickListener(this);

        mTopBar.setBackIcon2(R.drawable.ic_top_bar_close2_white);
        mTv_vp_point1.setSelected(true);
        mTv_vp_point2.setSelected(false);

        setMusicUI();
        drawable = (RotateDrawable) getResources().getDrawable(R.drawable.anim_loading);

        Flowable.timer(500, TimeUnit.MICROSECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        boolean isHasNav = MeasureUtil.isNavigationBarShow(mContext);
                        int height;
                        if (isHasNav) {
                            height = MeasureUtil.getScreenHeight(MusicPlayActivity.this) -
                                    getResources().getDimensionPixelSize(R.dimen.size_0) +
                                    MeasureUtil.getNavigationBarHeight(mContext);
                        } else {
                            height = MeasureUtil.getScreenHeight(MusicPlayActivity.this) -
                                    getResources().getDimensionPixelSize(R.dimen.size_0);
                        }
                        RelativeLayout.LayoutParams layoutParams = new
                                RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                        mIv_music_play_background.setLayoutParams(layoutParams);
                        mViewTran.setLayoutParams(layoutParams);

                        int height2 = MeasureUtil.getScreenHeight(MusicPlayActivity.this) -
                                getResources().getDimensionPixelSize(R.dimen.size_0);
                        RelativeLayout.LayoutParams layoutParams2 = new
                                RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height2);
                        containerMusic.setLayoutParams(layoutParams2);

                    }
                });

        CountDownTimer timer = new CountDownTimer(700, 20) {
            @Override
            public void onTick(long millisUntilFinished) {
                Observable.timer(1, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                mVoiceBar.hide();
                            }
                        });
            }

            @Override
            public void onFinish() {

            }
        };
        timer.start();
    }

    @Override
    protected void bindEvents() {
        super.bindEvents();
        MusicRecord.getInstance().setmPlayMode(MusicConstants.PlayMode.ORDER);

        //设置fragment
        MusicPlayFragment1 fragment1 = new MusicPlayFragment1();
        MusicPlayFragment2 fragment2 = new MusicPlayFragment2();
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(fragment1);
        fragments.add(fragment2);
        MusicFragmentAdapter adapter = new MusicFragmentAdapter(getSupportFragmentManager(), fragments);
        mVp_music_play.setAdapter(adapter);
        mVp_music_play.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mTv_vp_point1.setSelected(true);
                    mTv_vp_point2.setSelected(false);
                } else {
                    mTv_vp_point2.setSelected(true);
                    mTv_vp_point1.setSelected(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        registerRxBus(MusicEvent.MusicState.class, new Consumer<MusicEvent.MusicState>() {
            @Override
            public void accept(MusicEvent.MusicState musicState) throws Exception {
                switch (musicState.playState) {  //0 播放；1 暂停；2恢复；3 停止
                    case 0:
                    case 2:
                        MusicRecord.getInstance().refreshProgress();
                        mBt_music_play_and_pause.setBackground(getResources().getDrawable(R.drawable.selector_music_play_play));
                        break;
                    case 1:
                        MusicRecord.getInstance().stopRefreshSeekBar();
                        mBt_music_play_and_pause.setBackground(getResources().getDrawable(R.drawable.selector_music_play_pause));
                        break;
                    case 3:
                        MusicRecord.getInstance().stopRefreshSeekBar();
                        mBt_music_play_and_pause.setBackground(getResources().getDrawable(R.drawable.selector_music_play_pause));
                        mTv_time_cur.setText(PlayerUtils.tran(0));
                        mTv_time_total.setText(PlayerUtils.tran(0));
                        break;
                }
            }
        });

        registerRxBus(MusicEvent.MusicPlayProgress.class, new Consumer<MusicEvent.MusicPlayProgress>() {
            @Override
            public void accept(MusicEvent.MusicPlayProgress musicPlayProgress) throws Exception {
                int total = musicPlayProgress.duration;
                int curPos = musicPlayProgress.currentPosition;
                int progress;
                if (total <= 0) {
                    progress = 0;
                } else {
                    progress = (int) (ArithmeticUtils.div(curPos, total, 10) * 100);
                }
                setNormal();
                mSb_progress.setProgress(progress);
                mTv_time_cur.setText(PlayerUtils.tran(curPos));
                mTv_time_total.setText(PlayerUtils.tran(total));
                RxBus.getInstance().send(new MusicEvent.MusicLrc(curPos));  //刷新歌词进度
            }
        });

        registerRxBus(MusicEvent.SwitchMusic.class, new Consumer<MusicEvent.SwitchMusic>() {
            @Override
            public void accept(MusicEvent.SwitchMusic switchMusic) throws Exception {
                setMusicUI();
                mSb_progress.setProgress(0);
                mBt_music_play_and_pause.setBackground(getResources().getDrawable(R.drawable.selector_music_play_play));
                setLoading();
            }
        });

        startMusicPlayerService();

        mSb_progress.setProgress(0);
        mBt_music_play_and_pause.setBackground(getResources().getDrawable(R.drawable.selector_music_play_play));
        Intent intent = getIntent();
        isSDKOutside = intent.getBooleanExtra("isSDKOutside", false);
        boolean isPlaying = intent.getBooleanExtra("isPlaying", false);
        if (!isPlaying) {
            setLoading();
        }

        //获取一下播放状态
        Flowable.timer(100, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        sendBroadcast(new Intent(MusicConstants.ACTION_MUSIC_IS_PLYING));
                    }
                });

        //关闭
        registerRxBus(BdDetailExitEvent.class, new Consumer<BdDetailExitEvent>() {
            @Override
            public void accept(BdDetailExitEvent messageReceiverEvent) throws Exception {
                finish();
            }
        });
    }

    private void setLoading() {
        mSb_progress.setThumb(drawable);
        if (timer == null) {
            timer = new CountDownTimer(Integer.MAX_VALUE, 60) {
                @Override
                public void onTick(long millisUntilFinished) {
                    int level = drawable.getLevel();
                    drawable.setLevel(level + 100);
                }

                @Override
                public void onFinish() {
                }
            };
        }

        timer.start();
    }

    private void setNormal() {
        Drawable drawable = getResources().getDrawable(R.drawable.selector_seek_bar);
        mSb_progress.setThumb(drawable);
        mSb_progress.clearAnimation();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void bindKeyBoardShowEvent() {
    }

    @Override
    protected void requestVoiceBarNet() {
        DialogUtils.showProgressDialog(this);
        addDisposable(RobotServiceImpl
                .getRobotMusicResponse(searchContent, false, 1, 20)
                .subscribeWith(new NetSubscriber<BaseModel>() {
                    @Override
                    protected void onSuccess(final BaseModel baseModel) {
                        final ArrayList<Music> musics = (ArrayList<Music>) DataTranUtils.tranMusic(baseModel);
                        if (musics != null && musics.size() > 0) {

                            RxBus.getInstance().send(new CloseMusicListEvent());
                            Flowable.timer(300, TimeUnit.MILLISECONDS)
                                    .subscribe(new Consumer<Long>() {
                                        @Override
                                        public void accept(Long aLong) throws Exception {
                                            startActivity(new Intent(MusicPlayActivity.this, MusicListActivity.class)
                                                    .putExtra("data", musics)
                                                    .putExtra("total", DataTranUtils.getTotal(baseModel))
                                                    .putExtra("keyword", baseModel.data.keyword)
                                                    .putExtra("isSearchList", true)
                                                    .putExtra("isSDKOutside", isSDKOutside));
                                            finish();
                                        }
                                    });
                        } else {
                            showToast("小益没有找到您要的音乐信息");
                        }
                    }

                    @Override
                    protected void onFailure(int errorCode, String errorMessage) {
                        LogUtils.e(errorCode + ":" + errorMessage);
                    }
                }));
    }

    /**
     * 开始音乐播放服务
     */
    private void startMusicPlayerService() {
        mSb_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    Intent it = new Intent(MusicConstants.ACTION_MUSIC_SET_PROGRESS);
                    it.putExtra(MusicConstants.MUSIC_PROGRESS, progress);
                    sendBroadcast(it);
                }
                if (progress == seekBar.getMax()) { //下一曲
                    switchSong(false);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBar.setThumbOffset(8);
                seekBar.setThumb(getResources().getDrawable(R.drawable.bg_thumb_press));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setThumbOffset(0);
                seekBar.setThumb(getResources().getDrawable(R.drawable.bg_thumb_normal));
            }
        });

        MusicRecord.getInstance().refreshProgress();
    }

    /**
     * 切换歌曲
     */
    private void switchSong(boolean isUp) {
        mBt_music_play_and_pause.setBackground(getResources().getDrawable(R.drawable.selector_music_play_pause));
        mTv_time_cur.setText(PlayerUtils.tran(0));
        mTv_time_total.setText(PlayerUtils.tran(0));
        mSb_progress.setProgress(0);

        YDRobot.getInstance().switchMusic(isUp);
    }

    /**
     * 设置播放音乐UI
     */
    private void setMusicUI() {
        try {
            mTopBar.setDoubleTitle(MusicRecord.getInstance().getCurrentData().get_source().getMusic_n()
                    , MusicRecord.getInstance().getCurrentData().get_source().getSinger_name());
            String musicImageUrl = MusicRecord.getInstance().getCurrentData().get_source().getAlbum_pic();
            if (TextUtils.isEmpty(musicImageUrl)) {
                GlideUtils.loadBlurImageRes(mContext, R.drawable.ic_music_play_head_default_background, mIv_music_play_background);
            } else {
                GlideUtils.loadBlurImageNet(mContext, musicImageUrl, mIv_music_play_background);
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.bt_music_play_mode == id) {    //切换播放模式
            String tip = "";
            MusicRecord.getInstance().switchPlayMode();
            switch (MusicRecord.getInstance().getmPlayMode()) {
                case MusicConstants.PlayMode.SINGLE:
                    mBt_music_play_mode.setBackground(getResources().getDrawable(R.drawable.selector_music_play_mode1));
                    mRlPlayMode.showTips();
                    tip = "已切换到单曲循环模式";
                    break;
                case MusicConstants.PlayMode.ORDER:
                    mBt_music_play_mode.setBackground(getResources().getDrawable(R.drawable.selector_music_play_mode2));
                    mRlPlayMode.showTips();//显示提示RelativeLayout,移动动画.
                    tip = "已切换到顺序播放模式";
                    break;
                case MusicConstants.PlayMode.RANDOM:
                    mBt_music_play_mode.setBackground(getResources().getDrawable(R.drawable.selector_music_play_mode3));
                    mRlPlayMode.showTips();
                    tip = "已切换到随机播放模式";
                    break;
            }
            mTvPlayMode.setText(tip);
            mRlPlayMode.showTips();
        } else if (R.id.bt_music_play_up == id) {   //上一曲
            switchSong(true);
        } else if (R.id.bt_music_play_and_pause == id) {    //播放&暂停
            YDRobot.getInstance().toggleMusic();
        } else if (R.id.bt_music_play_next == id) {  //下一曲
            switchSong(false);
        } else if (R.id.bt_music_play_list == id) {  //返回放列表
            RxBus.getInstance().send(new CloseMusicListEvent());
            Flowable.timer(300, TimeUnit.MILLISECONDS)
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            startActivity(new Intent(MusicPlayActivity.this, MusicListActivity.class)
                                    .putExtra("data", MusicRecord.getInstance().getmMusics())
                                    .putExtra("classifyPos", MusicRecord.getInstance().getCurrentPosition())
                                    .putExtra("keyword", MusicRecord.getInstance().getKeyWord())
                                    .putExtra("isSearchList", true)
                                    .putExtra("isSDKOutside", isSDKOutside));
                            finish();
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
        MusicRecord.getInstance().stopRefreshSeekBar();
        super.onDestroy();
        unregisterRxBus();
    }
}