package com.readyidu.robot.ui.fm.activity;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.readyidu.basic.widgets.title.CustomerTitle;
import com.readyidu.robot.R;
import com.readyidu.robot.base.BaseVoiceBarActivity;
import com.readyidu.robot.component.voice.VoiceBar;
import com.readyidu.robot.ui.fm.adapetr.FmAlbumPlayContentAdapter;
import com.readyidu.robot.ui.fm.util.AlbumTrackModel;
import com.readyidu.robot.ui.fm.util.FmPlayUtil;
import com.readyidu.robot.ui.fm.util.ToolUtil;
import com.readyidu.robot.ui.fm.view.CycleImageView;
import com.readyidu.robot.ui.fm.view.LoadCycleImageView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
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

public class FmPlayActivity extends BaseVoiceBarActivity {

    CustomerTitle fmPlayHead;
    TextView fmPlayTitle;
    TextView fmPlayPlayCount;
    TextView fmPlayPlayTime;
    CycleImageView fmPlayPlayCover;
    VoiceBar voiceBar;
    TextView fmPlayPlayStepsTitle;
    TextView fmPlayPlayStepsPlayPerson;
    TextView fmPlayPlayStepsPlayTimeStart;
    TextView fmPlayPlayStepsPlayTimeEnd;
    SeekBar fmPlayPlayStepsPlaySeek;
    ImageButton fmPlayPlayStepsPlayPlay;
    ImageButton fmPlayPlayStepsPlayB;
    ImageButton fmPlayPlayStepsPlayF;
    TextView fmPlayPlayStepsPlayListBtn;
    TextView fmPlayPlaySum;
    ImageView fmPlayPlayStepsPlayListPlayAll;
    TextView fmPlayPlayStepsPlayListPlayAllTv;
    ImageButton fmPlayPlayStepsPlayListPlaySortImg;
    Button fmPlayPlayStepsPlayListPlaySortTv;
    ListView fmPlayPlayStepsPlayList;
    RelativeLayout fmPlayPlayStepsPlayListLayout;
    SmartRefreshLayout fmPlayPlayStepsPlayListRefresh;
    View fmPlayPlayStepsPlayListBg;
    LoadCycleImageView fmPlayPlayStepsPlayPlayCycle;

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

        page = getIntent().getIntExtra("selectStepsPage", 1);
        sort = getIntent().getStringExtra("sort");
        if (sort == null) {
            sort = "asc";
        }

        fmPlayHead = (CustomerTitle) findViewById(R.id.fm_play_head);
        fmPlayHead.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        fmPlayTitle = (TextView) findViewById(R.id.fm_play_title);
        fmPlayPlayCount = (TextView) findViewById(R.id.fm_play_play_count);
        fmPlayPlayTime = (TextView) findViewById(R.id.fm_play_play_time);
        fmPlayPlayStepsTitle = (TextView) findViewById(R.id.fm_play_play_steps_title);
        fmPlayPlayStepsPlayPerson = (TextView) findViewById(R.id.fm_play_play_steps_play_person);
        fmPlayPlayCover = (CycleImageView) findViewById(R.id.fm_play_play_cover);
        voiceBar = (VoiceBar) findViewById(R.id.voice_bar);

