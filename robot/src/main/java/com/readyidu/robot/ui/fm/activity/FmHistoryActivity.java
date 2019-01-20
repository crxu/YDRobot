package com.readyidu.robot.ui.fm.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.readyidu.baiduvoice.laster.FloatView;
import com.readyidu.baiduvoice.laster.FloatViewInstance;
import com.readyidu.basic.models.LoginUser;
import com.readyidu.basic.utils.DialogUtils;
import com.readyidu.basic.widgets.CustomerLayoutManager;
import com.readyidu.basic.widgets.title.CustomerTitle;
import com.readyidu.robot.R;
import com.readyidu.robot.api.NetSubscriber;
import com.readyidu.robot.api.impl.RobotServiceImpl;
import com.readyidu.robot.base.BaseVoiceBarActivity;
import com.readyidu.robot.model.BaseModel;
import com.readyidu.robot.ui.fm.adapetr.FmHistoryAlbumRecyclerAdapter;
import com.readyidu.robot.ui.fm.util.AlbumHistoryBase;
import com.readyidu.robot.ui.fm.util.AlbumHistoryModel;
import com.readyidu.robot.ui.fm.util.AlbumTrackModel;
import com.readyidu.robot.ui.fm.util.FmPlayUtil;
import com.readyidu.robot.ui.fm.util.RadioScheduleModel;
import com.readyidu.robot.ui.fm.view.CycleImageView;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.observers.DisposableObserver;

/**
 * Created by yuzhang on 2018/6/3.
 */

public class FmHistoryActivity extends BaseVoiceBarActivity {

    CustomerTitle fmHistoryTitle;
//    ListView fmHistoryList;
    RecyclerView fmHistoryRecycler;
//    SmartRefreshLayout fmHistoryListRefresh;
    CycleImageView fmHistoryBottomPlayCover;
    RelativeLayout fmHistoryBottomPlay;
    ImageButton fmHistoryListHeadSelecyAll;
    TextView fmHistoryListHeadSelecyCancel;
    Button fmHistoryBottomDelete;
    RelativeLayout fmHistoryBottomDeleteRl;

    private View fmHistoryListHead;
//    private FmHistoryAlbumAdapter mFmAlbumAdapter;
    private FmHistoryAlbumRecyclerAdapter mFmHistoryAlbumRecyclerAdapter;
    private HeaderAndFooterWrapper mFmHistoryAlbumHeaderAndFooterWrapper;
    private int page;

    private XmPlayerManager mPlayerManager;

