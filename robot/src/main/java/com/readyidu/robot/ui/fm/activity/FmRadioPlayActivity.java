package com.readyidu.robot.ui.fm.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.readyidu.baiduvoice.laster.FloatView;
import com.readyidu.baiduvoice.laster.FloatViewInstance;
import com.readyidu.basic.widgets.title.CustomerTitle;
import com.readyidu.robot.R;
import com.readyidu.robot.base.BaseVoiceBarActivity;
import com.readyidu.robot.component.voice.VoiceBar;
import com.readyidu.robot.ui.fm.adapetr.FmAlbumPlayContentAdapter;
import com.readyidu.robot.ui.fm.util.FmPlayUtil;
import com.readyidu.robot.ui.fm.util.RadioScheduleModel;
import com.readyidu.robot.ui.fm.util.ToolUtil;
import com.readyidu.robot.ui.fm.view.LoadCycleImageView;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.schedule.LiveAnnouncer;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.model.live.schedule.ScheduleList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuzhang on 2018/5/30.
 */

public class FmRadioPlayActivity extends BaseVoiceBarActivity {

    CustomerTitle fmRadioPlayHead;
    ImageView fmRadioPlayCover;
    TextView fmRadioPlayTitle;
    TextView fmRadioPlayActor;
    TextView fmRadioPlayTime;
    TextView fmRadioPlayTimeStart;
    TextView fmRadioPlayTimeEnd;
    SeekBar fmRadioPlaySeek;
    ImageButton fmRadioPlayStepsPlayPlay;
    ImageButton fmRadioPlayStepsPlayB;
    ImageButton fmRadioPlayStepsPlayF;
    RelativeLayout fmRadioPlayCrt;
    TextView fmPlayPlayStepsPlayListBtn;
    VoiceBar voiceBar;
    ImageView fmRadioPlayLogo;
    TextView fmRadioPlayCount;
    TextView fmRadioPlayTitleName;
    TextView fmRadioPlayProgramName;
    View fmRadioPlayListBg;
    TabLayout fmRadioPlayListTabs;
    ViewPager fmRadioPlayListViewpager;
    RelativeLayout fmRadioPlayListLayout;
    LoadCycleImageView fmRadioPlayStepsPlayPlayCycle;
    private XmPlayerManager mPlayerManager;
    private List<Track> playList;
    private FmAlbumPlayContentAdapter mFmAlbumPlayContentAdapter;

    private boolean mUpdateProgress = true;

