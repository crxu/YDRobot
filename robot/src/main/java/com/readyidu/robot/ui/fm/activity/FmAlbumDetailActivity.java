package com.readyidu.robot.ui.fm.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.readyidu.baiduvoice.laster.FloatView;
import com.readyidu.baiduvoice.laster.FloatViewInstance;
import com.readyidu.basic.models.LoginUser;
import com.readyidu.basic.utils.DialogUtils;
import com.readyidu.basic.widgets.title.CustomerTitle;
import com.readyidu.robot.R;
import com.readyidu.robot.api.NetSubscriber;
import com.readyidu.robot.api.impl.RobotServiceImpl;
import com.readyidu.robot.base.BaseVoiceBarActivity;
import com.readyidu.robot.component.voice.VoiceBar;
import com.readyidu.robot.model.BaseModel;
import com.readyidu.robot.ui.fm.adapetr.FmAlbumContentAdapter;
import com.readyidu.robot.ui.fm.adapetr.FmStepslAdapter;
import com.readyidu.robot.ui.fm.util.AlbumTrackModel;
import com.readyidu.robot.ui.fm.util.FmPlayUtil;
import com.readyidu.robot.ui.fm.util.RadioScheduleModel;
import com.readyidu.robot.ui.fm.view.CircleImageView;
import com.readyidu.robot.ui.fm.view.CycleImageView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuzhang on 2018/5/28.
 */

public class FmAlbumDetailActivity extends BaseVoiceBarActivity {

    CustomerTitle fmAlbumDetailTitle;
    ImageView fmAlbumDetailCover;
    TextView fmAlbumDetailTitleTv;
    CircleImageView fmAlbumDetailWriterImg;
    TextView fmAlbumDetailWriter;
    TextView fmAlbumDetailTime;
    TextView fmAlbumDetailPlayCount;
    TextView fmAlbumDetailPlaySteps;
    RelativeLayout fmAlbumDetailHead;
    LinearLayout fmAlbumDetailPlayTab;
    ImageView fmAlbumDetailPlayControlPlay;
    TextView fmAlbumDetailPlayControlPlayTv;
    ImageButton fmAlbumDetailPlayControlStepsImg;
    Button fmAlbumDetailPlayControlStepsTv;
    ImageButton fmAlbumDetailPlayControlSortImg;
    TextView fmAlbumDetailPlayControlSortTv;
    RelativeLayout fmAlbumDetailPlayControl;
    ListView fmAlbumDetailList;
    VoiceBar voiceBar;
    SmartRefreshLayout fmAlbumDetailRefresh;
    Button fmAlbumDetailPlayTabSum;
    Button fmAlbumDetailPlayTabTrack;
    TextView fmAlbumDetailSumTv;
    ScrollView fmAlbumDetailSumSv;
    RecyclerView fmAlbumDetailPlayControlStepsRv;
    CycleImageView fmAlbumDetailPlayControlBottomPlayCover;
    RelativeLayout fmAlbumDetailPlayControlBottomPlay;

    private int page;
    private FmAlbumContentAdapter mFmAlbumContentAdapter;
    private int tab;
    private String sort;
    private String albumTitle;
    private int trackCount;
    private String updatedAt;
    private String albumIntro;
    private String albumId;
    private Album album;

    private FmStepslAdapter mFmStepslAdapter;
    private boolean selectSteps;
    private int selectStepsPage;

    private boolean allPlay;