    private boolean isEdit;
    private boolean allSelect;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void bindViews() {
        super.bindViews();

        page = 1;

        fmHistoryBottomDelete = (Button) findViewById(R.id.fm_history_bottom_delete);
        fmHistoryBottomDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fmHistoricalDelete();
            }
        });
        fmHistoryBottomDeleteRl = (RelativeLayout) findViewById(R.id.fm_history_bottom_delete_rl);
        fmHistoryBottomDeleteRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fmHistoricalDelete();
            }
        });

        fmHistoryBottomPlayCover = (CycleImageView) findViewById(R.id.fm_history_bottom_play_cover);
        fmHistoryBottomPlay = (RelativeLayout) findViewById(R.id.fm_history_bottom_play);
        fmHistoryBottomPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FmPlayUtil.getCurAlbumTrackModel() == null) {
                    RadioScheduleModel model = FmPlayUtil.getCurRadioScheduleModel();
                    Intent playIntent = new Intent(FmHistoryActivity.this, FmRadioPlayActivity.class);
//                    playIntent.putParcelableArrayListExtra("schedules", (ArrayList<? extends Parcelable>) model.getSchedules());
                    playIntent.putExtra("radio", model.getRadio());
                    playIntent.putExtra("scheduleIndex", model.getScheduleIndex());
                    FmHistoryActivity.this.startActivity(playIntent);
                } else {
                    int steps = FmPlayUtil.getCurAlbumTrackModel().getSteps();
                    if (FmPlayUtil.getCurAlbumTrackModel().getCurTrackList().size() <= steps) {
                        steps = steps % 50;
                    }
                    PlayableModel model = FmPlayUtil.getCurAlbumTrackModel().getCurTrackList().get(steps);
                    if (model instanceof Track) {
                        Track info = (Track) model;
                        Intent playIntent = new Intent(FmHistoryActivity.this, FmPlayActivity.class);
                        playIntent.putExtra("trackTitle", info.getTrackTitle());
                        playIntent.putExtra("trackId", info.getDataId());
                        playIntent.putExtra("playCount", String.valueOf(info.getPlayCount()));
                        playIntent.putExtra("trackCount", FmPlayUtil.getCurAlbumTrackModel().getTrackCount());
                        playIntent.putExtra("updatedAt", FmPlayUtil.getCurAlbumTrackModel().getUpdatedAt());
                        playIntent.putExtra("cover", info.getCoverUrlLarge());
                        playIntent.putExtra("albumTitle", FmPlayUtil.getCurAlbumTrackModel().getAlbumTitle());
                        playIntent.putParcelableArrayListExtra("trackList", (ArrayList<? extends Parcelable>) FmPlayUtil.getCurAlbumTrackModel().getCurTrackList());
                        playIntent.putExtra("albumIntro", FmPlayUtil.getCurAlbumTrackModel().getAlbumIntro());
                        playIntent.putExtra("steps", FmPlayUtil.getCurAlbumTrackModel().getSteps());
                        playIntent.putExtra("albumId", FmPlayUtil.getCurAlbumTrackModel().getAlbumId());
                        playIntent.putExtra("sort", "asc");
                        playIntent.putExtra("selectSteps", FmPlayUtil.getCurAlbumTrackModel().getSteps());
                        playIntent.putExtra("selectStepsPage", FmPlayUtil.getCurAlbumTrackModel().getSelectStepsPage());

                        FmHistoryActivity.this.startActivity(playIntent);
                    }
                }
            }
        });

//        fmHistoryListRefresh = (SmartRefreshLayout) findViewById(R.id.fm_history_list_refresh);
//        fmHistoryListRefresh.setEnableLoadmore(true);
//        fmHistoryListRefresh.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh(RefreshLayout refreshlayout) {
//                page = 1;
//                fmHistoricalList();
//            }
//        });
//        fmHistoryListRefresh.setOnLoadmoreListener(new OnLoadmoreListener() {
//            @Override
//            public void onLoadmore(RefreshLayout refreshlayout) {
//                page++;
//                fmHistoricalList();
//            }
//        });
        page = 1;
        fmHistoryTitle = (CustomerTitle) findViewById(R.id.fm_history_title);
        fmHistoryTitle.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEdit) {
                    mFmHistoryAlbumRecyclerAdapter.setEdit(true);
                    fmHistoryListHead.setVisibility(View.VISIBLE);
//                    fmHistoryList.addHeaderView(fmHistoryListHead);
                    isEdit = true;
                    fmHistoryBottomDeleteRl.setVisibility(View.VISIBLE);
                    fmHistoryBottomPlay.setVisibility(View.GONE);
                    fmHistoryTitle.setRightText("");
//                    fmHistoryListRefresh.setEnableLoadmore(false);
//                    fmHistoryListRefresh.setEnableRefresh(false);
                }
            }
        });

//        fmHistoryList = (ListView) findViewById(R.id.fm_history_list);
        fmHistoryRecycler = (RecyclerView)findViewById(R.id.fm_history_recycler) ;