        fmPlayPlayStepsPlayListBg = (View) findViewById(R.id.fm_play_play_steps_play_list_bg);
        fmPlayPlayStepsPlayListBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fmPlayPlayStepsPlayListLayout.setVisibility(View.GONE);
            }
        });
        fmPlayPlayStepsPlayListPlayAll = (ImageView) findViewById(R.id.fm_play_play_steps_play_list_play_all);
        fmPlayPlayStepsPlayListPlayAllTv = (TextView) findViewById(R.id.fm_play_play_steps_play_list_play_all_tv);
        fmPlayPlayStepsPlayListPlaySortImg = (ImageButton) findViewById(R.id.fm_play_play_steps_play_list_play_sort_img);
        fmPlayPlayStepsPlayListPlaySortImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page = 1;
                if (sort.equals("desc")) {
                    sort = "asc";
                    fmPlayPlayStepsPlayListPlaySortTv.setText("正序");
                    fmPlayPlayStepsPlayListPlaySortImg.setImageResource(R.mipmap.fm_sort_down);
                } else {
                    sort = "desc";
                    fmPlayPlayStepsPlayListPlaySortTv.setText("倒序");
                    fmPlayPlayStepsPlayListPlaySortImg.setImageResource(R.mipmap.fm_sort_up);
                }
                getTracks();
            }
        });
        fmPlayPlayStepsPlayListPlaySortTv = (Button) findViewById(R.id.fm_play_play_steps_play_list_play_sort_tv);
        fmPlayPlayStepsPlayListPlaySortTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page = 1;
                if (sort.equals("desc")) {
                    sort = "asc";
                    fmPlayPlayStepsPlayListPlaySortTv.setText("正序");
                    fmPlayPlayStepsPlayListPlaySortImg.setImageResource(R.mipmap.fm_sort_down);
                } else {
                    sort = "desc";
                    fmPlayPlayStepsPlayListPlaySortTv.setText("倒序");
                    fmPlayPlayStepsPlayListPlaySortImg.setImageResource(R.mipmap.fm_sort_up);
                }
                getTracks();
            }
        });
        fmPlayPlayStepsPlayList = (ListView) findViewById(R.id.fm_play_play_steps_play_list);
        fmPlayPlayStepsPlayList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mFmAlbumPlayContentAdapter.setSelectId(playList.get(position).getDataId());
                mPlayerManager.playList(playList, position);
                fmPlayPlayStepsPlayPlay.setImageResource(R.mipmap.fm_stop);
                fmPlayPlayCover.setCycle(true);
            }
        });
        fmPlayPlayStepsPlayListLayout = (RelativeLayout) findViewById(R.id.fm_play_play_steps_play_list_layout);
        fmPlayPlayStepsPlayListRefresh = (SmartRefreshLayout) findViewById(R.id.fm_play_play_steps_play_list_refresh);
        fmPlayPlayStepsPlayListRefresh.setEnableRefresh(false);
        fmPlayPlayStepsPlayListRefresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                page++;
                getTracks();
            }
        });


        fmPlayPlayStepsPlayTimeStart = (TextView) findViewById(R.id.fm_play_play_steps_play_time_start);
        fmPlayPlayStepsPlayTimeEnd = (TextView) findViewById(R.id.fm_play_play_steps_play_time_end);
        fmPlayPlayStepsPlaySeek = (SeekBar) findViewById(R.id.fm_play_play_steps_play_seek);
        fmPlayPlayStepsPlaySeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mPlayerManager.seekToByPercent(seekBar.getProgress() / (float) seekBar.getMax());
                mUpdateProgress = true;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mUpdateProgress = false;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
        });
        fmPlayPlayStepsPlayPlayCycle = (LoadCycleImageView)findViewById(R.id.fm_play_play_steps_play_play_cycle);
        fmPlayPlayStepsPlayPlay = (ImageButton) findViewById(R.id.fm_play_play_steps_play_play);
        fmPlayPlayStepsPlayPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerManager.isPlaying()) {
                    mPlayerManager.pause();
                    fmPlayPlayCover.setCycle(false);
                } else {
                    mPlayerManager.play();
                    fmPlayPlayCover.setCycle(true);
                }
            }
        });
        fmPlayPlayStepsPlayB = (ImageButton) findViewById(R.id.fm_play_play_steps_play_b);
        fmPlayPlayStepsPlayB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayerManager.playPre();
            }
        });
        fmPlayPlayStepsPlayF = (ImageButton) findViewById(R.id.fm_play_play_steps_play_f);
        fmPlayPlayStepsPlayF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayerManager.playNext();
            }
        });
        fmPlayPlayStepsPlayListBtn = (TextView) findViewById(R.id.fm_play_play_steps_play_list_btn);
        fmPlayPlayStepsPlayListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fmPlayPlayStepsPlayListLayout.setVisibility(View.VISIBLE);
            }
        });
        fmPlayPlaySum = (TextView) findViewById(R.id.fm_play_play_sum);


        String cover = getIntent().getStringExtra("cover");
        if (cover != null) {
            Glide.with(FmPlayActivity.this).load(cover).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    fmPlayPlayCover.setImageBitmap(resource);
                }
            });
        }
        fmPlayTitle.setText(getIntent().getStringExtra("albumTitle"));
        fmPlayPlayStepsTitle.setText(getIntent().getStringExtra("albumTitle"));
        fmPlayPlayCount.setText(getIntent().getStringExtra("playCount") + "次 | " + getIntent().getStringExtra("trackCount") + "集");
        fmPlayPlayTime.setText(getIntent().getStringExtra("updatedAt"));
        fmPlayPlayStepsTitle.setText(getIntent().getStringExtra("trackTitle"));
        playList = getIntent().getParcelableArrayListExtra("trackList");
        fmPlayPlaySum.setText("    " + getIntent().getStringExtra("albumIntro"));
        final long trackId = getIntent().getLongExtra("trackId", 0);
        final int steps = getIntent().getIntExtra("steps", 0);

        fmPlayPlayStepsPlayPerson.setText("与您一起收听的还有" + getIntent().getStringExtra("playCount") + "人");
        mFmAlbumPlayContentAdapter = new FmAlbumPlayContentAdapter(this);