    private XmPlayerManager mPlayerManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void bindViews() {
        super.bindViews();

        page = 1;
        sort = "asc";
        albumId = getIntent().getStringExtra("albumId");
        album = getIntent().getParcelableExtra("album");

        fmAlbumDetailPlayControlBottomPlayCover = (CycleImageView) findViewById(R.id.fm_album_detail_play_control_bottom_play_cover);
        fmAlbumDetailPlayControlBottomPlay = (RelativeLayout) findViewById(R.id.fm_album_detail_play_control_bottom_play);
        fmAlbumDetailPlayControlBottomPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (FmPlayUtil.getCurAlbumTrackModel() == null) {
                    RadioScheduleModel model = FmPlayUtil.getCurRadioScheduleModel();
                    Intent playIntent = new Intent(FmAlbumDetailActivity.this, FmRadioPlayActivity.class);
//                    playIntent.putParcelableArrayListExtra("schedules", (ArrayList<? extends Parcelable>) model.getSchedules());
                    playIntent.putExtra("radio", model.getRadio());
                    playIntent.putExtra("scheduleIndex", model.getScheduleIndex());
                    FmAlbumDetailActivity.this.startActivity(playIntent);
                } else {
                    int steps = FmPlayUtil.getCurAlbumTrackModel().getSteps();
                    if(FmPlayUtil.getCurAlbumTrackModel().getCurTrackList().size() <= steps){
                        steps = steps % 50;
                    }
                    PlayableModel model = FmPlayUtil.getCurAlbumTrackModel().getCurTrackList().get(steps);
                    if (model instanceof Track) {
                        Track info = (Track) model;
                        Intent playIntent = new Intent(FmAlbumDetailActivity.this, FmPlayActivity.class);
                        playIntent.putExtra("trackTitle", info.getTrackTitle());
                        playIntent.putExtra("trackId", info.getDataId());
                        playIntent.putExtra("playCount", String.valueOf(info.getPlayCount()));
                        playIntent.putExtra("trackCount", FmPlayUtil.getCurAlbumTrackModel().getTrackCount());
                        playIntent.putExtra("updatedAt", FmPlayUtil.getCurAlbumTrackModel().getUpdatedAt());
                        playIntent.putExtra("cover", info.getCoverUrlLarge());
                        playIntent.putExtra("albumTitle", FmPlayUtil.getCurAlbumTrackModel().getAlbumTitle());
                        playIntent.putParcelableArrayListExtra("trackList", (ArrayList<? extends Parcelable>) FmPlayUtil.getCurAlbumTrackModel().getCurTrackList());
                        playIntent.putExtra("albumIntro", albumIntro);
                        playIntent.putExtra("steps", FmPlayUtil.getCurAlbumTrackModel().getSteps());
                        playIntent.putExtra("albumId", FmPlayUtil.getCurAlbumTrackModel().getAlbumId());
                        playIntent.putExtra("sort", "asc");
                        playIntent.putExtra("selectSteps", FmPlayUtil.getCurAlbumTrackModel().getSteps());
                        playIntent.putExtra("selectStepsPage", FmPlayUtil.getCurAlbumTrackModel().getSelectStepsPage());

                        FmAlbumDetailActivity.this.startActivity(playIntent);
                    }
                }
            }
        });

        fmAlbumDetailPlayControlStepsRv = (RecyclerView) findViewById(R.id.fm_album_detail_play_control_steps_rv);
        GridLayoutManager managerLayout = new GridLayoutManager(this, 4);
        fmAlbumDetailPlayControlStepsRv.setLayoutManager(managerLayout);
        fmAlbumDetailSumTv = (TextView) findViewById(R.id.fm_album_detail_sum_tv);
        fmAlbumDetailSumSv = (ScrollView) findViewById(R.id.fm_album_detail_sum_sv);
        fmAlbumDetailPlayTabSum = (Button) findViewById(R.id.fm_album_detail_play_tab_sum);
        fmAlbumDetailPlayTabSum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tab == 1) {
                    tab = 0;
                    fmAlbumDetailPlayTabSum.setTextColor(getResources().getColor(R.color.material_blue));
                    fmAlbumDetailPlayTabTrack.setTextColor(getResources().getColor(R.color.color_share_gray));
                    fmAlbumDetailSumSv.setVisibility(View.VISIBLE);
                    fmAlbumDetailRefresh.setVisibility(View.GONE);
                }
            }
        });
        fmAlbumDetailPlayTabTrack = (Button) findViewById(R.id.fm_album_detail_play_tab_track);
        fmAlbumDetailPlayTabTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tab == 0) {
                    tab = 1;
                    fmAlbumDetailPlayTabSum.setTextColor(getResources().getColor(R.color.color_share_gray));
                    fmAlbumDetailPlayTabTrack.setTextColor(getResources().getColor(R.color.material_blue));
                    fmAlbumDetailSumSv.setVisibility(View.GONE);
                    fmAlbumDetailRefresh.setVisibility(View.VISIBLE);
                }
            }
        });
        fmAlbumDetailTitle = (CustomerTitle) findViewById(R.id.fm_album_detail_title);
        fmAlbumDetailCover = (ImageView) findViewById(R.id.fm_album_detail_cover);
        fmAlbumDetailTitleTv = (TextView) findViewById(R.id.fm_album_detail_title_tv);
        fmAlbumDetailWriterImg = (CircleImageView) findViewById(R.id.fm_album_detail_writer_img);
        fmAlbumDetailWriter = (TextView) findViewById(R.id.fm_album_detail_writer);
        fmAlbumDetailTime = (TextView) findViewById(R.id.fm_album_detail_time);
        fmAlbumDetailPlayCount = (TextView) findViewById(R.id.fm_album_detail_play_count);
        fmAlbumDetailPlaySteps = (TextView) findViewById(R.id.fm_album_detail_play_steps);
        fmAlbumDetailHead = (RelativeLayout) findViewById(R.id.fm_album_detail_head);
        fmAlbumDetailPlayTab = (LinearLayout) findViewById(R.id.fm_album_detail_play_tab);
        fmAlbumDetailPlayControlPlay = (ImageView) findViewById(R.id.fm_album_detail_play_control_play);
        fmAlbumDetailPlayControlPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page = 1;
                fmAlbumDetailRefresh.setEnableLoadmore(true);
                fmAlbumDetailRefresh.setEnableRefresh(true);
                selectSteps = false;

                allPlay = true;
                getTracks();
            }
        });
        fmAlbumDetailPlayControlPlayTv = (TextView) findViewById(R.id.fm_album_detail_play_control_play_tv);
        fmAlbumDetailPlayControlPlayTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page = 1;
                fmAlbumDetailRefresh.setEnableLoadmore(true);
                fmAlbumDetailRefresh.setEnableRefresh(true);
                selectSteps = false;

                allPlay = true;
                getTracks();
            }
        });
        fmAlbumDetailPlayControlStepsImg = (ImageButton) findViewById(R.id.fm_album_detail_play_control_steps_img);
        fmAlbumDetailPlayControlStepsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fmAlbumDetailPlayControlStepsRv.getVisibility() == View.VISIBLE) {
                    fmAlbumDetailPlayControlStepsRv.setVisibility(View.GONE);
                    fmAlbumDetailPlayControlStepsImg.setImageResource(R.mipmap.fm_channel_up);
                } else if (fmAlbumDetailPlayControlStepsRv.getVisibility() == View.GONE) {
                    fmAlbumDetailPlayControlStepsRv.setVisibility(View.VISIBLE);
                    fmAlbumDetailPlayControlStepsImg.setImageResource(R.mipmap.fm_channel_down);
                }
            }
        });
        fmAlbumDetailPlayControlStepsTv = (Button) findViewById(R.id.fm_album_detail_play_control_steps_tv);
        fmAlbumDetailPlayControlStepsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fmAlbumDetailPlayControlStepsRv.getVisibility() == View.VISIBLE) {
                    fmAlbumDetailPlayControlStepsRv.setVisibility(View.GONE);
                    fmAlbumDetailPlayControlStepsImg.setImageResource(R.mipmap.fm_channel_up);
                } else if (fmAlbumDetailPlayControlStepsRv.getVisibility() == View.GONE) {
                    fmAlbumDetailPlayControlStepsRv.setVisibility(View.VISIBLE);
                    fmAlbumDetailPlayControlStepsImg.setImageResource(R.mipmap.fm_channel_down);
                }
            }
        });
        fmAlbumDetailPlayControlSortImg = (ImageButton) findViewById(R.id.fm_album_detail_play_control_sort_img);
        fmAlbumDetailPlayControlSortImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fmAlbumDetailPlayControlStepsRv.setVisibility(View.GONE);
                page = 1;
                if (sort.equals("desc")) {
                    sort = "asc";
                    fmAlbumDetailPlayControlSortTv.setText("正序");
                    fmAlbumDetailPlayControlSortImg.setImageResource(R.mipmap.fm_sort_down);
                } else {
                    sort = "desc";
                    fmAlbumDetailPlayControlSortTv.setText("倒序");
                    fmAlbumDetailPlayControlSortImg.setImageResource(R.mipmap.fm_sort_up);
                }
                if(selectSteps){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put(DTransferConstants.ALBUM_ID, getIntent().getStringExtra("albumId"));
                    map.put(DTransferConstants.SORT, sort);
                    map.put(DTransferConstants.PAGE, String.valueOf(selectStepsPage));
                    map.put(DTransferConstants.PAGE_SIZE, String.valueOf(50));
                    CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {

                        @Override
                        public void onSuccess(@Nullable TrackList trackList) {
                            mFmAlbumContentAdapter.setData(trackList.getTracks(),(selectStepsPage - 1) * 50,sort,trackCount,true);
                            mFmAlbumContentAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });
                }else{
                    getTracks();
                }

            }
        });
        fmAlbumDetailPlayControlSortTv = (Button) findViewById(R.id.fm_album_detail_play_control_sort_tv);
        fmAlbumDetailPlayControlSortTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fmAlbumDetailPlayControlStepsRv.setVisibility(View.GONE);
                page = 1;
                if (sort.equals("desc")) {
                    sort = "asc";
                    fmAlbumDetailPlayControlSortTv.setText("正序");
                    fmAlbumDetailPlayControlSortImg.setImageResource(R.mipmap.fm_sort_down);
                } else {
                    sort = "desc";
                    fmAlbumDetailPlayControlSortTv.setText("倒序");
                    fmAlbumDetailPlayControlSortImg.setImageResource(R.mipmap.fm_sort_up);
                }
                if(selectSteps){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put(DTransferConstants.ALBUM_ID, getIntent().getStringExtra("albumId"));
                    map.put(DTransferConstants.SORT, sort);
                    map.put(DTransferConstants.PAGE, String.valueOf(selectStepsPage));
                    map.put(DTransferConstants.PAGE_SIZE, String.valueOf(50));
                    CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {

                        @Override
                        public void onSuccess(@Nullable TrackList trackList) {
                            mFmAlbumContentAdapter.setData(trackList.getTracks(),(selectStepsPage - 1) * 50,sort,trackCount,true);
                            mFmAlbumContentAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });
                }else{
                    getTracks();
                }
            }
        });
        fmAlbumDetailPlayControl = (RelativeLayout) findViewById(R.id.fm_album_detail_play_control);
        fmAlbumDetailList = (ListView) findViewById(R.id.fm_album_detail_list);
        fmAlbumDetailRefresh = (SmartRefreshLayout) findViewById(R.id.fm_album_detail_refresh);
        voiceBar = (VoiceBar) findViewById(R.id.voice_bar);

        mFmAlbumContentAdapter = new FmAlbumContentAdapter(this);
        fmAlbumDetailList.setAdapter(mFmAlbumContentAdapter);
        fmAlbumDetailList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                addDisposable(RobotServiceImpl.fmHistoricalAdd(new Gson().toJson(album),album.getId(),0, LoginUser.getUserId())
                        .subscribeWith(new NetSubscriber<BaseModel>() {
                            @Override
                            protected void onSuccess(BaseModel baseModel) {
                                try {
                                    if (baseModel.data != null) {

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            protected void onFailure(int errorCode, String errorMessage) {

                            }
                        }));

                Track data = mFmAlbumContentAdapter.getData().get(position);
                Intent playIntent = new Intent(FmAlbumDetailActivity.this, FmPlayActivity.class);
                playIntent.putExtra("trackTitle", data.getTrackTitle());
                playIntent.putExtra("trackId", data.getDataId());
                playIntent.putExtra("playCount", String.valueOf(data.getPlayCount()));
                playIntent.putExtra("trackCount", String.valueOf(trackCount));
                playIntent.putExtra("updatedAt", updatedAt);
                playIntent.putExtra("cover", data.getCoverUrlLarge());
                playIntent.putExtra("albumTitle", albumTitle);
                playIntent.putParcelableArrayListExtra("trackList", (ArrayList<? extends Parcelable>) mFmAlbumContentAdapter.getData());
                playIntent.putExtra("albumIntro", albumIntro);
                playIntent.putExtra("steps", position);
                playIntent.putExtra("albumId", albumId);
                playIntent.putExtra("sort", sort);
                playIntent.putExtra("selectSteps", selectSteps);
                if (selectSteps) {
                    playIntent.putExtra("selectStepsPage", selectStepsPage);
                } else {
                    playIntent.putExtra("selectStepsPage", page);
                }


                FmAlbumDetailActivity.this.startActivity(playIntent);
            }
        });

        fmAlbumDetailRefresh.setEnableLoadmore(true);
        selectSteps = false;
        fmAlbumDetailRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                getTracks();
            }
        });
        fmAlbumDetailRefresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                page++;
                getTracks();
            }
        });
        fmAlbumDetailRefresh.autoRefresh();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mPlayerManager = FmPlayUtil.getPlayerManager();
        mPlayerManager.addPlayerStatusListener(mPlayerStatusListener);
        mPlayerManager.addAdsStatusListener(mAdsListener);

        FloatViewInstance.attach(findViewById(android.R.id.content), this, new FloatView.OnMessageResult() {
            @Override
            public void onMessageResult(String s) {
                searchContent = s;
                requestVoiceBarNet();
            }
        });

        if ((FmPlayUtil.getCurAlbumTrackModel() == null || FmPlayUtil.getCurAlbumTrackModel().getCurTrackList() == null) && FmPlayUtil.getCurRadioScheduleModel() == null) {
            fmAlbumDetailPlayControlBottomPlay.setVisibility(View.GONE);
        } else if (FmPlayUtil.getCurAlbumTrackModel() != null) {
            fmAlbumDetailPlayControlBottomPlay.setVisibility(View.VISIBLE);
            int steps = FmPlayUtil.getCurAlbumTrackModel().getSteps();
            if(FmPlayUtil.getCurAlbumTrackModel().getCurTrackList().size() <= steps){
                steps = steps % 50;
            }
            PlayableModel model = FmPlayUtil.getCurAlbumTrackModel().getCurTrackList().get(steps);
            if (model instanceof Track) {
                Track info = (Track) model;
                Glide.with(FmAlbumDetailActivity.this).load(info.getCoverUrlSmall()).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        fmAlbumDetailPlayControlBottomPlayCover.setImageBitmap(resource);
                    }
                });
                if (FmPlayUtil.getPlayerManager().isPlaying()) {
                    fmAlbumDetailPlayControlBottomPlayCover.setCycle(true);
                } else {
                    fmAlbumDetailPlayControlBottomPlayCover.setCycle(false);
                }
            }
            if (albumId != null && albumId.equals(FmPlayUtil.getCurAlbumTrackModel().getAlbumId())) {
                mFmAlbumContentAdapter.setSelectId(FmPlayUtil.getCurAlbumTrackModel().getTrackId());
            }
        } else {
            fmAlbumDetailPlayControlBottomPlay.setVisibility(View.VISIBLE);
            PlayableModel model = FmPlayUtil.getCurRadioScheduleModel().getRadio();
            if (model instanceof Radio) {
                Radio info = (Radio) model;
                Glide.with(FmAlbumDetailActivity.this).load(info.getCoverUrlSmall()).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        fmAlbumDetailPlayControlBottomPlayCover.setImageBitmap(resource);
                    }
                });
                if (FmPlayUtil.getPlayerManager().isPlaying()) {
                    fmAlbumDetailPlayControlBottomPlayCover.setCycle(true);
                } else {
                    fmAlbumDetailPlayControlBottomPlayCover.setCycle(false);
                }
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayerManager.removePlayerStatusListener(mPlayerStatusListener);
        mPlayerManager.removeAdsStatusListener(mAdsListener);
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
        DialogUtils.showProgressDialog(this);