//        fmHistoryRecycler.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (isEdit) {
//                    mFmHistoryAlbumRecyclerAdapter.select(position);
//                } else {
//                    Intent detailIntent = new Intent(FmHistoryActivity.this, FmAlbumDetailActivity.class);
//                    detailIntent.putExtra("album", mFmHistoryAlbumRecyclerAdapter.getData().get(position));
//                    detailIntent.putExtra("albumId", String.valueOf(mFmHistoryAlbumRecyclerAdapter.getData().get(position).getId()));
//                    detailIntent.putExtra("nickname", mFmHistoryAlbumRecyclerAdapter.getData().get(position).getAnnouncer().getNickname());
//                    detailIntent.putExtra("avatar", mFmHistoryAlbumRecyclerAdapter.getData().get(position).getAnnouncer().getAvatarUrl());
//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd更新");
//                    detailIntent.putExtra("updatedAt", sdf.format(mFmHistoryAlbumRecyclerAdapter.getData().get(position).getUpdatedAt()));
//                    detailIntent.putExtra("playConnt", mFmHistoryAlbumRecyclerAdapter.getData().get(position).getPlayCount() + "次");
//                    detailIntent.putExtra("trackCount", mFmHistoryAlbumRecyclerAdapter.getData().get(position).getIncludeTrackCount() + "集");
//                    startActivity(detailIntent);
//                }
//            }
//        });


        CustomerLayoutManager layoutManager = new CustomerLayoutManager(FmHistoryActivity.this, LinearLayoutManager.VERTICAL, false);
        fmHistoryRecycler.setLayoutManager(layoutManager);
        mFmHistoryAlbumRecyclerAdapter = new FmHistoryAlbumRecyclerAdapter(FmHistoryActivity.this);
        mFmHistoryAlbumHeaderAndFooterWrapper = new HeaderAndFooterWrapper(mFmHistoryAlbumRecyclerAdapter);

//        mFmAlbumAdapter = new FmHistoryAlbumAdapter(this);
//        fmHistoryList.setAdapter(mFmAlbumAdapter);

        fmHistoryRecycler.setAdapter(mFmHistoryAlbumHeaderAndFooterWrapper);

        fmHistoryListHead = LayoutInflater.from(this).inflate(R.layout.fm_history_list_head_layout, null);
        fmHistoryListHeadSelecyAll = (ImageButton) fmHistoryListHead.findViewById(R.id.fm_history_list_head_selecy_all);
        fmHistoryListHeadSelecyAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allSelect) {
                    mFmHistoryAlbumRecyclerAdapter.allCancelSelect();
                    fmHistoryListHeadSelecyAll.setImageResource(R.mipmap.fm_weixuan);
                } else {
                    mFmHistoryAlbumRecyclerAdapter.allSelect();
                    fmHistoryListHeadSelecyAll.setImageResource(R.mipmap.fm_xuanze);
                }
                allSelect = !allSelect;
            }
        });
        fmHistoryListHeadSelecyCancel = (TextView) fmHistoryListHead.findViewById(R.id.fm_history_list_head_selecy_cancel);
        fmHistoryListHeadSelecyCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                fmHistoryList.removeHeaderView(fmHistoryListHead);
                fmHistoryListHead.setVisibility(View.GONE);
                isEdit = false;
                mFmHistoryAlbumRecyclerAdapter.setEdit(false);
                mFmHistoryAlbumRecyclerAdapter.allCancelSelect();
                mFmHistoryAlbumRecyclerAdapter.notifyDataSetChanged();
                fmHistoryBottomDeleteRl.setVisibility(View.GONE);
                if (!((FmPlayUtil.getCurAlbumTrackModel() == null || FmPlayUtil.getCurAlbumTrackModel().getCurTrackList() == null) && FmPlayUtil.getCurRadioScheduleModel() == null)) {
                    fmHistoryBottomPlay.setVisibility(View.VISIBLE);
                }
                fmHistoryTitle.setRightText("编辑");