//        mFmAlbumPlayContentAdapter.setData(playList);
        fmPlayPlayStepsPlayList.setAdapter(mFmAlbumPlayContentAdapter);

        mPlayerManager = FmPlayUtil.getPlayerManager();
        mPlayerManager.addPlayerStatusListener(mPlayerStatusListener);
        mPlayerManager.addAdsStatusListener(mAdsListener);
        mPlayerManager.setPlayMode(XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP);
        selectId = trackId;
        mFmAlbumPlayContentAdapter.setSelectId(trackId);
        mPlayerManager.playList(playList, steps);

//        AlbumTrackModel albumTrackModel = new AlbumTrackModel();
//        albumTrackModel.setCurTrackList(playList);
//        albumTrackModel.setSteps(steps);
//        albumTrackModel.setSort(sort);
//        albumTrackModel.setAlbumId(getIntent().getStringExtra("albumId"));
//        albumTrackModel.setAlbumIntro("    " + getIntent().getStringExtra("albumIntro"));
//        albumTrackModel.setTrackTitle(getIntent().getStringExtra("trackTitle"));
//        albumTrackModel.setPlayCount(getIntent().getStringExtra("playCount"));
//        albumTrackModel.setTrackCount(getIntent().getStringExtra("trackCount"));
//        albumTrackModel.setUpdatedAt(getIntent().getStringExtra("updatedAt"));
//        albumTrackModel.setCover(getIntent().getStringExtra("cover"));
//        albumTrackModel.setAlbumTitle(getIntent().getStringExtra("albumTitle"));
//        FmPlayUtil.setCurAlbumTrackModel(albumTrackModel);
        fmPlayPlayStepsPlayPlay.setImageResource(R.mipmap.fm_stop);
        fmPlayPlayCover.setCycle(true);

        getInitTracks();
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

    @Override
    protected void bindKeyBoardShowEvent() {

    }

    @Override
    protected void requestVoiceBarNet() {

    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fm_play_layout;
    }

    private IXmPlayerStatusListener mPlayerStatusListener = new IXmPlayerStatusListener() {

        @Override
        public void onSoundPrepared() {
//            Log.i(TAG, "onSoundPrepared");
            fmPlayPlayStepsPlaySeek.setEnabled(true);
//            mProgress.setVisibility(View.GONE);
        }

        @Override
        public void onSoundSwitch(PlayableModel laModel, PlayableModel curModel) {
//            Log.i(TAG, "onSoundSwitch index:" + curModel);
            PlayableModel model = mPlayerManager.getCurrSound();
            if (model != null) {
                String title = null;
                String coverUrl = null;
                Track info = (Track) model;
                title = info.getTrackTitle();
                coverUrl = info.getCoverUrlLarge();

                fmPlayPlayStepsTitle.setText(title);
                if (coverUrl != null) {
                    Glide.with(FmPlayActivity.this).load(coverUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            fmPlayPlayCover.setImageBitmap(resource);
                        }
                    });
                }

                AlbumTrackModel albumTrackModel = new AlbumTrackModel();
                albumTrackModel.setTrackId(info.getDataId());
                albumTrackModel.setCurTrackList(playList);
                albumTrackModel.setSteps(info.getOrderNum());
                albumTrackModel.setSort(sort);
                albumTrackModel.setAlbumId(getIntent().getStringExtra("albumId"));
                albumTrackModel.setAlbumIntro("    " + getIntent().getStringExtra("albumIntro"));
                albumTrackModel.setTrackTitle(getIntent().getStringExtra("trackTitle"));
                albumTrackModel.setPlayCount(getIntent().getStringExtra("playCount"));
                albumTrackModel.setTrackCount(getIntent().getStringExtra("trackCount"));
                albumTrackModel.setUpdatedAt(getIntent().getStringExtra("updatedAt"));
                albumTrackModel.setCover(getIntent().getStringExtra("cover"));
                albumTrackModel.setAlbumTitle(getIntent().getStringExtra("albumTitle"));
                albumTrackModel.setSelectSteps(getIntent().getBooleanExtra("selectSteps", false));
                albumTrackModel.setSelectStepsPage(getIntent().getIntExtra("selectStepsPage", 1));
                FmPlayUtil.setCurAlbumTrackModel(albumTrackModel);
            }
            updateButtonStatus();
        }


        private void updateButtonStatus() {
            if (mPlayerManager.hasPreSound()) {
                fmPlayPlayStepsPlayB.setEnabled(true);
            } else {
                fmPlayPlayStepsPlayB.setEnabled(false);
            }
            if (mPlayerManager.hasNextSound()) {
                fmPlayPlayStepsPlayF.setEnabled(true);
            } else {
                fmPlayPlayStepsPlayF.setEnabled(false);
            }
        }

        @Override
        public void onPlayStop() {
//            Log.i(TAG, "onPlayStop");
            fmPlayPlayStepsPlayPlay.setImageResource(R.mipmap.fm_play);
        }

        @Override
        public void onPlayStart() {
//            Log.i(TAG, "onPlayStart");
            fmPlayPlayStepsPlayPlay.setImageResource(R.mipmap.fm_stop);
        }

        @Override
        public void onPlayProgress(int currPos, int duration) {
            String title = "";
            PlayableModel info = mPlayerManager.getCurrSound();
            if (info != null) {
                title = ((Track) info).getTrackTitle();
            }

//            TextView fmPlayPlayStepsPlayTimeStart;
//            TextView fmPlayPlayStepsPlayTimeEnd;
            fmPlayPlayStepsPlayTimeStart.setText(ToolUtil.formatTime(currPos));
            fmPlayPlayStepsPlayTimeEnd.setText(ToolUtil.formatTime(duration));
            if (mUpdateProgress && duration != 0) {
                fmPlayPlayStepsPlaySeek.setProgress((int) (100 * currPos / (float) duration));
            }
        }

        @Override
        public void onPlayPause() {
//            Log.i(TAG, "onPlayPause");
            fmPlayPlayStepsPlayPlay.setImageResource(R.mipmap.fm_play);
        }

        @Override
        public void onSoundPlayComplete() {
//            Log.i(TAG, "onSoundPlayComplete");
            fmPlayPlayStepsPlayPlay.setImageResource(R.mipmap.fm_play);
            XmPlayerManager.getInstance(mContext).pause();
        }

        @Override
        public boolean onError(XmPlayerException exception) {
//            Log.i(TAG, "onError " + exception.getMessage());
            fmPlayPlayStepsPlayPlay.setImageResource(R.mipmap.fm_play);
            return false;
        }

        @Override
        public void onBufferProgress(int position) {
            fmPlayPlayStepsPlaySeek.setSecondaryProgress(position);
        }

        public void onBufferingStart() {
            fmPlayPlayStepsPlaySeek.setEnabled(false);
//            mProgress.setVisibility(View.VISIBLE);
        }

        public void onBufferingStop() {
            fmPlayPlayStepsPlaySeek.setEnabled(true);
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
            fmPlayPlayStepsPlayPlay.setEnabled(false);
            fmPlayPlayStepsPlaySeek.setEnabled(false);

            fmPlayPlayStepsPlayPlayCycle.setVisibility(View.VISIBLE);
            fmPlayPlayStepsPlayPlayCycle.setCycle(true);
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
            fmPlayPlayStepsPlayPlay.setEnabled(true);
            fmPlayPlayStepsPlaySeek.setEnabled(true);
//            PlayableModel model = mPlayerManager.getCurrSound();
//            if (model != null && model instanceof Track) {
//                x.image().bind(mSoundCover ,((Track) model).getCoverUrlLarge());
//            }
            fmPlayPlayStepsPlayPlayCycle.setCycle(false);
            fmPlayPlayStepsPlayPlayCycle.setVisibility(View.GONE);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayerManager.removePlayerStatusListener(mPlayerStatusListener);
        mPlayerManager.removeAdsStatusListener(mAdsListener);
    }

    private void getInitTracks() {
        Map<String, String> map = new HashMap<String, String>();
        if (getIntent().getBooleanExtra("selectSteps", false)) {
            fmPlayPlayStepsPlayListRefresh.setEnableLoadmore(false);
            fmPlayPlayStepsPlayListRefresh.setEnableRefresh(false);
            map.put(DTransferConstants.PAGE_SIZE, "50");
        } else if (playList != null && playList.size() > 0) {
            initLoad = true;
            mFmAlbumPlayContentAdapter.setData(playList,sort,Integer.parseInt(getIntent().getStringExtra("trackCount")),false);
        }

        if (!initLoad) {
            initLoad = false;
            map.put(DTransferConstants.ALBUM_ID, getIntent().getStringExtra("albumId"));
            map.put(DTransferConstants.SORT, sort);
            map.put(DTransferConstants.PAGE, String.valueOf(page));
            CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {
                @Override
                public void onSuccess(@Nullable TrackList trackList) {
                    if (page == 1) {
                        if(getIntent().getBooleanExtra("selectSteps", false)){
                            mFmAlbumPlayContentAdapter.setData(playList,(getIntent().getIntExtra("selectStepsPage", 1) - 1) * 50,sort,Integer.parseInt(getIntent().getStringExtra("trackCount")),true);
                        }else{
                            mFmAlbumPlayContentAdapter.setData(playList,sort,Integer.parseInt(getIntent().getStringExtra("trackCount")),false);
                        }
                    } else {
                        if(getIntent().getBooleanExtra("selectSteps", false)){
                            mFmAlbumPlayContentAdapter.addData(trackList.getTracks(),(getIntent().getIntExtra("selectStepsPage", 1) - 1) * 50,sort,Integer.parseInt(getIntent().getStringExtra("trackCount")),true);
                        }else{
                            mFmAlbumPlayContentAdapter.addData(trackList.getTracks(),0,sort,Integer.parseInt(getIntent().getStringExtra("trackCount")),false);
                        }
                    }
                    mFmAlbumPlayContentAdapter.notifyDataSetChanged();
                    fmPlayPlayStepsPlayListRefresh.finishRefresh();
                    fmPlayPlayStepsPlayListRefresh.finishLoadmore();
                    playList = mFmAlbumPlayContentAdapter.getData();
                }

                @Override
                public void onError(int i, String s) {
                    fmPlayPlayStepsPlayListRefresh.finishRefresh();
                    fmPlayPlayStepsPlayListRefresh.finishLoadmore();
                }
            });
        }
    }

    private void getTracks() {
        Map<String, String> map = new HashMap<String, String>();
        if (getIntent().getBooleanExtra("selectSteps", false)) {
            map.put(DTransferConstants.PAGE_SIZE, "50");
            page = getIntent().getIntExtra("selectStepsPage", 1);
            fmPlayPlayStepsPlayListRefresh.setEnableLoadmore(false);
            fmPlayPlayStepsPlayListRefresh.setEnableRefresh(false);
        }
        map.put(DTransferConstants.ALBUM_ID, getIntent().getStringExtra("albumId"));
        map.put(DTransferConstants.SORT, sort);
        map.put(DTransferConstants.PAGE, String.valueOf(page));
        CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(@Nullable TrackList trackList) {
                if(getIntent().getBooleanExtra("selectSteps", false)){
                    mFmAlbumPlayContentAdapter.setData(playList,(getIntent().getIntExtra("selectStepsPage", 1) - 1) * 50,sort,Integer.parseInt(getIntent().getStringExtra("trackCount")),false);
                }else{
                    mFmAlbumPlayContentAdapter.setData(playList,sort,Integer.parseInt(getIntent().getStringExtra("trackCount")),false);
                }
                mFmAlbumPlayContentAdapter.notifyDataSetChanged();
                fmPlayPlayStepsPlayListRefresh.finishRefresh();
                fmPlayPlayStepsPlayListRefresh.finishLoadmore();
                playList = mFmAlbumPlayContentAdapter.getData();
            }

            @Override
            public void onError(int i, String s) {
                fmPlayPlayStepsPlayListRefresh.finishRefresh();
                fmPlayPlayStepsPlayListRefresh.finishLoadmore();
            }
        });
    }
}