//        listDaily.scrollToPosition(0);
        if (TextUtils.isEmpty(searchContent)) {
            searchContent = "";
        }
        addDisposable(RobotServiceImpl.fmApp(searchContent,false)
                .subscribeWith(new NetSubscriber<BaseModel>() {

                    @Override
                    protected void onSuccess(BaseModel baseModel) {
                        if("history".equals(baseModel.data.contentType)){
                            startActivity(new Intent(FmAlbumDetailActivity.this, FmHistoryActivity.class));
                        }else{
                            if (baseModel.data.contentType != null && baseModel.data.contentType.length() > 0 && baseModel.data.keyword != null && baseModel.data.keyword.length() > 0) {
                                Intent searchIntent = new Intent(FmAlbumDetailActivity.this, FmAlbumSearchActivity.class);
                                searchIntent.putExtra("searchkey", baseModel.data.keyword);
                                searchIntent.putExtra("searchType", baseModel.data.contentType);
                                startActivity(searchIntent);
                            }
                        }
                        DialogUtils.closeProgressDialog();
                    }

                    @Override
                    protected void onFailure(int errorCode, String errorMessage) {
                        DialogUtils.closeProgressDialog();
                    }
                }));
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fm_album_detail_layout;
    }


    private void getTracks() {
        DialogUtils.showProgressDialog(this);
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_ID, albumId);
        map.put(DTransferConstants.SORT, sort);
        map.put(DTransferConstants.PAGE, String.valueOf(page));
        CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(@Nullable TrackList trackList) {
                DialogUtils.closeProgressDialog();

                if (page == 1) {
                    trackCount = Integer.parseInt(getIntent().getStringExtra("trackCount").replace("集", ""));
                    mFmStepslAdapter = new FmStepslAdapter(FmAlbumDetailActivity.this, trackCount);
                    fmAlbumDetailPlayControlStepsRv.setAdapter(mFmStepslAdapter);
                    mFmStepslAdapter.notifyDataSetChanged();
                    mFmStepslAdapter.setOnItemClickListener(new FmStepslAdapter.OnItemClickListener() {
                        @Override
                        public void onClick(final int position) {

                            if (tab == 0) {
                                tab = 1;
                                fmAlbumDetailPlayTabSum.setTextColor(getResources().getColor(R.color.color_share_gray));
                                fmAlbumDetailPlayTabTrack.setTextColor(getResources().getColor(R.color.material_blue));
                                fmAlbumDetailSumSv.setVisibility(View.GONE);
                                fmAlbumDetailRefresh.setVisibility(View.VISIBLE);
                            }

//                            sort = "asc";
//                            fmAlbumDetailPlayControlSortTv.setText("正序");
//                            fmAlbumDetailPlayControlSortImg.setImageResource(R.mipmap.fm_sort_down);

                            selectSteps = true;
                            selectStepsPage = position + 1;
                            fmAlbumDetailRefresh.setEnableLoadmore(false);
                            fmAlbumDetailRefresh.setEnableRefresh(false);
                            Map<String, String> map = new HashMap<String, String>();
                            map.put(DTransferConstants.ALBUM_ID, getIntent().getStringExtra("albumId"));
                            map.put(DTransferConstants.SORT, sort);
                            map.put(DTransferConstants.PAGE, String.valueOf(position + 1));
                            map.put(DTransferConstants.PAGE_SIZE, String.valueOf(50));
                            CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {

                                @Override
                                public void onSuccess(@Nullable TrackList trackList) {
                                    mFmAlbumContentAdapter.setData(trackList.getTracks(),position * 50,sort,trackCount,true);
                                    mFmAlbumContentAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onError(int i, String s) {

                                }
                            });
                            fmAlbumDetailPlayControlStepsRv.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLongClick(int position) {

                        }
                    });

                    Glide.with(mContext).load(trackList.getCoverUrlMiddle()).into(fmAlbumDetailCover);
                    albumTitle = trackList.getAlbumTitle();
                    fmAlbumDetailTitleTv.setText(albumTitle);
                    if (getIntent().getStringExtra("avatar") != null) {
                        Glide.with(mContext).load(getIntent().getStringExtra("avatar")).into(fmAlbumDetailWriterImg);
                    }
                    albumIntro = trackList.getAlbumIntro();
                    if (albumIntro == null || albumIntro.length() == 0) {
                        fmAlbumDetailSumTv.setText("暂无简介");
                    } else {
                        fmAlbumDetailSumTv.setText(albumIntro);
                    }
                    fmAlbumDetailWriter.setText(getIntent().getStringExtra("nickname"));
                    updatedAt = getIntent().getStringExtra("updatedAt");
                    fmAlbumDetailTime.setText(updatedAt);
                    fmAlbumDetailPlayCount.setText(getIntent().getStringExtra("playConnt"));
                    fmAlbumDetailPlaySteps.setText(getIntent().getStringExtra("trackCount"));
                    fmAlbumDetailPlayTabTrack.setText("节目" + "(" + getIntent().getStringExtra("trackCount") + ")");


                    mFmAlbumContentAdapter.setData(trackList.getTracks(),sort,trackCount,false);
                } else {
                    mFmAlbumContentAdapter.addData(trackList.getTracks());
                }
                mFmAlbumContentAdapter.notifyDataSetChanged();
                fmAlbumDetailRefresh.finishRefresh();
                fmAlbumDetailRefresh.finishLoadmore();

                if(allPlay){
                    allPlay = false;
                    addDisposable(RobotServiceImpl.fmHistoricalAdd(new Gson().toJson(album), album.getId(), 0, LoginUser.getUserId())
                            .subscribeWith(new NetSubscriber<BaseModel>() {
                                @Override
                                protected void onSuccess(BaseModel baseModel) {
                                    try {
                                        if (baseModel.data != null) {

                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                protected void onFailure(int errorCode, String errorMessage) {

                                }
                            }));

                    Track data = mFmAlbumContentAdapter.getData().get(0);
                    Intent playIntent = new Intent(FmAlbumDetailActivity.this, FmPlayActivity.class);
                    playIntent.putExtra("trackTitle", data.getTrackTitle());
                    playIntent.putExtra("trackId", data.getDataId());
                    playIntent.putExtra("playCount", String.valueOf(data.getPlayCount()));
                    playIntent.putExtra("trackCount", String.valueOf(trackCount));
                    playIntent.putExtra("updatedAt", updatedAt);
                    playIntent.putExtra("cover", data.getCoverUrlLarge());
                    playIntent.putExtra("albumTitle", albumTitle);
                    playIntent.putParcelableArrayListExtra("trackList", (ArrayList<? extends Parcelable>) mFmAlbumContentAdapter.getData());
                    playIntent.putExtra("albumIntro", albumIntro);
                    playIntent.putExtra("steps", 0);
                    playIntent.putExtra("albumId", albumId);
                    playIntent.putExtra("sort", sort);
                    playIntent.putExtra("selectSteps", selectSteps);
                    if (selectSteps) {
                        playIntent.putExtra("selectStepsPage", selectStepsPage);
                    } else {
                        playIntent.putExtra("selectStepsPage", page);
                    }


                    FmAlbumDetailActivity.this.startActivity(playIntent);
                }
            }

            @Override
            public void onError(int i, String s) {
                DialogUtils.closeProgressDialog();
                fmAlbumDetailRefresh.finishRefresh();
                fmAlbumDetailRefresh.finishLoadmore();
            }
        });
    }


    private IXmPlayerStatusListener mPlayerStatusListener = new IXmPlayerStatusListener() {

        @Override
        public void onSoundPrepared() {
        }

        @Override
        public void onSoundSwitch(PlayableModel laModel, PlayableModel curModel) {
            PlayableModel model = mPlayerManager.getCurrSound();
            if (model != null && model instanceof Track) {
                String title = null;
                String coverUrl = null;
                Track info = (Track) model;
                title = info.getTrackTitle();
                coverUrl = info.getCoverUrlLarge();

                if (coverUrl != null) {
                    Glide.with(getApplicationContext()).load(coverUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            fmAlbumDetailPlayControlBottomPlayCover.setImageBitmap(resource);
                        }
                    });
                }

                AlbumTrackModel albumTrackModel = FmPlayUtil.getCurAlbumTrackModel();
                if (albumTrackModel != null) {
                    albumTrackModel.setSteps(info.getOrderNum());
                    albumTrackModel.setTrackId(info.getDataId());
                    albumTrackModel.setCover(coverUrl);
                    albumTrackModel.setTrackTitle(title);
                    FmPlayUtil.setCurAlbumTrackModel(albumTrackModel);

//                    if(FmPlayUtil.getCurAlbumTrackModel() == null || FmPlayUtil.getCurAlbumTrackModel().getCurTrackList() == null){
//                        fmAlbumDetailPlayControlBottomPlay.setVisibility(View.GONE);
//                    }else{
//                        fmAlbumDetailPlayControlBottomPlay.setVisibility(View.VISIBLE);
//                        PlayableModel model = FmPlayUtil.getCurAlbumTrackModel().getCurTrackList().get(FmPlayUtil.getCurAlbumTrackModel().getSteps());
//                        if (model instanceof Track) {
//                            Track info = (Track) model;
//                            Glide.with(FmAlbumDetailActivity.this).load(info.getCoverUrlSmall()).asBitmap().into(new SimpleTarget<Bitmap>() {
//                                @Override
//                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                                    fmAlbumDetailPlayControlBottomPlayCover.setImageBitmap(resource);
//                                }
//                            });
//                            if(FmPlayUtil.getPlayerManager().isPlaying()){
//                                fmAlbumDetailPlayControlBottomPlayCover.setCycle(true);
//                            }else{
//                                fmAlbumDetailPlayControlBottomPlayCover.setCycle(false);
//                            }
//                        }
//
                    if (albumId != null && albumId.equals(FmPlayUtil.getCurAlbumTrackModel().getAlbumId())) {
                        mFmAlbumContentAdapter.setSelectId(FmPlayUtil.getCurAlbumTrackModel().getTrackId());
                    }
//                    }
//                }
                }


            }
            updateButtonStatus();
        }


        private void updateButtonStatus() {
        }

        @Override
        public void onPlayStop() {
        }

        @Override
        public void onPlayStart() {
        }

        @Override
        public void onPlayProgress(int currPos, int duration) {
        }

        @Override
        public void onPlayPause() {
        }

        @Override
        public void onSoundPlayComplete() {
            XmPlayerManager.getInstance(mContext).pause();
        }

        @Override
        public boolean onError(XmPlayerException exception) {
            return false;
        }

        @Override
        public void onBufferProgress(int position) {
        }

        public void onBufferingStart() {
        }

        public void onBufferingStop() {
        }

    };

    private IXmAdsStatusListener mAdsListener = new IXmAdsStatusListener() {

        @Override
        public void onStartPlayAds(Advertis ad, int position) {
        }

        @Override
        public void onStartGetAdsInfo() {
        }

        @Override
        public void onGetAdsInfo(final AdvertisList ads) {
        }

        @Override
        public void onError(int what, int extra) {
        }

        @Override
        public void onCompletePlayAds() {
        }

        @Override
        public void onAdsStopBuffering() {
        }

        @Override
        public void onAdsStartBuffering() {
        }
    };
}