    private int page;
    private Radio radio;
    private List<Schedule> schedules;
    private int scheduleIndex;

//    private String contentMenu[] = {"昨天", "今天", "明天"};
    private String contentMenu[] = {"节目列表"};


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void bindViews() {
        super.bindViews();

        page = 1;

        fmRadioPlayStepsPlayPlayCycle = (LoadCycleImageView)findViewById(R.id.fm_radio_play_steps_play_play_cycle) ;
//        fmRadioPlayStepsPlayPlayCycle.setCycle(true);
//        fmRadioPlayStepsPlayPlayCycle.setVisibility(View.VISIBLE);
        fmRadioPlayHead = (CustomerTitle) findViewById(R.id.fm_radio_play_head);
        fmRadioPlayHead.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fmRadioPlayListBg = (View) findViewById(R.id.fm_radio_play_list_bg);
        fmRadioPlayListBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fmRadioPlayListLayout.setVisibility(View.GONE);
            }
        });
        fmRadioPlayListTabs = (TabLayout) findViewById(R.id.fm_radio_play_list_tabs);
        fmRadioPlayListViewpager = (ViewPager) findViewById(R.id.fm_radio_play_list_viewpager);
        fmRadioPlayListLayout = (RelativeLayout) findViewById(R.id.fm_radio_play_list_layout);


        fmRadioPlayLogo = (ImageView) findViewById(R.id.fm_radio_play_logo);
        fmRadioPlayCount = (TextView) findViewById(R.id.fm_radio_play_count);
        fmRadioPlayTitleName = (TextView) findViewById(R.id.fm_radio_play_title_name);
        fmRadioPlayProgramName = (TextView) findViewById(R.id.fm_radio_play_program_name);

        fmRadioPlayCover = (ImageView) findViewById(R.id.fm_radio_play_cover);
        fmRadioPlayTitle = (TextView) findViewById(R.id.fm_radio_play_title);
        fmRadioPlayActor = (TextView) findViewById(R.id.fm_radio_play_actor);
        fmRadioPlayTime = (TextView) findViewById(R.id.fm_radio_play_time);
        fmRadioPlayTimeStart = (TextView) findViewById(R.id.fm_radio_play_time_start);
        fmRadioPlayTimeEnd = (TextView) findViewById(R.id.fm_radio_play_time_end);
        fmRadioPlaySeek = (SeekBar) findViewById(R.id.fm_radio_play_seek);
        fmRadioPlayStepsPlayPlay = (ImageButton) findViewById(R.id.fm_radio_play_steps_play_play);
        fmRadioPlayStepsPlayPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerManager.isPlaying()) {
                    mPlayerManager.pause();
                } else {
                    mPlayerManager.play();
                }
            }
        });
        fmRadioPlayStepsPlayB = (ImageButton) findViewById(R.id.fm_radio_play_steps_play_b);
        fmRadioPlayStepsPlayB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayerManager.playPre();
            }
        });
        fmRadioPlayStepsPlayF = (ImageButton) findViewById(R.id.fm_radio_play_steps_play_f);
        fmRadioPlayStepsPlayF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayerManager.playNext();
            }
        });
        fmRadioPlayCrt = (RelativeLayout) findViewById(R.id.fm_radio_play_crt);
        fmPlayPlayStepsPlayListBtn = (TextView) findViewById(R.id.fm_play_play_steps_play_list_btn);
        fmPlayPlayStepsPlayListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fmRadioPlayListLayout.setVisibility(View.VISIBLE);
            }
        });
        voiceBar = (VoiceBar) findViewById(R.id.voice_bar);

        radio = getIntent().getParcelableExtra("radio");
        if (radio == null) {

        } else {
            Glide.with(mContext).load(radio.getCoverUrlLarge()).into(fmRadioPlayLogo);
            fmRadioPlayCount.setText(radio.getRadioPlayCount() + "人听过");
            fmRadioPlayTitleName.setText(radio.getRadioName());
            fmRadioPlayProgramName.setText("正在直播:" + radio.getProgramName());
            fmRadioPlayHead.setTitle(radio.getRadioName());
            fmRadioPlayTitle.setText(radio.getProgramName());

            mPlayerManager = FmPlayUtil.getPlayerManager();
            mPlayerManager.addPlayerStatusListener(mPlayerStatusListener);
            mPlayerManager.addAdsStatusListener(mAdsListener);
            mPlayerManager.setPlayMode(XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP);


            scheduleIndex = getIntent().getIntExtra("scheduleIndex", -1);

            if (scheduleIndex == -1) {
                mPlayerManager.playActivityRadio(radio);
            }else{

            }

            RadioScheduleModel radioScheduleModel = new RadioScheduleModel();
            radioScheduleModel.setRadio(radio);
            radioScheduleModel.setScheduleIndex(scheduleIndex);
            FmPlayUtil.setCurRadioScheduleModel(radioScheduleModel);

            initViewPager();
            getSchedules();

            fmRadioPlayStepsPlayPlay.setImageResource(R.mipmap.fm_stop);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        FloatViewInstance.attach(findViewById(android.R.id.content), this, new FloatView.OnMessageResult() {
//            @Override
//            public void onMessageResult(String s) {
//                searchContent = s;
//                requestVoiceBarNet();
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void bindEvents() {
        super.bindEvents();

//        Share data = null;
//        Intent intent = getIntent();
//        if (intent != null) {
//            isSDKOutside = intent.getBooleanExtra("isSDKOutside", false);
//            if (isSDKOutside) {
//                data = intent.getParcelableExtra("data");
//                if (null != data) {
//                    setData(data);
//                } else {
//                    requestData();
//                }
//            } else {
//                data = intent.getParcelableExtra("data");
//                setData(data);
//            }
//
//        }

    }


    protected void initViewPager() {

        fmRadioPlayListViewpager.setOffscreenPageLimit(contentMenu.length);

        //设置适配器
        fmRadioPlayListViewpager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public CharSequence getPageTitle(int position) {
                //展示的标题
                return contentMenu[position];
            }

            @Override
            public Fragment getItem(int position) {
                FmRadioContentPlayFragment subFragment = new FmRadioContentPlayFragment();
                Bundle bundle = new Bundle();
                //把当前位置的标题传递过去
                bundle.putString("radioId", String.valueOf(radio.getDataId()));
                bundle.putInt("dayTab", 1);
                bundle.putLong("liveId", radio.getScheduleID());
                bundle.putInt("index", scheduleIndex);

                subFragment.setArguments(bundle);
                return subFragment;
            }

            @Override
            public int getCount() {
                return contentMenu.length;
            }
        });

        //绑定在一起
        fmRadioPlayListTabs.setupWithViewPager(fmRadioPlayListViewpager);
        fmRadioPlayListViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void bindKeyBoardShowEvent() {

    }

    @Override
    protected void requestVoiceBarNet() {

    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fm_radio_play_layout;
    }

    private IXmPlayerStatusListener mPlayerStatusListener = new IXmPlayerStatusListener() {

        @Override
        public void onSoundPrepared() {
//            Log.i(TAG, "onSoundPrepared");
            fmRadioPlaySeek.setEnabled(true);
//            mProgress.setVisibility(View.GONE);
        }

        @Override
        public void onSoundSwitch(PlayableModel laModel, PlayableModel curModel) {
//            Log.i(TAG, "onSoundSwitch index:" + curModel);
//            PlayableModel model = mPlayerManager.getCurrSound();
//            if (model != null) {
//                String title = null;
//                String coverUrl = null;
//                Track info = (Track) model;
//                title = info.getTrackTitle();
//                coverUrl = info.getCoverUrlLarge();
//
//                fmPlayPlayStepsTitle.setText(title);
//                if (coverUrl != null) {
//                    Glide.with(FmRadioPlayActivity.this).load(coverUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
//                        @Override
//                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                            fmPlayPlayCover.setImageBitmap(resource);
//                        }
//                    });
//                }
//
//                AlbumTrackModel albumTrackModel = new AlbumTrackModel();
//                albumTrackModel.setTrackId(info.getDataId());
//                albumTrackModel.setCurTrackList(playList);
//                albumTrackModel.setSteps(info.getOrderNum());
//                albumTrackModel.setSort(sort);
//                albumTrackModel.setAlbumId(getIntent().getStringExtra("albumId"));
//                albumTrackModel.setAlbumIntro("    " + getIntent().getStringExtra("albumIntro"));
//                albumTrackModel.setTrackTitle(getIntent().getStringExtra("trackTitle"));
//                albumTrackModel.setPlayCount(getIntent().getStringExtra("playCount"));
//                albumTrackModel.setTrackCount(getIntent().getStringExtra("trackCount"));
//                albumTrackModel.setUpdatedAt(getIntent().getStringExtra("updatedAt"));
//                albumTrackModel.setCover(getIntent().getStringExtra("cover"));
//                albumTrackModel.setAlbumTitle(getIntent().getStringExtra("albumTitle"));
//                albumTrackModel.setSelectSteps(getIntent().getBooleanExtra("selectSteps", false));
//                albumTrackModel.setSelectStepsPage(getIntent().getIntExtra("selectStepsPage", 1));
//                FmPlayUtil.setCurAlbumTrackModel(albumTrackModel);
//            }
            updateButtonStatus();
        }


        private void updateButtonStatus() {
            if (mPlayerManager.hasPreSound()) {
                fmRadioPlayStepsPlayB.setEnabled(true);
            } else {
                fmRadioPlayStepsPlayB.setEnabled(false);
            }
            if (mPlayerManager.hasNextSound()) {
                fmRadioPlayStepsPlayF.setEnabled(true);
            } else {
                fmRadioPlayStepsPlayF.setEnabled(false);
            }
        }

        @Override
        public void onPlayStop() {
//            Log.i(TAG, "onPlayStop");
            fmRadioPlayStepsPlayPlay.setImageResource(R.mipmap.fm_play);
        }

        @Override
        public void onPlayStart() {
//            Log.i(TAG, "onPlayStart");
            fmRadioPlayStepsPlayPlay.setImageResource(R.mipmap.fm_stop);
        }

        @Override
        public void onPlayProgress(int currPos, int duration) {
            String title = "";
            PlayableModel info = mPlayerManager.getCurrSound();
            if (info != null && info instanceof Radio) {
                title = ((Radio) info).getRadioName();
            }

//            TextView fmPlayPlayStepsPlayTimeStart;
//            TextView fmPlayPlayStepsPlayTimeEnd;
            fmRadioPlayTimeStart.setText(FmRadioPlayActivity.formatTime(currPos));
            fmRadioPlayTimeEnd.setText(FmRadioPlayActivity.formatTime(duration));
            if (mUpdateProgress && duration != 0) {
                fmRadioPlaySeek.setProgress((int) (100 * currPos / (float) duration));
            }
        }

        @Override
        public void onPlayPause() {
//            Log.i(TAG, "onPlayPause");
            fmRadioPlayStepsPlayPlay.setImageResource(R.mipmap.fm_play);
        }

        @Override
        public void onSoundPlayComplete() {
//            Log.i(TAG, "onSoundPlayComplete");
            fmRadioPlayStepsPlayPlay.setImageResource(R.mipmap.fm_play);
            XmPlayerManager.getInstance(mContext).pause();
        }

        @Override
        public boolean onError(XmPlayerException exception) {
//            Log.i(TAG, "onError " + exception.getMessage());
            fmRadioPlayStepsPlayPlay.setImageResource(R.mipmap.fm_play);
            return false;
        }

        @Override
        public void onBufferProgress(int position) {
            fmRadioPlaySeek.setSecondaryProgress(position);
        }

        public void onBufferingStart() {
            fmRadioPlaySeek.setEnabled(false);
//            mProgress.setVisibility(View.VISIBLE);
        }

        public void onBufferingStop() {
            fmRadioPlaySeek.setEnabled(true);
//            mProgress.setVisibility(View.GONE);
        }

    };

    private IXmAdsStatusListener mAdsListener = new IXmAdsStatusListener() {

        @Override
        public void onStartPlayAds(Advertis ad, int position) {
//            Log.i(TAG, "onStartPlayAds, Ad:" + ad.getName() + ", pos:" + position);
//            if (ad != null) {
//                x.image().bind(mSoundCover ,ad.getImageUrl());
//            }
        }

        @Override
        public void onStartGetAdsInfo() {
//            Log.i(TAG, "onStartGetAdsInfo");
//            fmRadioPlayStepsPlayPlay.setEnabled(false);
//            fmRadioPlaySeek.setEnabled(false);
        }

        @Override
        public void onGetAdsInfo(final AdvertisList ads) {
//            Log.i(TAG, "onGetAdsInfo " + (ads != null));
//            if(ads != null && ads.getAdvertisList() != null && ads.getAdvertisList().size() > 0) {
//                if (ads.getAdvertisList().get(0) != null) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(MainFragmentActivity.this);
//                    LayoutInflater inflater =getLayoutInflater();
//                    View myview = inflater.inflate(R.layout.my_dialog, null);
//                    builder.setView(myview);
//                    myview.findViewById(R.id.imageview).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            AdManager.handlerSoundAdClick(MainFragmentActivity.this ,ads.getAdvertisList().get(0));
//                        }
//                    });
//
//                    x.image().bind((ImageView) myview.findViewById(R.id.imageview), ads.getAdvertisList().get(0).getImageUrl(), new Callback.CommonCallback<Drawable>() {
//                        @Override
//                        public void onSuccess(Drawable drawable) {
//                            AdManager.adRecord(MainFragmentActivity.this ,ads.getAdvertisList().get(0));
//                        }
//
//                        @Override
//                        public void onError(Throwable throwable, boolean b) {
//
//                        }
//
//                        @Override
//                        public void onCancelled(CancelledException e) {
//
//                        }
//
//                        @Override
//                        public void onFinished() {
//
//                        }
//                    });
//                    builder.show();
//
//                }
//            }
        }

        @Override
        public void onError(int what, int extra) {
//            Log.i(TAG, "onError what:" + what + ", extra:" + extra);
        }

        @Override
        public void onCompletePlayAds() {
//            mPlayerManager.play();
//            Log.i(TAG, "onCompletePlayAds");
            fmRadioPlayStepsPlayPlay.setEnabled(true);
            fmRadioPlaySeek.setEnabled(true);
//            PlayableModel model = mPlayerManager.getCurrSound();
//            if (model != null && model instanceof Track) {
//                x.image().bind(mSoundCover ,((Track) model).getCoverUrlLarge());
//            }

//            fmRadioPlayStepsPlayPlayCycle.setCycle(false);
//            fmRadioPlayStepsPlayPlayCycle.setVisibility(View.GONE);
        }

        @Override
        public void onAdsStopBuffering() {
//            Log.i(TAG, "onAdsStopBuffering");
        }

        @Override
        public void onAdsStartBuffering() {
//            Log.i(TAG, "onAdsStartBuffering");
        }
    };

    /**
     * one hour in ms
     */
    private static final int ONE_HOUR = 1 * 60 * 60 * 1000;
    /**
     * one minute in ms
     */
    private static final int ONE_MIN = 1 * 60 * 1000;
    /**
     * one second in ms
     */
    private static final int ONE_SECOND = 1 * 1000;

    public static String formatTime(long ms) {
        StringBuilder sb = new StringBuilder();
        int hour = (int) (ms / ONE_HOUR);
        int min = (int) ((ms % ONE_HOUR) / ONE_MIN);
        int sec = (int) (ms % ONE_MIN) / ONE_SECOND;
        if (hour == 0) {
//			sb.append("00:");
        } else if (hour < 10) {
            sb.append("0").append(hour).append(":");
        } else {
            sb.append(hour).append(":");
        }
        if (min == 0) {
            sb.append("00:");
        } else if (min < 10) {
            sb.append("0").append(min).append(":");
        } else {
            sb.append(min).append(":");
        }
        if (sec == 0) {
            sb.append("00");
        } else if (sec < 10) {
            sb.append("0").append(sec);
        } else {
            sb.append(sec);
        }
        return sb.toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayerManager.removePlayerStatusListener(mPlayerStatusListener);
        mPlayerManager.removeAdsStatusListener(mAdsListener);
    }

    private void getRadio() {
    }


    /**
     * Created by yuzhang on 2018/5/30.
     */

    public static class FmRadioContentFragment extends BaseVoiceBarActivity {

        CustomerTitle fmRadioPlayHead;
        ImageView fmRadioPlayCover;
        TextView fmRadioPlayTitle;
        TextView fmRadioPlayActor;
        TextView fmRadioPlayTime;
        TextView fmRadioPlayTimeStart;
        TextView fmRadioPlayTimeEnd;
        SeekBar fmRadioPlaySeek;
        ImageButton fmRadioPlayStepsPlayPlay;
        ImageButton fmRadioPlayStepsPlayB;
        ImageButton fmRadioPlayStepsPlayF;
        RelativeLayout fmRadioPlayCrt;
        TextView fmPlayPlayStepsPlayListBtn;
        VoiceBar voiceBar;
        ImageView fmRadioPlayLogo;
        TextView fmRadioPlayCount;
        TextView fmRadioPlayTitleName;
        TextView fmRadioPlayProgramName;
        View fmRadioPlayListBg;
        TabLayout fmRadioPlayListTabs;
        ViewPager fmRadioPlayListViewpager;
        RelativeLayout fmRadioPlayListLayout;
        private XmPlayerManager mPlayerManager;
        private List<Track> playList;
        private FmAlbumPlayContentAdapter mFmAlbumPlayContentAdapter;

        private boolean mUpdateProgress = true;

        private int page;
        private String sort;
        private long selectId;

        private boolean initLoad;

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void bindViews() {
            super.bindViews();

            page = 1;

            fmRadioPlayHead = (CustomerTitle) findViewById(R.id.fm_radio_play_head);
            fmRadioPlayHead.setListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            fmRadioPlayListBg = (View) findViewById(R.id.fm_radio_play_list_bg);
            fmRadioPlayListTabs = (TabLayout) findViewById(R.id.fm_radio_play_list_tabs);
            fmRadioPlayListViewpager = (ViewPager) findViewById(R.id.fm_radio_play_list_viewpager);
            fmRadioPlayListLayout = (RelativeLayout) findViewById(R.id.fm_radio_play_list_layout);


            fmRadioPlayLogo = (ImageView) findViewById(R.id.fm_radio_play_logo);
            fmRadioPlayCount = (TextView) findViewById(R.id.fm_radio_play_count);
            fmRadioPlayTitleName = (TextView) findViewById(R.id.fm_radio_play_title_name);
            fmRadioPlayProgramName = (TextView) findViewById(R.id.fm_radio_play_program_name);

            fmRadioPlayCover = (ImageView) findViewById(R.id.fm_radio_play_cover);
            fmRadioPlayTitle = (TextView) findViewById(R.id.fm_radio_play_title);
            fmRadioPlayActor = (TextView) findViewById(R.id.fm_radio_play_actor);
            fmRadioPlayTime = (TextView) findViewById(R.id.fm_radio_play_time);
            fmRadioPlayTimeStart = (TextView) findViewById(R.id.fm_radio_play_time_start);
            fmRadioPlayTimeEnd = (TextView) findViewById(R.id.fm_radio_play_time_end);
            fmRadioPlaySeek = (SeekBar) findViewById(R.id.fm_radio_play_seek);
            fmRadioPlayStepsPlayPlay = (ImageButton) findViewById(R.id.fm_radio_play_steps_play_play);
            fmRadioPlayStepsPlayPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPlayerManager.isPlaying()) {
                        mPlayerManager.pause();
                    } else {
                        mPlayerManager.play();
                    }
                }
            });
            fmRadioPlayStepsPlayB = (ImageButton) findViewById(R.id.fm_radio_play_steps_play_b);
            fmRadioPlayStepsPlayB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPlayerManager.playPre();
                }
            });
            fmRadioPlayStepsPlayF = (ImageButton) findViewById(R.id.fm_radio_play_steps_play_f);
            fmRadioPlayStepsPlayF.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPlayerManager.playNext();
                }
            });
            fmRadioPlayCrt = (RelativeLayout) findViewById(R.id.fm_radio_play_crt);
            fmPlayPlayStepsPlayListBtn = (TextView) findViewById(R.id.fm_play_play_steps_play_list_btn);
            voiceBar = (VoiceBar) findViewById(R.id.voice_bar);

            Radio radio = getIntent().getParcelableExtra("radio");
            if (radio == null) {

            } else {
                Glide.with(mContext).load(radio.getCoverUrlLarge()).into(fmRadioPlayLogo);
                fmRadioPlayCount.setText(radio.getRadioPlayCount() + "人听过");
                fmRadioPlayTitleName.setText(radio.getRadioName());
                fmRadioPlayProgramName.setText("正在直播:" + radio.getProgramName());
                fmRadioPlayHead.setTitle(radio.getRadioName());
                fmRadioPlayTitle.setText(radio.getProgramName());
                fmRadioPlayActor.setText(radio.getProgramName());
                fmRadioPlayTime.setText(radio.getStartTime() + "-" + radio.getEndTime());

                mPlayerManager = FmPlayUtil.getPlayerManager();
                mPlayerManager.addPlayerStatusListener(mPlayerStatusListener);
                mPlayerManager.addAdsStatusListener(mAdsListener);
                mPlayerManager.setPlayMode(XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP);
                mPlayerManager.playActivityRadio(radio);
            }
        }

        @Override
        protected void onResume() {
            super.onResume();
            FloatViewInstance.attach(findViewById(android.R.id.content), this, new FloatView.OnMessageResult() {
                @Override
                public void onMessageResult(String s) {
                    searchContent = s;
                    requestVoiceBarNet();
                }
            });
        }

        @Override
        protected void onStart() {
            super.onStart();
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        protected void bindEvents() {
            super.bindEvents();

            //        Share data = null;
            //        Intent intent = getIntent();
            //        if (intent != null) {
            //            isSDKOutside = intent.getBooleanExtra("isSDKOutside", false);
            //            if (isSDKOutside) {
            //                data = intent.getParcelableExtra("data");
            //                if (null != data) {
            //                    setData(data);
            //                } else {
            //                    requestData();
            //                }
            //            } else {
            //                data = intent.getParcelableExtra("data");
            //                setData(data);
            //            }
            //
            //        }

        }

        @Override
        protected void bindKeyBoardShowEvent() {

        }

        @Override
        protected void requestVoiceBarNet() {

        }

        @Override
        protected int getContentViewLayoutID() {
            return R.layout.fm_radio_play_layout;
        }

        private IXmPlayerStatusListener mPlayerStatusListener = new IXmPlayerStatusListener() {

            @Override
            public void onSoundPrepared() {
                //            Log.i(TAG, "onSoundPrepared");
                fmRadioPlaySeek.setEnabled(true);
                //            mProgress.setVisibility(View.GONE);
            }

            @Override
            public void onSoundSwitch(PlayableModel laModel, PlayableModel curModel) {
//                            PlayableModel model = mPlayerManager.getCurrSound();
//                            if (model != null && model instanceof Radio) {
//                                String title = null;
//                                String coverUrl = null;
//                                Radio info = (Radio) model;
////                                title = info.getTrackTitle();
//                                coverUrl = info.getCoverUrlLarge();

//                                fmPlayPlayStepsTitle.setText(title);
//                                if (coverUrl != null) {
//                                    Glide.with(FmRadioPlayActivity.this).load(coverUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
//                                        @Override
//                                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                                            fmPlayPlayCover.setImageBitmap(resource);
//                                        }
//                                    });
//                                }
//
//                                AlbumTrackModel albumTrackModel = new AlbumTrackModel();
//                                albumTrackModel.setTrackId(info.getDataId());
//                                albumTrackModel.setCurTrackList(playList);
//                                albumTrackModel.setSteps(info.getOrderNum());
//                                albumTrackModel.setSort(sort);
//                                albumTrackModel.setAlbumId(getIntent().getStringExtra("albumId"));
//                                albumTrackModel.setAlbumIntro("    " + getIntent().getStringExtra("albumIntro"));
//                                albumTrackModel.setTrackTitle(getIntent().getStringExtra("trackTitle"));
//                                albumTrackModel.setPlayCount(getIntent().getStringExtra("playCount"));
//                                albumTrackModel.setTrackCount(getIntent().getStringExtra("trackCount"));
//                                albumTrackModel.setUpdatedAt(getIntent().getStringExtra("updatedAt"));
//                                albumTrackModel.setCover(getIntent().getStringExtra("cover"));
//                                albumTrackModel.setAlbumTitle(getIntent().getStringExtra("albumTitle"));
//                                albumTrackModel.setSelectSteps(getIntent().getBooleanExtra("selectSteps", false));
//                                albumTrackModel.setSelectStepsPage(getIntent().getIntExtra("selectStepsPage", 1));
//                                FmPlayUtil.setCurAlbumTrackModel(albumTrackModel);
//                            }
                updateButtonStatus();
            }


            private void updateButtonStatus() {
                if (mPlayerManager.hasPreSound()) {
                    fmRadioPlayStepsPlayB.setEnabled(true);
                } else {
                    fmRadioPlayStepsPlayB.setEnabled(false);
                }
                if (mPlayerManager.hasNextSound()) {
                    fmRadioPlayStepsPlayF.setEnabled(true);
                } else {
                    fmRadioPlayStepsPlayF.setEnabled(false);
                }
            }

            @Override
            public void onPlayStop() {
                //            Log.i(TAG, "onPlayStop");
                fmRadioPlayStepsPlayPlay.setImageResource(R.mipmap.fm_play);
            }

            @Override
            public void onPlayStart() {
                //            Log.i(TAG, "onPlayStart");
                fmRadioPlayStepsPlayPlay.setImageResource(R.mipmap.fm_stop);
            }

            @Override
            public void onPlayProgress(int currPos, int duration) {
                String title = "";
                PlayableModel info = mPlayerManager.getCurrSound();
                if (info != null) {
                    title = ((Radio) info).getRadioName();
                }

                //            TextView fmPlayPlayStepsPlayTimeStart;
                //            TextView fmPlayPlayStepsPlayTimeEnd;
                fmRadioPlayTimeStart.setText(ToolUtil.formatTime(currPos));
                fmRadioPlayTimeEnd.setText(ToolUtil.formatTime(duration));
                if (mUpdateProgress && duration != 0) {
                    fmRadioPlaySeek.setProgress((int) (100 * currPos / (float) duration));
                }
            }

            @Override
            public void onPlayPause() {
                //            Log.i(TAG, "onPlayPause");
                fmRadioPlayStepsPlayPlay.setImageResource(R.mipmap.fm_play);
            }

            @Override
            public void onSoundPlayComplete() {
                //            Log.i(TAG, "onSoundPlayComplete");
                fmRadioPlayStepsPlayPlay.setImageResource(R.mipmap.fm_play);
                XmPlayerManager.getInstance(mContext).pause();
            }

            @Override
            public boolean onError(XmPlayerException exception) {
                //            Log.i(TAG, "onError " + exception.getMessage());
                fmRadioPlayStepsPlayPlay.setImageResource(R.mipmap.fm_play);
                return false;
            }

            @Override
            public void onBufferProgress(int position) {
                fmRadioPlaySeek.setSecondaryProgress(position);
            }

            public void onBufferingStart() {
                fmRadioPlaySeek.setEnabled(false);
                //            mProgress.setVisibility(View.VISIBLE);
            }

            public void onBufferingStop() {
                fmRadioPlaySeek.setEnabled(true);
                //            mProgress.setVisibility(View.GONE);
            }

        };

        private IXmAdsStatusListener mAdsListener = new IXmAdsStatusListener() {

            @Override
            public void onStartPlayAds(Advertis ad, int position) {
                //            Log.i(TAG, "onStartPlayAds, Ad:" + ad.getName() + ", pos:" + position);
                //            if (ad != null) {
                //                x.image().bind(mSoundCover ,ad.getImageUrl());
                //            }
            }

            @Override
            public void onStartGetAdsInfo() {
                //            Log.i(TAG, "onStartGetAdsInfo");
                fmRadioPlayStepsPlayPlay.setEnabled(false);
                fmRadioPlaySeek.setEnabled(false);
            }

            @Override
            public void onGetAdsInfo(final AdvertisList ads) {
            }

            @Override
            public void onError(int what, int extra) {
                //            Log.i(TAG, "onError what:" + what + ", extra:" + extra);
            }

            @Override
            public void onCompletePlayAds() {
                //            mPlayerManager.play();
                //            Log.i(TAG, "onCompletePlayAds");
                fmRadioPlayStepsPlayPlay.setEnabled(true);
                fmRadioPlaySeek.setEnabled(true);
                //            PlayableModel model = mPlayerManager.getCurrSound();
                //            if (model != null && model instanceof Track) {
                //                x.image().bind(mSoundCover ,((Track) model).getCoverUrlLarge());
                //            }
            }

            @Override
            public void onAdsStopBuffering() {
                //            Log.i(TAG, "onAdsStopBuffering");
            }

            @Override
            public void onAdsStartBuffering() {
                //            Log.i(TAG, "onAdsStartBuffering");
            }
        };
    }

    private void getSchedules() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.RADIOID, String.valueOf(radio.getDataId()));
        CommonRequest.getSchedules(map, new IDataCallBack<ScheduleList>() {
            @Override
            public void onSuccess(@Nullable ScheduleList scheduleList) {
                List<Schedule> schedules = scheduleList.getmScheduleList();
                for (Schedule schedule : schedules) {
                    if (schedule.getDataId() == radio.getScheduleID()) {
                        List<LiveAnnouncer> announcers = schedule.getRelatedProgram().getAnnouncerList();
                        if (announcers.size() > 0) {
                            StringBuffer announcerSb = new StringBuffer();
                            for (LiveAnnouncer liveAnnouncer : announcers) {
                                announcerSb.append(liveAnnouncer.getNickName() + " ");
                            }
                            fmRadioPlayActor.setText(announcerSb.toString());
                        } else {
                            fmRadioPlayActor.setText("未知");
                        }
                        fmRadioPlayTime.setText(schedule.getStartTime() + "-" + schedule.getEndTime());

                    }
                }
                if (scheduleIndex != -1) {
                    playRadio(schedules, scheduleIndex);
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    public void playRadio(List<Schedule> schedules, int startIndex) {
        RadioScheduleModel radioScheduleModel = FmPlayUtil.getCurRadioScheduleModel();
        if (radioScheduleModel != null) {
            radioScheduleModel.setScheduleIndex(startIndex);
            radioScheduleModel.setSchedules(schedules);
            FmPlayUtil.setCurRadioScheduleModel(radioScheduleModel);
        }
        mPlayerManager.playSchedule(schedules, startIndex);
    }

}