//                fmHistoryListRefresh.setEnableLoadmore(true);
//                fmHistoryListRefresh.setEnableRefresh(true);

            }
        });
        mFmHistoryAlbumHeaderAndFooterWrapper.addHeaderView(fmHistoryListHead);
        fmHistoryListHead.setVisibility(View.GONE);

        mFmHistoryAlbumRecyclerAdapter.setOnItemClickListener(new FmHistoryAlbumRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                if (isEdit) {
                    mFmHistoryAlbumRecyclerAdapter.select(position);
                } else {
                    Intent detailIntent = new Intent(FmHistoryActivity.this, FmAlbumDetailActivity.class);
                    detailIntent.putExtra("album", mFmHistoryAlbumRecyclerAdapter.getData().get(position));
                    detailIntent.putExtra("albumId", String.valueOf(mFmHistoryAlbumRecyclerAdapter.getData().get(position).getId()));
                    detailIntent.putExtra("nickname", mFmHistoryAlbumRecyclerAdapter.getData().get(position).getAnnouncer().getNickname());
                    detailIntent.putExtra("avatar", mFmHistoryAlbumRecyclerAdapter.getData().get(position).getAnnouncer().getAvatarUrl());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd更新");
                    detailIntent.putExtra("updatedAt", sdf.format(mFmHistoryAlbumRecyclerAdapter.getData().get(position).getUpdatedAt()));
                    detailIntent.putExtra("playConnt", mFmHistoryAlbumRecyclerAdapter.getData().get(position).getPlayCount() + "次");
                    detailIntent.putExtra("trackCount", mFmHistoryAlbumRecyclerAdapter.getData().get(position).getIncludeTrackCount() + "集");
                    startActivity(detailIntent);
                }
            }

            @Override
            public void onLongClick(int position) {

            }
        });

        fmHistoricalList();
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


        mPlayerManager = FmPlayUtil.getPlayerManager();
        mPlayerManager.addPlayerStatusListener(mPlayerStatusListener);
        mPlayerManager.addAdsStatusListener(mAdsListener);

        if ((FmPlayUtil.getCurAlbumTrackModel() == null || FmPlayUtil.getCurAlbumTrackModel().getCurTrackList() == null) && FmPlayUtil.getCurRadioScheduleModel() == null) {
            fmHistoryBottomPlay.setVisibility(View.GONE);
        } else if (FmPlayUtil.getCurAlbumTrackModel() != null) {
            fmHistoryBottomPlay.setVisibility(View.VISIBLE);
            int steps = FmPlayUtil.getCurAlbumTrackModel().getSteps();
            if (FmPlayUtil.getCurAlbumTrackModel().getCurTrackList().size() <= steps) {
                steps = steps % 50;
            }
            PlayableModel model = FmPlayUtil.getCurAlbumTrackModel().getCurTrackList().get(steps);
            if (model instanceof Track) {
                Track info = (Track) model;
                Glide.with(FmHistoryActivity.this).load(info.getCoverUrlSmall()).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        fmHistoryBottomPlayCover.setImageBitmap(resource);
                    }
                });
                if (FmPlayUtil.getPlayerManager().isPlaying()) {
                    fmHistoryBottomPlayCover.setCycle(true);
                } else {
                    fmHistoryBottomPlayCover.setCycle(false);
                }
            }
        } else {
            fmHistoryBottomPlay.setVisibility(View.VISIBLE);
            PlayableModel model = FmPlayUtil.getCurRadioScheduleModel().getRadio();
            if (model instanceof Radio) {
                Radio info = (Radio) model;
                Glide.with(FmHistoryActivity.this).load(info.getCoverUrlSmall()).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        fmHistoryBottomPlayCover.setImageBitmap(resource);
                    }
                });
                if (FmPlayUtil.getPlayerManager().isPlaying()) {
                    fmHistoryBottomPlayCover.setCycle(true);
                } else {
                    fmHistoryBottomPlayCover.setCycle(false);
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
        if (TextUtils.isEmpty(searchContent)) {
            searchContent = "";
        }
        addDisposable(RobotServiceImpl.fmApp(searchContent, false)
                .subscribeWith(new NetSubscriber<BaseModel>() {

                    @Override
                    protected void onSuccess(BaseModel baseModel) {
                        DialogUtils.closeProgressDialog();
                        if (baseModel.data.contentType != null && baseModel.data.contentType.length() > 0 && baseModel.data.keyword != null && baseModel.data.keyword.length() > 0) {
                            Intent searchIntent = new Intent(FmHistoryActivity.this, FmAlbumSearchActivity.class);
                            searchIntent.putExtra("searchkey", baseModel.data.keyword);
                            searchIntent.putExtra("searchType", baseModel.data.contentType);
                            startActivity(searchIntent);
                        }
                    }

                    @Override
                    protected void onFailure(int errorCode, String errorMessage) {
                        DialogUtils.closeProgressDialog();
                    }
                }));
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fm_history_layout;
    }

    private void fmHistoricalList() {
        DialogUtils.showProgressDialog(this);
        addDisposable(RobotServiceImpl
                .fmHistoricalList(0, LoginUser.getUserId(), page, 20)
                .subscribeWith(new DisposableObserver<AlbumHistoryBase>() {
                    @Override
                    public void onNext(AlbumHistoryBase albumHistoryListBaseListModel) {
                        DialogUtils.closeProgressDialog();
                        if (albumHistoryListBaseListModel != null && albumHistoryListBaseListModel.getData() != null && albumHistoryListBaseListModel.getData().getRows() != null) {
                            List<AlbumHistoryModel> rows = albumHistoryListBaseListModel.getData().getRows();
                            if (page == 1) {
                                mFmHistoryAlbumRecyclerAdapter.setData(rows);
                            } else {
                                mFmHistoryAlbumRecyclerAdapter.addData(rows);
                            }
                            mFmHistoryAlbumRecyclerAdapter.notifyDataSetChanged();

                        }
//                        fmHistoryListRefresh.finishRefresh();
//                        fmHistoryListRefresh.finishLoadmore();
                    }

                    @Override
                    public void onError(Throwable e) {
//                        fmHistoryListRefresh.finishRefresh();
//                        fmHistoryListRefresh.finishLoadmore();
                        DialogUtils.closeProgressDialog();
                    }

                    @Override
                    public void onComplete() {
                        DialogUtils.closeProgressDialog();
                    }
                }));
    }

    private void fmHistoricalDelete() {
        DialogUtils.showProgressDialog(this);
        addDisposable(RobotServiceImpl.fmHistoricalDelete(mFmHistoryAlbumRecyclerAdapter.getSelect())
                .subscribeWith(new NetSubscriber<BaseModel>() {
                    @Override
                    protected void onSuccess(BaseModel baseModel) {
                        DialogUtils.closeProgressDialog();

//                        fmHistoryList.removeHeaderView(fmHistoryListHead);
                        fmHistoryListHead.setVisibility(View.GONE);
                        isEdit = false;
                        fmHistoryBottomDeleteRl.setVisibility(View.GONE);
                        if (!((FmPlayUtil.getCurAlbumTrackModel() == null || FmPlayUtil.getCurAlbumTrackModel().getCurTrackList() == null) && FmPlayUtil.getCurRadioScheduleModel() == null)) {
                            fmHistoryBottomPlay.setVisibility(View.VISIBLE);
                        }
                        fmHistoryTitle.setRightText("编辑");
//                        fmHistoryListRefresh.setEnableLoadmore(true);
//                        fmHistoryListRefresh.setEnableRefresh(true);

                        mFmHistoryAlbumRecyclerAdapter.setEdit(false);

                        fmHistoricalList();
                    }

                    @Override
                    protected void onFailure(int errorCode, String errorMessage) {
                        DialogUtils.closeProgressDialog();

//                        fmHistoryList.removeHeaderView(fmHistoryListHead);
                        fmHistoryListHead.setVisibility(View.GONE);
                        isEdit = false;
                        fmHistoryBottomDeleteRl.setVisibility(View.GONE);
                        if (!((FmPlayUtil.getCurAlbumTrackModel() == null || FmPlayUtil.getCurAlbumTrackModel().getCurTrackList() == null) && FmPlayUtil.getCurRadioScheduleModel() == null)) {
                            fmHistoryBottomPlay.setVisibility(View.VISIBLE);
                        }
                        fmHistoryTitle.setRightText("编辑");
//                        fmHistoryListRefresh.setEnableLoadmore(true);
//                        fmHistoryListRefresh.setEnableRefresh(true);

                        mFmHistoryAlbumRecyclerAdapter.setEdit(false);

                        fmHistoricalList();

                        mFmHistoryAlbumRecyclerAdapter.notifyDataSetChanged();
                    }
                }));
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
                            fmHistoryBottomPlayCover.setImageBitmap(resource);
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
