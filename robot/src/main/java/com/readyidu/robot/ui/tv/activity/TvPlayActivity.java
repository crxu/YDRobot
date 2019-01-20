package com.readyidu.robot.ui.tv.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.readyidu.baiduvoice.laster.FloatView;
import com.readyidu.baiduvoice.laster.FloatViewInstance;
import com.readyidu.basic.utils.ToastUtils;
import com.readyidu.basic.widgets.EmptyView;
import com.readyidu.robot.AppConfig;
import com.readyidu.robot.R;
import com.readyidu.robot.api.impl.RobotServiceImpl;
import com.readyidu.robot.api.impl.TVServiceImpl;
import com.readyidu.robot.base.BaseVoiceBarActivity;
import com.readyidu.robot.component.ijk.listener.VideoAllCallBack;
import com.readyidu.robot.component.ijk.utils.NetWorkUtils;
import com.readyidu.robot.component.ijk.view.IjkPlayerView;
import com.readyidu.robot.component.ijk.widgets.HorizontalNavigationBar;
import com.readyidu.robot.component.ijk.widgets.HorizontalNavigationItemView;
import com.readyidu.robot.component.rxbus.RxBus;
import com.readyidu.robot.event.BaseMessageReceiverEvent;
import com.readyidu.robot.event.PlayCompleteEvent;
import com.readyidu.robot.event.tv.TvEvent;
import com.readyidu.robot.model.BaseListModel;
import com.readyidu.robot.model.BaseObjModel;
import com.readyidu.robot.model.business.tv.CommonChannel;
import com.readyidu.robot.model.business.tv.ProgrammeListModel;
import com.readyidu.robot.model.business.tv.ProgrammeModel;
import com.readyidu.robot.model.business.tv.TvChannel;
import com.readyidu.robot.model.business.tv.TvSource;
import com.readyidu.robot.model.business.tv.TvSourceDetail;
import com.readyidu.robot.model.business.tv.TvType;
import com.readyidu.robot.model.business.tv.base.BaseContent;
import com.readyidu.robot.model.business.tv.base.BaseData;
import com.readyidu.robot.ui.adapter.common.MultiItemTypeAdapter;
import com.readyidu.robot.ui.adapter.common.wrapper.HeaderAndFooterWrapper;
import com.readyidu.robot.ui.tv.adapter.MovieOnDemandSeeMoreAdapter;
import com.readyidu.robot.ui.tv.adapter.SitcomOnDemandSelectAdapter;
import com.readyidu.robot.ui.tv.adapter.TvSearchAdapter;
import com.readyidu.robot.ui.tv.adapter.template.TvCartoonDemandTemplate;
import com.readyidu.robot.ui.tv.adapter.template.TvCartoonLiveTemplate;
import com.readyidu.robot.ui.tv.adapter.template.TvChannelLiveTemplate;
import com.readyidu.robot.ui.tv.adapter.template.TvCustomerTemplate;
import com.readyidu.robot.ui.tv.adapter.template.TvLocalTemplate;
import com.readyidu.robot.ui.tv.adapter.template.TvMovieLiveTemplate;
import com.readyidu.robot.ui.tv.adapter.template.TvMovieOnDemandTemplate;
import com.readyidu.robot.ui.tv.adapter.template.TvSitcomLiveTemplate;
import com.readyidu.robot.ui.tv.adapter.template.TvSitcomOnDemandTemplate;
import com.readyidu.robot.ui.tv.utils.CustomerSourceUtils;
import com.readyidu.robot.ui.tv.utils.TvDataTransform;
import com.readyidu.robot.ui.tv.view.NetworkWarnDialog;
import com.readyidu.robot.ui.tv.view.SitcomHorizontalNavigationBar;
import com.readyidu.robot.ui.tv.view.TvClassifyHorizontalNavigationBar;
import com.readyidu.robot.ui.tv.view.flowlayout.FlowLayout;
import com.readyidu.robot.ui.tv.view.flowlayout.TagAdapter;
import com.readyidu.robot.ui.tv.view.flowlayout.TagFlowLayout;
import com.readyidu.robot.ui.tv.view.recyclerview.MyGridLayoutManager;
import com.readyidu.robot.ui.tv.view.recyclerview.MyLinearLayoutManager;
import com.readyidu.robot.ui.widgets.recyclerview.CustomerLayoutManager;
import com.readyidu.robot.utils.data.DataTranUtils;
import com.readyidu.robot.utils.event.EventUtils;
import com.readyidu.robot.utils.log.LogUtils;
import com.readyidu.robot.utils.net.NetworkUtils;
import com.readyidu.robot.utils.view.DialogUtils;
import com.readyidu.robot.utils.view.MeasureUtil;
import com.readyidu.utils.JLog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * @Autour: wlq
 * @Description: 电视直播
 * @Date: 2017/10/18 15:12
 * @Update: 2017/10/18 15:12
 * @UpdateRemark:
 * @Version: V1.0
 */
public class TvPlayActivity extends BaseVoiceBarActivity implements View.OnClickListener {

    private TextView mTvReloadReplay;
    private ImageView mIvReloadReplay;
    private RelativeLayout mRlReloadReplay;
    private TvClassifyHorizontalNavigationBar mClassifyHorizontalNavigationBar;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView mRvSelect;
    private View mViewLine;
    private LinearLayout mLlMovieClassify;
    private TextView mTvMovieClassify1;
    private TextView mTvMovieClassify2;

    private RelativeLayout mRlSeeMoreSelectLayout;
    private TextView mTvSeeMoreName;
    private RelativeLayout mRlSeeMoreSitcom;
    private SitcomHorizontalNavigationBar sitcomHorizontalNavigationBar;
    private GridView mGvSeeMoreMovie;
    private TagFlowLayout mTflTvLocalSeeMore;
    private ScrollView mSvTvLocalSeeMore;
    private TranslateAnimation mShowAnimation;
    private TranslateAnimation mHideAnimation;
    private HeaderAndFooterWrapper mAdapter;
    private SitcomOnDemandSelectAdapter mSitcomDemandSeeMoreAdapter;
    private MovieOnDemandSeeMoreAdapter mMovieDemandSeeMoreAdapter;
    private List<TvChannel> mSitcomOnDemandSelects = new ArrayList<>();    //电视剧点播查看全部剧集集合
    private List<TvChannel> mMovieOnDemandSeeMores = new ArrayList<>(); //电影点播查看更多集合
    private List<TvChannel> mTvChannels = new ArrayList<>();    //竖屏列表集合数据

    private NetworkWarnDialog networkDialog;
    private NetworkConnectChangedReceiver networkReceiver;

    private boolean isPause;

    private String mCurSource;
    private boolean isAlreadyPlaying;   //是否已经开始播放
    private boolean isChangeChannel;  //是否切换过频道

    private MyOrientationDetector mMyOrientationDetector;
    private boolean isInit = true;
    private boolean isHaveOrientation;  //是否允许有重力感应
    private Disposable subscribe2;
    private IjkPlayerView mPlayerView;
    private EmptyView emptyView;
    private EmptyView empty;
    private TvChannel searchTvChannel;
    private GridView showMoreGridView;
    private PopupWindow popupWindow;
    private View viewPlayLine2;
    private View sourceSetting;
    private View containerEmpty;
    private RelativeLayout mRlAddCustomChannel;
    private RelativeLayout mRlOpenFunction;
    private ArrayList<TvChannel> tvChannels;
    private FloatView floatView;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_tv_play;
    }

    @Override
    protected void bindViews() {
        super.bindViews();

        // 加载 IjkMediaPlayer 库
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        TvDataTransform.resetPosition();

        mPlayerView = (IjkPlayerView) findViewById(R.id.ijk_player_view);
        mRlReloadReplay = (RelativeLayout) findViewById(R.id.rl_reload_replay);
        mIvReloadReplay = (ImageView) findViewById(R.id.iv_reload_replay);
        mTvReloadReplay = (TextView) findViewById(R.id.tv_reload_replay);
        mClassifyHorizontalNavigationBar = (TvClassifyHorizontalNavigationBar) findViewById(R.id.horizontal_bar_tv_play);
        mViewLine = findViewById(R.id.view_play_line);
        refreshLayout = (SmartRefreshLayout) findViewById(R.id.refresh);
        mRvSelect = (RecyclerView) findViewById(R.id.rv_tv_play_select);
        emptyView = (EmptyView) findViewById(R.id.news_empty);
        viewPlayLine2 = findViewById(R.id.view_play_line2);
        mLlMovieClassify = (LinearLayout) findViewById(R.id.rl_tv_play_movie_classify_select);
        mTvMovieClassify1 = (TextView) findViewById(R.id.tv_tv_play_movie_classify1);
        mTvMovieClassify2 = (TextView) findViewById(R.id.tv_tv_play_movie_classify2);
        mRlSeeMoreSelectLayout = (RelativeLayout) findViewById(R.id.rl_see_more_select_layout);
        mTvSeeMoreName = (TextView) findViewById(R.id.iv_see_more_select_name);
        mRlSeeMoreSitcom = (RelativeLayout) findViewById(R.id.rl_see_more_sitcom_on_remand_layout);
        mGvSeeMoreMovie = (GridView) findViewById(R.id.gv_movie_on_demand_see_more);
        mTflTvLocalSeeMore = (TagFlowLayout) findViewById(R.id.tfl_tv_local_see_more);
        mSvTvLocalSeeMore = (ScrollView) findViewById(R.id.sv_tv_local_see_more);
        sitcomHorizontalNavigationBar = (SitcomHorizontalNavigationBar) findViewById(R.id.horizontal_bar_sitcom_select);
        empty = (EmptyView) findViewById(R.id.empty);
        sourceSetting = findViewById(R.id.container_source_setting);
        sourceSetting.setOnClickListener(this);
        containerEmpty = findViewById(R.id.container_empty);
        mRlAddCustomChannel = (RelativeLayout) findViewById(R.id.rl_add_custom_channel);
        mRlOpenFunction = (RelativeLayout) findViewById(R.id.rl_add_tv_live);
        View viewById1 = findViewById(R.id.add_source);
        ((RelativeLayout.LayoutParams) viewById1.getLayoutParams()).topMargin = MeasureUtil.dip2px(mContext, 30);
        viewById1.setOnClickListener(this);
        View viewById = findViewById(R.id.txt_open_function);
        ((RelativeLayout.LayoutParams) viewById.getLayoutParams()).topMargin = MeasureUtil.dip2px(mContext, 30);
        viewById.setOnClickListener(this);

        findViewById(R.id.iv_see_more_layout_close).setOnClickListener(this);
        mTvMovieClassify1.setOnClickListener(this);
        mTvMovieClassify2.setOnClickListener(this);
        mRlSeeMoreSelectLayout.setVisibility(View.GONE);

        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                TvDataTransform.requestChannel(TvDataTransform.getClassifyPositionTemp());
            }
        });

        startPlayLoading();
        mShowAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAnimation.setDuration(500);
        mHideAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f);
        mHideAnimation.setDuration(500);
        isHaveOrientation = true;
        mCurSource = "";
        mMyOrientationDetector = new MyOrientationDetector(mContext);

        MultiItemTypeAdapter<TvChannel> multiItemAdapter = new MultiItemTypeAdapter<>(this, mTvChannels);
        multiItemAdapter.addItemViewDelegate(new TvCustomerTemplate());
        multiItemAdapter.addItemViewDelegate(new TvChannelLiveTemplate());
        multiItemAdapter.addItemViewDelegate(new TvMovieLiveTemplate());
        multiItemAdapter.addItemViewDelegate(new TvSitcomLiveTemplate());
        multiItemAdapter.addItemViewDelegate(new TvCartoonLiveTemplate(this));
        multiItemAdapter.addItemViewDelegate(new TvMovieOnDemandTemplate(this));
        multiItemAdapter.addItemViewDelegate(new TvSitcomOnDemandTemplate());
        multiItemAdapter.addItemViewDelegate(new TvCartoonDemandTemplate());
        multiItemAdapter.addItemViewDelegate(new TvLocalTemplate());
        mAdapter = new HeaderAndFooterWrapper(multiItemAdapter);
        mRvSelect.setLayoutManager(new MyLinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false));
        mRvSelect.setAdapter(mAdapter);

        mSitcomDemandSeeMoreAdapter = new SitcomOnDemandSelectAdapter(mContext, mSitcomOnDemandSelects);
        showMoreGridView = (GridView) findViewById(R.id.grid_view);
        showMoreGridView.setAdapter(mSitcomDemandSeeMoreAdapter);
        mMovieDemandSeeMoreAdapter = new MovieOnDemandSeeMoreAdapter(mContext, mMovieOnDemandSeeMores);
        mGvSeeMoreMovie.setAdapter(mMovieDemandSeeMoreAdapter);

        mPlayerView.setVideoCallBack(new VideoAllCallBack() {

            @Override
            public void onEnterFullscreen() {
                LogUtils.i("AAAAA onEnterFullscreen");
                changeUI(false);
            }

            @Override
            public void onClickEnterFullscreen() {
                LogUtils.i("AAAAA onClickEnterFullscreen");
                temporarilyDisable();
//                Flowable.timer(1000, TimeUnit.MILLISECONDS)
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new Consumer<Long>() {
//                            @Override
//                            public void accept(Long aLong) throws Exception {
                changeUI(false);
//                            }
//                        });
            }

            @Override
            public void onQuitFullscreen() {
                LogUtils.i("AAAAA onQuitFullscreen");
                changeUI(true);
                posPlayingVideo();
                if (subscribe2 != null && !subscribe2.isDisposed()) {
                    subscribe2.dispose();
                }
                subscribe2 = Flowable.timer(300, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                if (floatView != null) {
                                    floatView.setVisibility(View.VISIBLE);
                                }
                            }
                        });
            }

            @Override
            public void onClickQuitFullscreen() {
                LogUtils.i("AAAAA onClickQuitFullscreen");
                changeUI(true);
                temporarilyDisable();

                if (subscribe2 != null && !subscribe2.isDisposed()) {
                    subscribe2.dispose();
                }
                subscribe2 = Flowable.timer(300, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                if (floatView != null) {
                                    floatView.setVisibility(View.VISIBLE);
                                }
                            }
                        });
            }

            @Override
            public void onActivityFinish() {
                onFinish();
                finish();
            }

            @Override
            public void onStartBuffer() {
                disableOrientation();
            }

            @Override
            public void onEndBuffer() {
                if (isHaveOrientation) {
                    enableOrientation();
                }
            }

            @Override
            public void startPlay() {
                if (isHaveOrientation) {
                    enableOrientation();
                }
                isAlreadyPlaying = true;
            }

            @Override
            public void stopPlay() {
                disableOrientation();
            }

            @Override
            public void onPlayError() {
                clearDisposable();
                switchChannelOrSource();
            }
        });
        //默认关闭重力感应，只有视频开始播放的时候才打开
        disableOrientation();
    }

    /**
     * 播放错误自动切源
     */
    private void switchChannelOrSource() {
        if (TvDataTransform.getSourcePosition() >= TvDataTransform.getmTvSources().size() - 1) {
            disableOrientation();
            if (mPlayerView != null) {
                mPlayerView.setLoadingPlayUI(true);
            }
        } else {
            showToast("没有获取到相关内容，即将切源");
            TvDataTransform.getCurrentTvSources().source = "";
            TvDataTransform.switchSource();
            JLog.e(""+TvDataTransform.getCurrentTvSources());
            requestPlaySource(TvDataTransform.getCurrentTvSources());
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 横竖屏切换布局
     */
    private void changeUI(boolean isPort) {
        synchronized (TvPlayActivity.class) {
            LogUtils.e("changeUI:" + isPort);
            if (isPort) {
                mViewLine.setVisibility(View.VISIBLE);
                mClassifyHorizontalNavigationBar.setVisibility(View.VISIBLE);
                refreshLayout.setVisibility(View.VISIBLE);
                mHideAnimation.setDuration(1);
                mRlSeeMoreSelectLayout.setVisibility(View.GONE);
                try {
                    if (floatView != null) {
                        floatView.setVisibility(View.VISIBLE);
                    }
                    FloatViewInstance.startWakeUp();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                mViewLine.setVisibility(View.GONE);
                mClassifyHorizontalNavigationBar.setVisibility(View.GONE);
                refreshLayout.setVisibility(View.GONE);
                try {
                    if (floatView != null) {
                        floatView.setVisibility(View.GONE);
                    }
                    FloatViewInstance.stopWakeUp();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void bindEvents() {
        super.bindEvents();

        requestChannelClassify();   //请求频道分类列表
        try {

            registerRxBus(PlayCompleteEvent.class, new Consumer<PlayCompleteEvent>() {
                @Override
                public void accept(PlayCompleteEvent event) throws Exception {
                    playVideo();
                }
            });

            // FIXME: 2017/12/4 切换分类、定位
            registerRxBus(TvEvent.ChannelChangedEvent.class, new Consumer<TvEvent.ChannelChangedEvent>() {
                @Override
                public void accept(TvEvent.ChannelChangedEvent event) throws Exception {
                    if (null == event)
                        return;

                    try {
                        if (mPlayerView != null) {
                            mPlayerView.loadList();
                        }

                        //恢复原状
                        emptyView.setVisibility(View.GONE);
                        mLlMovieClassify.setVisibility(View.GONE);
                        viewPlayLine2.setVisibility(View.GONE);
                        mTvMovieClassify1.setText("");
                        mTvMovieClassify2.setText("");
                        refreshLayout.finishRefresh();

                        //设置临时索引  要根据临时索引获取相应的数据
                        if (!mPlayerView.isShowList()) {
                            TvDataTransform.setClassifyPositionTemp(event.index);
                        }

                        int classifyPositionTemp = TvDataTransform.getClassifyPositionTemp();
                        int kindPosition = TvDataTransform.getKindPositionTemp();
                        int classifyPositionSecond = TvDataTransform.getClassifyPositionSecondTemp();

                        CommonChannel commonChannel = TvDataTransform.getmAllChannels().get(classifyPositionTemp);

                        LogUtils.e("classifyPositionTemp:" + classifyPositionTemp + "commonChannel:" + commonChannel);
                        refreshUIData(commonChannel);
                        if (commonChannel != null && (commonChannel.getTvLives().size() > 0 || commonChannel.getType() == -2)) {

                            List<TvChannel> tvChannels = commonChannel.getTvLives();
                            ArrayList<TvChannel> onDemands = commonChannel.getOnDemands();

                            //清空数据
                            TvDataTransform.getCurrentTvChannels().clear();
                            switch (commonChannel.getType()) {//客户端渲染类型  -2 自定义源  -1 电视直播（没有授权） 0：央视卫视 1：地方 2：电影 3：少儿 4：电视剧
                                case -2:
                                    setPortListData(CustomerSourceUtils.getTvChannels(mContext));
                                    TvDataTransform.getCurrentTvChannels().addAll(CustomerSourceUtils.getTvChannels(mContext));
                                    break;
                                case 0:     //央视&卫视&本地
                                    setPortListData(tvChannels);
                                    TvDataTransform.getCurrentTvChannels().addAll(tvChannels);
                                    break;
                                case 1:     //地方     横屏三级分类使用数据  channels
                                    if (mPlayerView.isShowList()) {
                                        TvDataTransform.getClassifyChannelSecondary().clear();
                                        TvDataTransform.getClassifyChannelSecondary().addAll(tvChannels);
                                    } else {
                                        //竖屏数据
                                        setPortListData(tvChannels);
                                    }
                                    if (tvChannels.size() > classifyPositionSecond) {//分类下面的频道默认使用三级分类里面的第一项数据
                                        TvChannel tvChannel = tvChannels.get(classifyPositionSecond);
                                        if (tvChannel != null && tvChannel.tvDemands != null && tvChannel.tvDemands.size() > 0) {
                                            TvDataTransform.getCurrentTvChannels().addAll(tvChannel.tvDemands);
                                        }
                                    }
                                    break;
                                case 2:     //电影
                                    if (mPlayerView.isShowList()) {
                                        if (1 == kindPosition) {    ////点播  使用onDemands 里面的数据
                                            TvDataTransform.getClassifyChannelSecondary().clear();
                                            TvDataTransform.getClassifyChannelSecondary().addAll(onDemands);
                                        }
                                    } else {    //设置竖屏数据
                                        switch (kindPosition) {
                                            case 0:
                                                setPortListData(tvChannels);
                                                break;
                                            case 1:
                                                setPortListData(onDemands);
                                                break;
                                        }
                                    }
                                    switch (kindPosition) {
                                        case 0: //电影轮播 使用channels 里面的数据
                                            TvDataTransform.getCurrentTvChannels().addAll(tvChannels);
                                            break;
                                        case 1: //点播 使用onDemands 里面的数据
                                            if (onDemands.size() > classifyPositionSecond) {//默认选择第一项
                                                TvChannel tvChannel1 = onDemands.get(classifyPositionSecond);
                                                if (tvChannel1 != null && tvChannel1.tvDemands != null && tvChannel1.tvDemands.size() > 0) {
                                                    TvDataTransform.getCurrentTvChannels().addAll(tvChannel1.tvDemands);
                                                }
                                            }
                                            break;
                                    }
                                    break;
                                case 3: //少儿
                                    switch (kindPosition) {
                                        case 0://电影轮播 使用channels  里面的数据
                                            TvDataTransform.getCurrentTvChannels().addAll(tvChannels);
                                            if (!mPlayerView.isShowList()) {
                                                //设置竖屏数据
                                                setPortListData(tvChannels);
                                            }
                                            break;
                                        case 1://点播  使用onDemands 里面的数据
                                            TvDataTransform.getCurrentTvChannels().addAll(onDemands);
                                            if (!mPlayerView.isShowList()) {
                                                //设置竖屏数据
                                                setPortListData(onDemands);
                                            }
                                            break;
                                    }
                                    break;
                                case 4: //电视剧
                                    switch (kindPosition) {
                                        case 0://电影轮播 使用channels  里面的数据
                                            TvDataTransform.getCurrentTvChannels().addAll(tvChannels);
                                            if (!mPlayerView.isShowList()) {
                                                //设置竖屏数据
                                                setPortListData(tvChannels);
                                            }
                                            break;
                                        case 1://点播  使用onDemands 里面的数据
//                                        TvDataTransform.getCurrentTvChannels().addAll(onDemands);
                                            TvDataTransform.getClassifyChannelSecondary().clear();
                                            TvDataTransform.getClassifyChannelSecondary().addAll(onDemands);

                                            if (onDemands.size() > classifyPositionSecond) {//默认选择第一项
                                                TvChannel tvChannel1 = onDemands.get(classifyPositionSecond);
                                                if (tvChannel1 != null && tvChannel1.tvDemands != null && tvChannel1.tvDemands.size() > 0) {
                                                    TvDataTransform.getCurrentTvChannels().addAll(tvChannel1.tvDemands);
                                                } else {//默认请求第一个电视剧的剧集
                                                    if (mPlayerView != null) {
                                                        mPlayerView.loadSitcomList();
                                                    }
                                                    if (mPlayerView.isShowList()) {//供横屏请求数据使用
                                                        RxBus.getInstance().send(new TvEvent.SeeMoreEvent(tvChannel1, classifyPositionSecond));
                                                    }
                                                }
                                            }
                                            if (!mPlayerView.isShowList()) {
                                                //设置竖屏数据
                                                setPortListData(onDemands);
                                            }
                                            break;
                                    }
                                    break;
                                default:
                                    mLlMovieClassify.setVisibility(View.GONE);
                                    viewPlayLine2.setVisibility(View.GONE);
                                    break;
                            }
                            if (mPlayerView.isShowList()) {
                                if (mPlayerView != null) {
                                    mPlayerView.changedListData();
                                }
                            } else {
                                seeMoreShowHiddenWithAnimation(false);

                                mClassifyHorizontalNavigationBar.setCurrentChannelItem(event.index);
                            }
                            mAdapter.notifyDataSetChanged();

                            if (searchTvChannel != null) {
                                jump(commonChannel, searchTvChannel, classifyPositionTemp);
                                searchTvChannel = null;
                            }
                        } else {//没有数据的时候
                            mLlMovieClassify.setVisibility(View.GONE);
                            viewPlayLine2.setVisibility(View.GONE);
                            LogUtils.e("event.status:" + event.status + " NetworkUtils.isAvailable(mContext):" + NetworkUtils.isAvailable(mContext));
                            if (event.status) {//网络请求 没有数据   说明是真的没有数据
                                if (!event.requestSuccess) {
                                    emptyView.setVisibility(View.VISIBLE);
                                    emptyView.changeStatus(EmptyView.LOADING_ERR, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            refreshLayout.autoRefresh();
                                        }
                                    });
                                } else {
                                    emptyView.changeStatus(EmptyView.NO_DATA);
                                    emptyView.setVisibility(View.VISIBLE);

                                    if (commonChannel != null && commonChannel.getType() == -2) {
                                        emptyView.setVisibility(View.GONE);
                                    }
                                }
                                if (mPlayerView != null) {
                                    mPlayerView.listNoData();
                                }
                                if (searchTvChannel != null) {
                                    jump(commonChannel, searchTvChannel, classifyPositionTemp);
                                    searchTvChannel = null;
                                }
                            } else {
                                emptyView.setVisibility(View.VISIBLE);
                                emptyView.changeStatus(EmptyView.LOADING);
                                TvDataTransform.requestChannel(TvDataTransform.getClassifyPositionTemp());
                            }
                        }
                    } catch (Exception e) {
                        LogUtils.e(e);
                    }
                }
            });

            // FIXME: 2017/12/4 切源操作
            registerRxBus(TvEvent.ChangeSourceEvent.class, new Consumer<TvEvent.ChangeSourceEvent>() {
                @Override
                public void accept(TvEvent.ChangeSourceEvent changeSourceEvent) throws Exception {
                    if (null != changeSourceEvent && changeSourceEvent.tvSourceDetail != null) {
                        requestPlaySource(changeSourceEvent.tvSourceDetail);
                    } else {
                        if (mPlayerView != null) {
                            mPlayerView.disMissSelectSourceView();
                        }
                    }
                }
            });

            // FIXME: 2017/12/4 播放视频事件接收
            registerRxBus(TvEvent.PlayEvent.class, new Consumer<TvEvent.PlayEvent>() {
                @Override
                public void accept(TvEvent.PlayEvent playEvent) throws Exception {
                    if (playEvent == null || playEvent.tvChannel == null) {
                        return;
                    }
                    TvChannel tvChannel = playEvent.tvChannel;
                    mCurrentTvChannel = tvChannel;
                    if (mPlayerView != null) {      //切换节目--》隐藏剧集列表、切换源列表
                        if (mPlayerView.isFullscreen()) {
                            mPlayerView.showList(false);
                        }
                        mPlayerView.disMissSelectSourceView();
                    }

                    if (isSitcomOnDemand(tvChannel)) {//如果是电视剧点播的话单独处理
                        TvDataTransform.getmTvSources().clear();
                        if (!TextUtils.isEmpty(tvChannel.s)) {
                            requestPlaySource(null);
                        } else {
                            showToast("没有源可播放");
                        }
                    } else {
                        if (tvChannel.o != null && tvChannel.o.size() > 0) {
                            TvDataTransform.getmTvSources().clear();
                            TvDataTransform.getmTvSources().addAll(tvChannel.o);
                            TvDataTransform.setSourcePosition(0);
                            requestPlaySource(TvDataTransform.getCurrentTvSources());
                        } else {
                            showToast("没有源可播放");
                        }
                    }
                    if (!playEvent.isInit) {
                        isChangeChannel = true;
                    }
                    mAdapter.notifyDataSetChanged();
                }
            });

            // FIXME: 2017/12/4 查看更多事件监听
            registerRxBus(TvEvent.SeeMoreEvent.class, new Consumer<TvEvent.SeeMoreEvent>() {
                @Override
                public void accept(final TvEvent.SeeMoreEvent seeMoreEvent) throws Exception {
                    if (null == seeMoreEvent)
                        return;

                    try {

                        final TvChannel tvChannel = seeMoreEvent.tvChannel;
                        int type = tvChannel.type;
                        switch (type) {
                            case 1:     //地方台
                                if (null != tvChannel.tvDemands && tvChannel.tvDemands.size() > 0) {
                                    seeMoreShowHiddenWithAnimation(true);
                                    mTvSeeMoreName.setText(tvChannel.g);
                                    mRlSeeMoreSitcom.setVisibility(View.GONE);
                                    mGvSeeMoreMovie.setVisibility(View.GONE);
                                    mSvTvLocalSeeMore.setVisibility(View.VISIBLE);

                                    final List<String> list = new ArrayList<>();
                                    final List<TvChannel> tvDemands = tvChannel.tvDemands;
                                    final int size = tvDemands.size();
                                    for (int i = 0; i < size; i++) {
                                        TvChannel tvDemand = tvDemands.get(i);
                                        list.add(tvDemand.c);
                                    }
                                    mTflTvLocalSeeMore.setAdapter(new TagAdapter<String>(list) {
                                        @Override
                                        public View getView(FlowLayout parent, int position, String s) {
                                            TextView tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.
                                                    layout_tv_classify_local_see_more_textview, mTflTvLocalSeeMore, false);
                                            tv.setTag(position);

                                            tv.setText(s);
                                            return tv;
                                        }
                                    });
                                    mTflTvLocalSeeMore.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
                                        @Override
                                        public boolean onTagClick(View view, int position, FlowLayout parent) {

                                            View viewWithTag = mTflTvLocalSeeMore.findViewWithTag(TvDataTransform.getChannelPosition());
                                            if (viewWithTag != null) {
                                                View parent1 = (View) viewWithTag.getParent();
                                                if (parent1 != null) {
                                                    parent1.setSelected(false);
                                                }
                                            }
                                            view.setSelected(true);

                                            TvDataTransform.setClassifyPosition(TvDataTransform.getClassifyPositionTemp());
                                            TvDataTransform.setClassifyPositionSecond(seeMoreEvent.classifyPos);
                                            TvDataTransform.setChannelPosition(position);

                                            TvDataTransform.getCurrentTvChannels().clear();
                                            TvDataTransform.getCurrentTvChannels().addAll(tvChannel.tvDemands);
                                            RxBus.getInstance().send(new TvEvent.PlayEvent(tvDemands.get(position)));
                                            setTvChannelData(tvDemands);

                                            seeMoreShowHiddenWithAnimation(false);

                                            mAdapter.notifyDataSetChanged();

                                            return true;
                                        }
                                    });
                                    for (int i = 0; i < mTflTvLocalSeeMore.getChildCount(); i++) {
                                        View tv = mTflTvLocalSeeMore.getChildAt(i);

                                        if (TvDataTransform.getClassifyPosition() == TvDataTransform.getClassifyPositionTemp()
                                                && TvDataTransform.getClassifyPositionSecond() == seeMoreEvent.classifyPos
                                                && TvDataTransform.getChannelPosition() == i) {
                                            tv.setSelected(true);
                                        } else {
                                            tv.setSelected(false);
                                        }
                                    }

                                    // FIXME: 2017/12/5 滚动到顶部
                                    if (tvChannel.kindPosition == TvDataTransform.getKindPosition()
                                            && tvChannel.classifyPosition == TvDataTransform.getClassifyPosition()
                                            && tvChannel.classifyPositionSecond == TvDataTransform.getClassifyPositionSecond()) {
                                        View childAt = mTflTvLocalSeeMore.getChildAt(TvDataTransform.getChannelPosition());
                                        if (childAt != null) {
                                            mTflTvLocalSeeMore.requestChildFocus(childAt, childAt);
                                        }
                                    } else {
                                        View childAt = mTflTvLocalSeeMore.getChildAt(0);
                                        if (childAt != null) {
                                            mTflTvLocalSeeMore.requestChildFocus(childAt, childAt);
                                        }
                                    }
                                }
                                break;
                            case 2:     //电影
                                if (null != tvChannel.tvDemands && tvChannel.tvDemands.size() > 0) {
                                    seeMoreShowHiddenWithAnimation(true);
                                    mTvSeeMoreName.setText(tvChannel.c);
                                    mRlSeeMoreSitcom.setVisibility(View.GONE);
                                    mSvTvLocalSeeMore.setVisibility(View.GONE);
                                    mGvSeeMoreMovie.setVisibility(View.VISIBLE);

                                    mMovieOnDemandSeeMores.clear();
                                    mMovieOnDemandSeeMores.addAll(tvChannel.tvDemands);
                                    setTvChannelData(tvChannel.tvDemands);
                                    mMovieDemandSeeMoreAdapter.setmClassifyPos(seeMoreEvent.classifyPos);
                                    mMovieDemandSeeMoreAdapter.notifyDataSetChanged();
                                    mGvSeeMoreMovie.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (tvChannel.kindPosition == TvDataTransform.getKindPosition()
                                                    && tvChannel.classifyPosition == TvDataTransform.getClassifyPosition()
                                                    && tvChannel.classifyPositionSecond == TvDataTransform.getClassifyPositionSecond()) {
                                                mGvSeeMoreMovie.setSelection(TvDataTransform.getChannelPosition());
                                            } else {
                                                mGvSeeMoreMovie.setSelection(0);
                                            }
                                        }
                                    });
                                }
                                break;
                            case 4:     //电视剧
                                if (mPlayerView != null) {
                                    mPlayerView.loadSitcomList();
                                }
                                if (tvChannel.tvDemands != null && tvChannel.tvDemands.size() > 0) {
                                    showSitcom(tvChannel.tvDemands, tvChannel, seeMoreEvent);
                                } else {
                                    addDisposable(TVServiceImpl
                                            .getNumberOfDramas(tvChannel.i)
                                            .subscribeWith(new DisposableObserver<BaseListModel<TvChannel>>() {
                                                @Override
                                                public void onNext(BaseListModel<TvChannel> baseListModel) {
                                                    showSitcom(baseListModel.data, tvChannel, seeMoreEvent);
                                                }

                                                @Override
                                                public void onError(Throwable e) {
                                                    if (mPlayerView != null && mPlayerView.isShowList()) {
                                                        mPlayerView.loadSitcomListErr();
                                                    }
                                                    showToast("该电视剧暂不可播放，请查看其它内容");
                                                }

                                                @Override
                                                public void onComplete() {
                                                }
                                            }));
                                }
                                break;
                        }
                    } catch (Exception e) {
                        LogUtils.e(e);
                    }

                }
            });

            // FIXME: 2017/12/4 自动切换节目事件监听
            registerRxBus(TvEvent.ChangeProgromme.class, new Consumer<TvEvent.ChangeProgromme>() {
                @Override
                public void accept(TvEvent.ChangeProgromme changeProgromme) throws Exception {
                    if (changeProgromme.tvChannel != null) {
                        setPrograme(changeProgromme.tvChannel, mPlayerView.getmTvTitle(), mPlayerView.getRvTvLandChannel(), mRvSelect);
                    }
                }
            });

            // FIXME: 2017/12/4 搜索并定位事件接收监听
            registerRxBus(TvEvent.SearchTvEvent.class, new Consumer<TvEvent.SearchTvEvent>() {
                @Override
                public void accept(final TvEvent.SearchTvEvent searchTvEvent) throws Exception {
                    if (searchTvEvent != null && searchTvEvent.tv != null) {
                        searchTvChannel = searchTvEvent.tv;
                        ArrayList<TvType> tvTypes = TvDataTransform.getmTvTypes();
                        if (tvTypes != null && tvTypes.size() > 0) {
                            for (int i = 0; i < tvTypes.size(); i++) {
                                if (searchTvEvent.tv.t == tvTypes.get(i).id) {
                                    CommonChannel commonChannel = TvDataTransform.getmAllChannels().get(i);
                                    if (commonChannel != null) {
                                        jump(commonChannel, searchTvEvent.tv, i);
                                    } else {
                                        emptyView.setVisibility(View.VISIBLE);
                                        emptyView.changeStatus(EmptyView.LOADING);
                                        mClassifyHorizontalNavigationBar.setCurrentChannelItem(i);
                                        TvDataTransform.setClassifyPositionTemp(i);
                                        TvDataTransform.requestChannel(TvDataTransform.getClassifyPositionTemp());
                                    }
                                    return;
                                }
                            }
                            showToast("没有找到该节目");
                        } else {
                            Flowable.timer(1000, TimeUnit.MILLISECONDS)
                                    .subscribe(new Consumer<Long>() {
                                        @Override
                                        public void accept(Long aLong) throws Exception {
                                            RxBus.getInstance().send(searchTvEvent);
                                        }
                                    });
                        }
                    }
                }
            });

            registerRxBus(BaseMessageReceiverEvent.class, new Consumer<BaseMessageReceiverEvent>() {
                @Override
                public void accept(BaseMessageReceiverEvent messageReceiverEvent) throws Exception {
                    if (messageReceiverEvent != null && messageReceiverEvent.message != null) {
                        final ArrayList<TvChannel> tvChannels = (ArrayList<TvChannel>) DataTranUtils.tranTvDetail(messageReceiverEvent.message);
                        if (tvChannels != null && tvChannels.size() > 0) {
                            Flowable.timer(500, TimeUnit.MILLISECONDS)
                                    .subscribe(new Consumer<Long>() {
                                        @Override
                                        public void accept(Long aLong) throws Exception {
                                            RxBus.getInstance().send(new TvEvent.SearchTvEvent(tvChannels.get(0)));
                                        }
                                    });
                        }
                    } else {
                        ToastUtils.showShortToast(mContext, "没有搜到数据");
                    }
                }
            });

            registerRxBus(TvEvent.CustomerSourceChange.class, new Consumer<TvEvent.CustomerSourceChange>() {
                @Override
                public void accept(TvEvent.CustomerSourceChange customerSourceChange) throws Exception {
                    if (TvDataTransform.getClassifyPositionTemp() == 0) {

                        if (CustomerSourceUtils.getTvChannels(mContext).size() == 0) {
                            containerEmpty.setVisibility(View.VISIBLE);
                            sourceSetting.setVisibility(View.GONE);
                        } else {
                            containerEmpty.setVisibility(View.GONE);
                            sourceSetting.setVisibility(View.VISIBLE);
                            setTvChannelData(CustomerSourceUtils.getTvChannels(mContext));
                            setPortListData(CustomerSourceUtils.getTvChannels(mContext));
                            mAdapter.notifyDataSetChanged();
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }
            });

            registerRxBus(TvEvent.CustomerSourceNameChange.class, new Consumer<TvEvent.CustomerSourceNameChange>() {
                @Override
                public void accept(TvEvent.CustomerSourceNameChange customerSourceChange) throws Exception {
                    View viewWithTag = mClassifyHorizontalNavigationBar.findViewWithTag(0);
                    if (viewWithTag instanceof HorizontalNavigationItemView) {
                        String name = CustomerSourceUtils.getCheckModel().getDefineName() + "";
                        TvDataTransform.getmTvTypes().get(0).type = name;

                        if (!CustomerSourceUtils.getCheckModel().isBindling()
                                || TvDataTransform.getmTvTypes() == null || TvDataTransform.getmTvTypes().size() == 0) {
                            name = space + name + space;
                        }
                        ((HorizontalNavigationItemView) viewWithTag).setChannelTitle(name);
                    }
                }
            });
            registerRxBus(TvEvent.BindSuccessChange.class, new Consumer<TvEvent.BindSuccessChange>() {
                @Override
                public void accept(TvEvent.BindSuccessChange customerSourceChange) throws Exception {
                    requestChannelClassify();   //请求频道分类列表
                }
            });
        } catch (Exception e) {
            LogUtils.e(e);
        }

        //自动定位第一个节目
        Intent intent = getIntent();
        tvChannels = (ArrayList<TvChannel>) intent.getSerializableExtra("searchData");
    }

    /**
     * 设置电视剧集UI数据
     *
     * @param baseListModel
     * @param tvChannel
     * @param seeMoreEvent
     */
    private void showSitcom(List<TvChannel> baseListModel, TvChannel tvChannel, TvEvent.SeeMoreEvent seeMoreEvent) {
        try {
            final List<TvChannel> tvChannels = baseListModel;
            if (null != tvChannels) {
                if (tvChannel.tvDemands != baseListModel) {
                    tvChannel.tvDemands.clear();
                    tvChannel.tvDemands.addAll(tvChannels);
                    for (TvChannel tvChannel1 : tvChannels) {
                        tvChannel1.type = tvChannel.type;
                        tvChannel1.classifyPositionSecond = seeMoreEvent.classifyPos;
                        tvChannel1.classifyPosition = tvChannel.classifyPosition;
                        tvChannel1.kindPosition = tvChannel.kindPosition;
                        tvChannel1.isMovie = true;
                    }
                }
                if (mPlayerView != null && mPlayerView.isShowList()) {
                    RxBus.getInstance().send(new TvEvent.ChannelChangedEvent(TvDataTransform.getClassifyPositionTemp(),
                            TvDataTransform.getKindPositionTemp()));
                } else {
                    if (tvChannel.classifyPosition == TvDataTransform.getClassifyPositionTemp()) {
                        TvDataTransform.getCurrentTvChannels().clear();
                        TvDataTransform.getCurrentTvChannels().addAll(tvChannels);
                    }
                }
            }

            final List<TvChannel> tvTemps = new ArrayList<>();
            if (null != tvChannels && tvChannels.size() > 0) {
                seeMoreShowHiddenWithAnimation(true);
                mTvSeeMoreName.setText(tvChannel.c);
                mRlSeeMoreSitcom.setVisibility(View.VISIBLE);
                mGvSeeMoreMovie.setVisibility(View.GONE);
                mSvTvLocalSeeMore.setVisibility(View.GONE);
                mSitcomDemandSeeMoreAdapter.setClassifyPos(seeMoreEvent.classifyPos);

                final int pageTotal = tvChannels.size();
                final int page = pageTotal / 30 + 1;
                ArrayList<String> list = new ArrayList<>();
                if (page == 1) {
                    list.add("1-" + pageTotal);
                    tvTemps.clear();
                    tvTemps.addAll(tvChannels);
                } else {
                    for (int i = 0; i < page; i++) {
                        if (i < page - 1) {
                            list.add((30 * i + 1) + "-" + (30 * (i + 1)));
                        } else {
                            list.add((30 * i + 1) + "-" + pageTotal);
                        }
                    }
                    tvTemps.clear();
                    tvTemps.addAll(tvChannels.subList(0, 30));
                }
                sitcomHorizontalNavigationBar.setChannelSplit(false);
                sitcomHorizontalNavigationBar.setItems(list);
                sitcomHorizontalNavigationBar.setCurrentChannelItem(0);
                mSitcomDemandSeeMoreAdapter.setCurrentPage(0);
                sitcomHorizontalNavigationBar.addOnHorizontalNavigationSelectListener(new HorizontalNavigationBar.
                        OnHorizontalNavigationSelectListener() {
                    @Override
                    public void select(int index) {

                        tvTemps.clear();
                        if (index < page - 1) {
                            if (page == 1) {
                                tvTemps.addAll(tvChannels);
                            } else {
                                tvTemps.addAll(tvChannels.subList(30 * index, 30 * (index + 1)));
                            }
                        } else {
                            tvTemps.addAll(tvChannels.subList(30 * index, pageTotal));
                        }
                        mSitcomOnDemandSelects.clear();
                        mSitcomOnDemandSelects.addAll(tvTemps);
                        mSitcomDemandSeeMoreAdapter.setCurrentPage(index);
                        mSitcomDemandSeeMoreAdapter.notifyDataSetChanged();
                    }
                });

                mSitcomOnDemandSelects.clear();
                mSitcomOnDemandSelects.addAll(tvTemps);
                mSitcomDemandSeeMoreAdapter.notifyDataSetChanged();
                showMoreGridView.post(new Runnable() {
                    @Override
                    public void run() {
                        showMoreGridView.setSelection(0);
                    }
                });
            } else {
                if (mPlayerView != null && mPlayerView.isShowList()) {
                    mPlayerView.loadSitcomListErr();
                }
                showToast("该电视剧暂不可播放，请查看其它内容");
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    /**
     * 匹配完毕自动跳转到该节目
     *
     * @param commonChannel
     * @param searchTvChannel
     * @param position
     */
    private void jump(CommonChannel commonChannel, TvChannel searchTvChannel, int position) {
        try {
            if (commonChannel != null) {
                ArrayList<TvChannel> tvLives = commonChannel.getTvLives();
                if (tvLives != null && tvLives.size() > 0) {
                    for (int j = 0; j < tvLives.size(); j++) {
                        TvChannel tvChannel = tvLives.get(j);

                        if (commonChannel.getType() == 1) {
                            ArrayList<TvChannel> tvDemands = tvChannel.tvDemands;
                            if (tvDemands != null && tvDemands.size() > 0) {
                                for (int i = 0; i < tvDemands.size(); i++) {
                                    TvChannel tvChannel1 = tvDemands.get(i);

                                    if (tvChannelEqual(searchTvChannel, tvChannel1, i, position)) {
                                        return;
                                    }
                                }
                            }
                        } else {
                            if (tvChannelEqual(searchTvChannel, tvChannel, j, position)) {
                                return;
                            }
                        }
                    }
                }
                showToast("没有找到该节目");
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    /**
     * 判断是否匹配
     *
     * @param searchTvChannel
     * @param tvChannel
     * @param channelPosition
     * @param position
     * @return
     */
    private boolean tvChannelEqual(TvChannel searchTvChannel, TvChannel tvChannel, int channelPosition, int position) {
        if (searchTvChannel.i == tvChannel.i) {
            TvDataTransform.setClassifyPosition(tvChannel.classifyPosition);
            TvDataTransform.setKindPosition(tvChannel.kindPosition);
            TvDataTransform.setClassifyPositionSecond(tvChannel.classifyPositionSecond);
            TvDataTransform.setChannelPosition(channelPosition);

            RxBus.getInstance().send(new TvEvent.ChannelChangedEvent(position));
            RxBus.getInstance().send(new TvEvent.PlayEvent(tvChannel));

            Flowable.timer(200, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            isChangeChannel = true;
                            posPlayingVideo();
                        }
                    });

            return true;
        }
        return false;
    }

    /**
     * 设置播放数据源
     *
     * @param tvChannels
     */
    private void setTvChannelData(List<TvChannel> tvChannels) {
        TvDataTransform.getCurrentTvChannels().clear();
        TvDataTransform.getCurrentTvChannels().addAll(tvChannels);
    }

    /**
     * 设置竖屏数据
     *
     * @param tvChannels
     */
    private void setPortListData(List<TvChannel> tvChannels) {
        mTvChannels.clear();
        mTvChannels.addAll(tvChannels);
    }

    /**
     * 刷新ui样式
     *
     * @param commonChannel
     */
    private void refreshUIData(CommonChannel commonChannel) {
        try {
            int type = commonChannel.getType();   //0：央视&卫视&本地；1：地方；2：电影；3：少儿； 4：电视剧
            switch (type) {
                case -2:    //自定义频道
                    mLlMovieClassify.setVisibility(View.GONE);
                    viewPlayLine2.setVisibility(View.GONE);
                    if (CustomerSourceUtils.getTvChannels(mContext).size() == 0) {
                        containerEmpty.setVisibility(View.VISIBLE);
                        sourceSetting.setVisibility(View.GONE);
                    } else {
                        sourceSetting.setVisibility(View.VISIBLE);
                        containerEmpty.setVisibility(View.GONE);
                    }
                    mRlAddCustomChannel.setVisibility(View.VISIBLE);
                    mRlOpenFunction.setVisibility(View.GONE);
                    break;
                case -1:    //电视直播
                    mLlMovieClassify.setVisibility(View.GONE);
                    viewPlayLine2.setVisibility(View.GONE);
                    sourceSetting.setVisibility(View.GONE);
                    containerEmpty.setVisibility(View.VISIBLE);
                    mRlAddCustomChannel.setVisibility(View.GONE);
                    mRlOpenFunction.setVisibility(View.VISIBLE);
                    break;
                case 0:
                case 1:
                    mLlMovieClassify.setVisibility(View.GONE);
                    viewPlayLine2.setVisibility(View.GONE);
                    sourceSetting.setVisibility(View.GONE);
                    containerEmpty.setVisibility(View.GONE);
                    break;
                case 2:
                case 3:
                case 4:
                    if (null != commonChannel.getOnDemands() && null != commonChannel.getTvLives()
                            && commonChannel.getOnDemands().size() > 0 && commonChannel.getTvLives().size() > 0) {

                        mLlMovieClassify.setVisibility(View.VISIBLE);
                        viewPlayLine2.setVisibility(View.VISIBLE);
                        if (TvDataTransform.getKindPositionTemp() == 0) {
                            mTvMovieClassify1.setTextColor(getResources().getColor(R.color.theme_color));
                            mTvMovieClassify2.setTextColor(getResources().getColor(R.color.content_color));
                        } else {
                            mTvMovieClassify1.setTextColor(getResources().getColor(R.color.content_color));
                            mTvMovieClassify2.setTextColor(getResources().getColor(R.color.theme_color));
                        }
                    } else {

                        mLlMovieClassify.setVisibility(View.GONE);
                        viewPlayLine2.setVisibility(View.GONE);
                    }
                    sourceSetting.setVisibility(View.GONE);
                    containerEmpty.setVisibility(View.GONE);
                    break;
                default:
                    mLlMovieClassify.setVisibility(View.GONE);
                    viewPlayLine2.setVisibility(View.GONE);
                    sourceSetting.setVisibility(View.GONE);
                    containerEmpty.setVisibility(View.GONE);
                    break;
            }
            switch (commonChannel.getType()) {
                case 2:
                case 3:
                case 4:
                    List<TvChannel> onDemands = commonChannel.getOnDemands();
                    List<TvChannel> onLives = commonChannel.getTvLives();
                    String demandName;
                    String liveName;
                    if (null != onLives && onLives.size() > 0) {
                        liveName = onLives.get(0).g;
                        mTvMovieClassify1.setText(TextUtils.isEmpty(liveName) ? "" : liveName);
                    }
                    if (null != onDemands && onDemands.size() > 0) {
                        demandName = onDemands.get(0).g;
                        mTvMovieClassify2.setText(TextUtils.isEmpty(demandName) ? "" : demandName);
                    }
                    break;
            }
            switch (commonChannel.getType()) {
                case 0:
                case 1:
                case 2:
                case 4:
                    mRvSelect.setLayoutManager(new MyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                    break;
                case 3:
                    if (TvDataTransform.getKindPositionTemp() == 1) {
                        mRvSelect.setLayoutManager(new MyGridLayoutManager(this, 2));
                    } else {
                        mRvSelect.setLayoutManager(new MyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // FIXME: 2017/12/2 设置节目信息
    private void setPrograme(TvChannel tvChannel, TextView titleView, RecyclerView... recyclerViews) {
        if (tvChannel.type != -2) {
            if (tvChannel.programmeModels != null && tvChannel.programmeModels.size() > 0) {
                refreshPlayBill(tvChannel, titleView, recyclerViews);
            } else {
                getProgrammeList(tvChannel, titleView, recyclerViews);
            }
        } else {

            setContent(tvChannel.c, tvChannel, titleView, recyclerViews);
        }
    }

    /**
     * 获取节目单列表
     *
     * @param tvChannel
     * @param titleView
     * @param recyclerViews
     */
    private void getProgrammeList(final TvChannel tvChannel, final TextView titleView, final RecyclerView... recyclerViews) {
        addDisposable(TVServiceImpl.getChannelPlaybill(String.valueOf(tvChannel.i))
                .subscribeWith(new DisposableObserver<ProgrammeListModel>() {
                    @Override
                    public void onNext(ProgrammeListModel programmeListModel) {
                        tvChannel.programmeModels.clear();

                        ArrayList<ProgrammeModel> todayProgram = programmeListModel.getTodayProgram();
                        if (todayProgram.size() > 0) {
                            tvChannel.programmeModels.addAll(todayProgram);
                        }
                        ArrayList<ProgrammeModel> tommorrowProgram = programmeListModel.getTommorrowProgram();

                        if (tommorrowProgram.size() > 0) {
                            tvChannel.programmeModels.addAll(tommorrowProgram);
                        }

                        refreshPlayBill(tvChannel, titleView, recyclerViews);
                    }

                    @Override
                    public void onError(Throwable e) {
                        refreshPlayBill(tvChannel, titleView, recyclerViews);
                    }

                    @Override
                    public void onComplete() {

                    }
                }));
    }

    /**
     * 刷新节目单
     *
     * @param tvChannel
     * @param titleView
     * @param recyclerViews
     */
    private void refreshPlayBill(TvChannel tvChannel, TextView titleView, RecyclerView... recyclerViews) {
        //结束上一次刷新
        if (programeSubscribe != null && !programeSubscribe.isDisposed()) {
            programeSubscribe.dispose();
        }

        try {
            long l = System.currentTimeMillis();

            //默认为暂无节目信息
            setPrograme(tvChannel, null, null, titleView, recyclerViews);

            if (tvChannel.programmeModels.size() > 0) {
                ProgrammeModel currentProgrammeModel = null;

                for (int i = 0; i < tvChannel.programmeModels.size(); i++) {
                    if (tvChannel.programmeModels.get(i).getShowTimeMillis() > System.currentTimeMillis()) {
                        setPrograme(tvChannel, currentProgrammeModel, tvChannel.programmeModels, titleView, recyclerViews);
                        break;
                    } else {
                        currentProgrammeModel = tvChannel.programmeModels.get(i);
                        tvChannel.programmeModels.remove(currentProgrammeModel);
                        i--;
                    }
                }
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    private Disposable programeSubscribe;

    /**
     * 匹配节目单之后设置节目   一个标题  两个 列表中的textView
     *
     * @param tvChannel
     * @param programmeModel
     * @param programmeModels
     * @param titleView
     * @param recyclerViews
     */
    private void setPrograme(final TvChannel tvChannel, ProgrammeModel programmeModel, final ArrayList<ProgrammeModel> programmeModels, final TextView titleView, final RecyclerView... recyclerViews) {
        //结束上一次刷新
        if (programeSubscribe != null && !programeSubscribe.isDisposed()) {
            programeSubscribe.dispose();
        }

        if (getCurrentChannel() != tvChannel) {
            return;
        }

        String channelName;

        if (tvChannel.isMovie) {
            channelName = tvChannel.c;
        } else {
            if (programmeModel != null) {
                channelName = programmeModel.getChannelName();
            } else {
                channelName = tvChannel.n;
            }

            if (TextUtils.isEmpty(channelName)) {
                channelName = "暂无节目信息";
            }
        }

        if (tvChannel.type == -2) {//自定义源显示源名称
            channelName = tvChannel.c;
        }

        setContent(channelName, tvChannel, titleView, recyclerViews);

        //延迟距离下个节目的时间设置下一个节目单
        if (programmeModels != null && programmeModels.size() > 0) {

            final ProgrammeModel programmeModel1 = programmeModels.get(0);
            programeSubscribe = Flowable.timer(programmeModel1.getShowTimeMillis() - System.currentTimeMillis() + 35 * 1000, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            programmeModels.remove(programmeModel1);
                            setPrograme(tvChannel, programmeModel1, programmeModels, titleView, recyclerViews);
                        }
                    });
        }
    }

    private void setContent(String content, TvChannel tvChannel, final TextView titleView, final RecyclerView... recyclerViews) {
        //设置数据
        if (!TextUtils.isEmpty(content)) {
            titleView.setText(content);
            if (tvChannel != null) {
                tvChannel.n = content;
                for (RecyclerView recyclerView : recyclerViews) {
                    View viewWithTag = recyclerView.findViewWithTag(tvChannel.i);
                    if (viewWithTag != null && viewWithTag instanceof TextView) {
                        ((TextView) viewWithTag).setText(content);
                    }
                }
            }
        }
    }

    TvChannel mCurrentTvChannel;

    //获取当前节目
    private TvChannel getCurrentChannel() {
        if (mCurrentTvChannel != null) {
            return mCurrentTvChannel;
        }
        return TvDataTransform.getCurrentTvChannel();
    }

    /**
     * 视频开始loading
     */
    private void startPlayLoading() {
        mRlReloadReplay.setEnabled(false);
        mRlReloadReplay.setVisibility(View.VISIBLE);
        mIvReloadReplay.setVisibility(View.GONE);
        mTvReloadReplay.setText("努力加载中...");
    }

    /**
     * 视频停止loading
     */
    private void cancelPlayLoading() {
        if (null != mRlReloadReplay && mRlReloadReplay.isShown()) {
            mRlReloadReplay.setVisibility(View.GONE);
        }
    }

    Disposable subscribe;

    /**
     * 延迟3秒恢复重力感应
     */
    private void temporarilyDisable() {
        if (subscribe != null && !subscribe.isDisposed()) {
            subscribe.dispose();
        }
        disableOrientation();
        isHaveOrientation = false;

        subscribe = Flowable.timer(3000, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (mPlayerView.isPlaying()) {
                            enableOrientation();
                            isHaveOrientation = true;
                        }
                    }
                });
    }

    /**
     * 关闭重力感应
     */
    private void disableOrientation() {
        if (null != mMyOrientationDetector) {
            mMyOrientationDetector.disable();
        }
    }

    /**
     * 开启重力感应
     */
    private void enableOrientation() {
        if (null != mMyOrientationDetector) {
            mMyOrientationDetector.enable();
        }
    }

    /**
     * 控制查看更多布局的显示隐藏
     *
     * @param isShow
     */
    private void seeMoreShowHiddenWithAnimation(boolean isShow) {
        if (isShow) {
            if (mPlayerView.isShowList())   //非竖屏不显示电视剧集
                return;
            mRlSeeMoreSelectLayout.startAnimation(mShowAnimation);
            mRlSeeMoreSelectLayout.setVisibility(View.VISIBLE);
            mRlSeeMoreSelectLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        } else {
            if (mRlSeeMoreSelectLayout.isShown()) {
                mHideAnimation.setDuration(500);
                mRlSeeMoreSelectLayout.startAnimation(mHideAnimation);
                mRlSeeMoreSelectLayout.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 注册接收器
     */
    private void registerNetworkReceiver() {
        networkReceiver = new NetworkConnectChangedReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);
    }

    private void unregisterNetworkReceiver() {
        try {
            if (null != networkReceiver)
                unregisterReceiver(networkReceiver);
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    private String space = "           ";

    /**
     * 请求分类列表
     */
    private void requestChannelClassify() {
        addDisposable(TVServiceImpl.getTypeList()
                .subscribeWith(new DisposableObserver<BaseListModel<TvType>>() {
                    @Override
                    public void onNext(BaseListModel<TvType> baseModel) {
                        final ArrayList<TvType> tvTypes = (ArrayList<TvType>) baseModel.data;
                        TvDataTransform.getmTvTypes().clear();
                        String customerSourceTitle = "";
                        try {
                            customerSourceTitle = CustomerSourceUtils.getCheckModel().getDefineName();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (TextUtils.isEmpty(customerSourceTitle)) {
                            customerSourceTitle = "自定义频道";
                            if (CustomerSourceUtils.getCheckModel() != null) {
                                CustomerSourceUtils.getCheckModel().setDefineName(customerSourceTitle);
                            }
                        }

                        if (tvTypes != null && tvTypes.size() > 0 && (CustomerSourceUtils.getCheckModel() != null && CustomerSourceUtils.getCheckModel().isBindling())) {
                            TvDataTransform.getmTvTypes().add(new TvType(-2, customerSourceTitle));
                            TvDataTransform.getmTvTypes().addAll(tvTypes);
                        } else {
                            TvDataTransform.getmTvTypes().add(new TvType(-2, customerSourceTitle));
                            TvDataTransform.getmTvTypes().add(new TvType(-1, "电视直播"));
                        }

                        mClassifyHorizontalNavigationBar.setChannelSplit(true);//需要设置在数据之前
                        mClassifyHorizontalNavigationBar.setItems(TvDataTransform.getmTvTypes());
                        mClassifyHorizontalNavigationBar.addOnHorizontalNavigationSelectListener(new HorizontalNavigationBar.
                                OnHorizontalNavigationSelectListener() {
                            @Override
                            public void select(int index) {
                                RxBus.getInstance().send(new TvEvent.ChannelChangedEvent(index));
                            }
                        });

                        if (CustomerSourceUtils.getCheckModel() != null && CustomerSourceUtils.getCheckModel().isBindling()) {
                            TvDataTransform.autoPaly = true;
                            RxBus.getInstance().send(new TvEvent.ChannelChangedEvent(1));
                            mClassifyHorizontalNavigationBar.setCurrentChannelItem(1);

                        } else {
                            if (CustomerSourceUtils.getTvChannels(mContext).size() > 0) {
                                TvChannel tvChannel = CustomerSourceUtils.getTvChannels(mContext).get(0);
                                String key = tvChannel.c + tvChannel.s + "";
                                CustomerSourceUtils.setKey(key);
                            }

                            RxBus.getInstance().send(new TvEvent.ChannelChangedEvent(0));
                            mClassifyHorizontalNavigationBar.setCurrentChannelItem(0);
                        }
                        empty.setVisibility(View.GONE);
                        if (CustomerSourceUtils.getCheckModel() != null && CustomerSourceUtils.getTvChannels(mContext).size() > 0
                                && (!CustomerSourceUtils.getCheckModel().isBindling())) {
                            TvDataTransform.setClassifyPosition(0);
                            TvDataTransform.setKindPosition(0);
                            TvDataTransform.setChannelPosition(0);

                            Flowable.timer(300, TimeUnit.MILLISECONDS)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<Long>() {
                                        @Override
                                        public void accept(Long aLong) throws Exception {
                                            try {
                                                RxBus.getInstance().send(new TvEvent.PlayEvent(CustomerSourceUtils.getTvChannels(mContext).get(0)));
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                        }

                        if (tvChannels != null && tvChannels.size() > 0) {
                            Flowable.timer(1000, TimeUnit.MILLISECONDS)
                                    .subscribe(new Consumer<Long>() {
                                        @Override
                                        public void accept(Long aLong) throws Exception {
                                            RxBus.getInstance().send(new TvEvent.SearchTvEvent(tvChannels.get(0)));
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        showToast(getResources().getString(R.string.failed_request_classify));

                        empty.setVisibility(View.VISIBLE);
                        empty.changeStatus(EmptyView.LOADING_ERR, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestChannelClassify();
                            }
                        });
                    }

                    @Override
                    public void onComplete() {
                    }
                }));
    }

    /**
     * 判断是否为(电视剧  点播)或者自定义源  是的话需要单曲请求源
     *
     * @param tvChannel 判断的节目
     * @return true 是  false 不是
     */
    private boolean isSitcomOnDemand(TvChannel tvChannel) {
        return (tvChannel.type == 4 && tvChannel.isMovie) || tvChannel.type == -2;
    }

    /**
     * 请求源 为后续播放作准备
     *
     * @param tvSourceDetail
     */
    private void requestPlaySource(final TvSourceDetail tvSourceDetail) {

        clearDisposable();
        onVideoPause();

        startPlayLoading();
        if (mPlayerView != null) {
            mPlayerView.disMissSelectSourceView();
        }

        TvChannel currentTvChannel = getCurrentChannel();

        assert currentTvChannel != null;

        if (tvSourceDetail != null) {//正常节目
            if (!TextUtils.isEmpty(tvSourceDetail.source)) {
                mCurSource = tvSourceDetail.source;
                preparePlay();
                return;
            }
        } else {//电视剧剧集  或者点播没有任何源

            mCurSource = currentTvChannel.s;
            if (TextUtils.isEmpty(mCurSource)) {
                mPlayerView.setLoadingPlayUI(true);
                return;
            }
        }
        if (isSitcomOnDemand(currentTvChannel)) {//电视剧剧集单独处理
            preparePlay();
        } else {
            if (tvSourceDetail != null) {
                if (currentTvChannel.isMovie) {
                    addDisposable(TVServiceImpl
                            .getDemandById(tvSourceDetail.i)
                            .subscribeWith(new DisposableObserver<String>() {
                                @Override
                                public void onComplete() {
                                }

                                @Override
                                public void onNext(@NonNull String baseModel3) {
                                    tvSourceDetail.source = baseModel3;
                                    mCurSource = baseModel3;
                                    playVideoWithDialog();
                                }

                                @Override
                                public void onError(@NonNull Throwable e) {
                                    clearDisposable();
                                    mCurSource = "";
                                    switchChannelOrSource();
                                }
                            }));
                } else {
                    addDisposable(TVServiceImpl
                            .getSourceById(tvSourceDetail.i)
                            .subscribeWith(new DisposableObserver<String>() {
                                @Override
                                public void onComplete() {
                                }

                                @Override
                                public void onNext(@NonNull String baseModel3) {
                                    tvSourceDetail.source = baseModel3;
                                    mCurSource = baseModel3;
                                    playVideoWithDialog();
                                }

                                @Override
                                public void onError(@NonNull Throwable e) {
                                    clearDisposable();
                                    mCurSource = "";
                                    switchChannelOrSource();
                                }
                            }));
                }
            }
        }
    }

    /**
     * 准备播放
     */
    private void preparePlay() {
        if (mCurSource != null) {
            if (mCurSource.startsWith("sourceUri://")) {
                requestPlaySource();
            } else {
                playVideoWithDialog();
            }
        } else {
            LogUtils.e("mCurSource is NULL");
        }
    }

    /**
     * 请求播放源数据
     */
    private void requestPlaySource() {
        clearDisposable();  //清除之前的请求
        addDisposable(TVServiceImpl
                .getRobotTvSourceUri(mCurSource)
                .subscribeWith(new DisposableObserver<BaseObjModel<TvSource>>() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onNext(@NonNull BaseObjModel<TvSource> baseModel3) {
                        mCurSource = baseModel3.data.source;
                        playVideoWithDialog();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        clearDisposable();
                        mCurSource = "";
                        switchChannelOrSource();
                    }
                }));
    }

    /**
     * wifi环境判断是否播放
     */
    private void playVideoWithDialog() {
        LogUtils.i("isWifiDialogShow:" + TvDataTransform.getIsShowWifiDialog());
        boolean isWifiConnect = NetworkUtils.isWifiConnected(mContext);
        if (isWifiConnect) {    //wifi已连接
            playVideo();
        } else {        //wifi未连接
            int netType = NetworkUtils.getNetWorkType(mContext);
            if (netType == 2 || netType == 3 || netType == 4) { //移动网络
                if (TvDataTransform.getIsShowWifiDialog()) {
                    if (null == networkDialog || !networkDialog.isShowing()) {
                        networkDialog = new NetworkWarnDialog(mContext, new NetworkWarnDialog.OnSelectListener() {
                            @Override
                            public void selectType(boolean confirm) {
                                if (confirm) {  //确定使用移动网络观看视频
                                    showToast(getResources().getString(R.string.play_is_use_mobile));
                                    playVideo();
                                    TvDataTransform.putIsShowWifiDialog(false);
                                } else {
                                    onFinish();
                                    finish();
                                }
                            }
                        });
                        if (!isPause) {
                            networkDialog.show();
                            if (isAlreadyPlaying) {
                                onVideoPause();
                            }
                        }
                    }
                } else {
                    playVideo();
                }
            }
        }
    }

    /**
     * 视频恢复播放
     */
    private void onVideoResume() {
        LogUtils.i("isWifiDialogShow:" + TvDataTransform.getIsShowWifiDialog());
        boolean isWifiConnect = NetworkUtils.isWifiConnected(mContext);
        if (isWifiConnect) {    //wifi已连接
            if (null != mPlayerView) {
                mPlayerView._togglePlayStatus();
            }
        } else {        //wifi未连接
            int netType = NetworkUtils.getNetWorkType(mContext);
            if (netType == 2 || netType == 3 || netType == 4) { //移动网络
                if (TvDataTransform.getIsShowWifiDialog()) {
                    if (null == networkDialog || !networkDialog.isShowing()) {
                        networkDialog = new NetworkWarnDialog(mContext, new NetworkWarnDialog.OnSelectListener() {
                            @Override
                            public void selectType(boolean confirm) {
                                if (confirm) {  //确定使用移动网络观看视频
                                    showToast(getResources().getString(R.string.play_is_use_mobile));
                                    if (null != mPlayerView) {
                                        mPlayerView._togglePlayStatus();
                                    }
                                    TvDataTransform.putIsShowWifiDialog(false);
                                } else {
                                    onFinish();
                                    finish();
                                }
                            }
                        });
                        if (!isPause) {
                            networkDialog.show();
                            if (isAlreadyPlaying) {
                                onVideoPause();
                            }
                        }
                    }
                } else {
                    if (null != mPlayerView) {
                        mPlayerView._togglePlayStatus();
                    }
                }
            }
        }
    }

    private void onVideoPause() {
        if (null != mPlayerView) {
            mPlayerView.onPause();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mPlayerView.configurationChanged(newConfig);
    }

    /**
     * 播放视频
     */
    private void playVideo() {

        try {

            TvChannel currentTvChannel = getCurrentChannel();

            if (currentTvChannel != null) {
                RxBus.getInstance().send(new TvEvent.ChangeProgromme(currentTvChannel));
                LogUtils.e(currentTvChannel.toString());

                if (currentTvChannel.type == -2) {
                    String key = currentTvChannel.c + currentTvChannel.s + "";
                    CustomerSourceUtils.setKey(key);
                } else {
                    CustomerSourceUtils.setKey("");
                }
            }


            if (!isHaveOrientation) {
                isHaveOrientation = true;
            }

            if (TextUtils.isEmpty(mCurSource)) {
                TvSourceDetail currentTvSources = TvDataTransform.getCurrentTvSources();
                if (null != currentTvSources) {
                    requestPlaySource(TvDataTransform.getCurrentTvSources());
                }
                return;
            }
            HashMap<String, String> map = new HashMap<>();//header参数
            int playerType = 0;//默认使用七牛，1使用EXO，2使用VLC，3使用vitamio
            //解析path，是否需要添加参数
            String[] paths = mCurSource.split("#");
            String realPath = paths[0];
            if (paths.length == 2) {
                String[] paths2 = paths[1].split("\\$");
                if (paths2.length == 2) {
                    try {
                        playerType = Integer.parseInt(paths2[1]);
                    } catch (Exception e) {
                        LogUtils.e(e);
                    }
                }
                try {
                    JSONObject object = new JSONObject(paths2[0]);
                    Iterator<String> iterator = object.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        String value = object.optString(key);
                        map.put(key, value);
                    }

                } catch (Exception e) {
                    LogUtils.e(e);
                }
            } else {
                //以$分割
                String[] paths2 = paths[0].split("\\$");
                realPath = paths2[0];
                if (paths2.length == 2) {
                    try {
                        playerType = Integer.parseInt(paths2[1]);
                    } catch (Exception e) {
                        LogUtils.e(e);
                    }
                }
            }

            int type;
            if (playerType == 1) {
                type = 0;
            } else {
                type = 2;
            }
            disableOrientation();
            mPlayerView.setIsMovie(currentTvChannel.isMovie);
            if (isInit) {
                isInit = false;
                mPlayerView
                        .init()
                        .setTitle("")
                        .setPlayerType(type)
                        .setPlayerControlUI()
                        .setVideoSource(map, null, realPath, realPath, null, null);
                mPlayerView._togglePlayStatus();
            } else {
                startPlayLoading();
                mPlayerView
                        .setPlayerType(type)
                        .switchVideoPath(realPath)
                        .start();
            }
            mPlayerView.setNoSource(false);
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    @Override
    protected void bindKeyBoardShowEvent() {
    }

    //语音获取节目并定位
    @Override
    protected void requestVoiceBarNet() {
        DialogUtils.showProgressDialog(mContext);
        addDisposable(RobotServiceImpl
                .getRobotTvLiveResponse(searchContent, false)
                .subscribeWith(new DisposableObserver<BaseData<BaseContent>>() {
                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onNext(@NonNull BaseData<BaseContent> baseData) {
                        DialogUtils.closeProgressDialog();
                        if (popupWindow != null) {
                            popupWindow.dismiss();
                        }
                        if (baseData.content == null || null == baseData.content.channelInfoList
                                || baseData.content.channelInfoList.size() == 0) {
                            showToast(mContext.getResources().getString(R.string.no_channel_info));
                            return;
                        }
                        List<TvChannel> tvList = baseData.content.channelInfoList;
                        if (tvList.size() == 1) {//搜索结果只有一个的话直接执行定位操作
                            RxBus.getInstance().send(new TvEvent.SearchTvEvent(tvList.get(0)));
                        } else {
                            if (!mPlayerView.isFullscreen()) {
                                setTvSearchDialog(tvList);
                                disableOrientation();
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        showToast(mContext.getResources().getString(R.string.no_channel_info));
                        DialogUtils.closeProgressDialog();
                    }
                }));
    }


    /**
     * 设置搜索出来的频道列表信息弹框
     *
     * @param tvList
     */
    private void setTvSearchDialog(final List<TvChannel> tvList) {

        try {
            if (popupWindow != null) {
                popupWindow.dismiss();
            }
            View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_tv_search_list, null);
            int screenHeight = MeasureUtil.getScreenHeight(mContext);
            popupWindow = new PopupWindow(RelativeLayout.LayoutParams.MATCH_PARENT, screenHeight);
            popupWindow.setContentView(view);
            popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setOutsideTouchable(true);
            popupWindow.setClippingEnabled(false);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    enableOrientation();
                }
            });

            RecyclerView mRvSearch = (RecyclerView) view.findViewById(R.id.rv_tv_search_list);
            CustomerLayoutManager layoutManager = new CustomerLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
            mRvSearch.setLayoutManager(layoutManager);
            TvSearchAdapter mAdapter = new TvSearchAdapter(mContext, tvList);
            mRvSearch.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(new TvSearchAdapter.OnItemClickListener() {
                @Override
                public void OnItemClick(View view, TvSearchAdapter.StateHolder holder, int position) {
                    RxBus.getInstance().send(new TvEvent.SearchTvEvent(tvList.get(position)));
                    popupWindow.dismiss();
                }
            });
            RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.rl_tv_search);
            rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupWindow.dismiss();
                }
            });

            int bottom;
            if (MeasureUtil.isNavigationBarShow(mContext)) {
                bottom = mContext.getResources().getDimensionPixelOffset(R.dimen.size_0) +
                        MeasureUtil.getNavigationBarHeight(mContext);
            } else {
                bottom = mContext.getResources().getDimensionPixelOffset(R.dimen.size_0);
            }

            popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM, 0, bottom);
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        AppConfig.mCurrentCageType = AppConfig.TYPE_TV;

        floatView = FloatViewInstance.attach(findViewById(android.R.id.content), this, new FloatView.OnMessageResult() {
            @Override
            public void onMessageResult(String s) {
                searchContent = s;
                requestVoiceBarNet();
            }
        });

        registerNetworkReceiver();

        if (isPause) {
            isPause = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isFinish) {
            FloatViewInstance.setOnMessageResult(null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableOrientation();
        unregisterNetworkReceiver();
        TvDataTransform.putIsShowWifiDialog(TvDataTransform.getIsShowWifiDialog());
        isPause = true;
        if (null != mPlayerView) {
            onVideoPause();
        }
        mVoiceBar.setOnSpeechDialogListener(null);
    }

    boolean isFinish;

    private void onFinish() {
        isFinish = true;
        disableOrientation();
        FloatViewInstance.detach(findViewById(android.R.id.content));
        cancelPlayLoading();
        onVideoDestroy();
        unregisterRxBus();
        clearDisposable();
        if (null != mMyOrientationDetector) {
            mMyOrientationDetector.canDetectOrientation();
            mMyOrientationDetector = null;
        }
        TvDataTransform.putIsShowWifiDialog(true);

        AppConfig.mCurrentCageType = AppConfig.CAGE_NONE;
    }

    private void onVideoDestroy() {
        try {

            if (null != mPlayerView) {
                mPlayerView.onDestroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mPlayerView.isFullscreen()) {
                if (mPlayerView.autoQuitFullScreen()) {
                    temporarilyDisable();
                }
                return true;
            } else {
                if (mPlayerView.canFinish()) {
                    if (popupWindow != null && popupWindow.isShowing()) {//如果有搜索弹框的话先隐藏
                        popupWindow.dismiss();
                        return true;
                    }
                    onFinish();
                } else {
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        //结束上一次刷新
        if (programeSubscribe != null && !programeSubscribe.isDisposed()) {
            programeSubscribe.dispose();
        }
        super.onDestroy();
    }

    /**
     * 定位正在播放的视频位置
     */
    private void posPlayingVideo() {
        try {
            LogUtils.i("isChangeChannel:" + isChangeChannel);
            if (isChangeChannel) {  //已经切换过频道

                isChangeChannel = false;
                final TvChannel tvChannel = getCurrentChannel();
                if (null != tvChannel) {
                    int firstPos = TvDataTransform.getClassifyPosition();
                    final int secondPos = TvDataTransform.getKindPosition();
                    final int thirdPos = TvDataTransform.getClassifyPositionSecond();
                    final int fourthPos = TvDataTransform.getChannelPosition();
                    mClassifyHorizontalNavigationBar.setCurrentChannelItem(firstPos);
                    mClassifyHorizontalNavigationBar.scrollChannelItem(firstPos);

                    CommonChannel commonChannel = TvDataTransform.getmAllChannels().get(firstPos);
                    refreshUIData(commonChannel);
                    List<TvChannel> tvChannels = commonChannel.getTvLives();
                    List<TvChannel> tvDemands = commonChannel.getOnDemands();
                    int type = tvChannel.type;
                    switch (type) {
                        case 0:
                        case 1:
                            mHideAnimation.setDuration(1);
                            mRlSeeMoreSelectLayout.setVisibility(View.GONE);
                            setPortListData(tvChannels);
                            break;
                        case 2:
                        case 3:
                            mHideAnimation.setDuration(1);
                            mRlSeeMoreSelectLayout.setVisibility(View.GONE);
                        case 4:
                            if (secondPos == 0) {
                                setPortListData(tvChannels);
                            } else {
                                setPortListData(tvDemands);
                            }
                            break;
                    }
                    mAdapter.notifyDataSetChanged();
                    switch (tvChannel.type) {
                        case 0:     //央视&卫视&本地
                            TvDataTransform.moveToTop(mRvSelect, fourthPos);
                            break;
                        case 1: //地方台
                            TvDataTransform.moveToTop(mRvSelect, thirdPos);
                            break;
                        case 2: //电影
                            if (secondPos == 0) {   //直播
                                TvDataTransform.moveToTop(mRvSelect, fourthPos);
                            } else {    //点播
                                TvDataTransform.moveToTop(mRvSelect, thirdPos);
                            }
                            break;
                        case 3: //少儿
                            TvDataTransform.moveToTop(mRvSelect, fourthPos);
                            break;
                        case 4: //电视剧
                            if (secondPos == 0) {   //直播
                                TvDataTransform.moveToTop(mRvSelect, fourthPos);
                            } else {    //点播
                                TvDataTransform.moveToTop(mRvSelect, thirdPos);
                            }
                            break;
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        if (EventUtils.isFastDoubleClick(view.getId()))
            return;

        int id = view.getId();
        if (id == R.id.tv_tv_play_select_empty_reload) {    //重新刷新

        } else if (id == R.id.iv_see_more_layout_close) {   //关闭查看更多
            mRvSelect.setVisibility(View.VISIBLE);
            seeMoreShowHiddenWithAnimation(false);
        } else if (id == R.id.tv_tv_play_movie_classify1) {     //直播
            try {
                mTvMovieClassify1.setTextColor(getResources().getColor(R.color.theme_color));
                mTvMovieClassify2.setTextColor(getResources().getColor(R.color.content_color));
                seeMoreShowHiddenWithAnimation(false);
                TvDataTransform.setKindPositionTemp(0);
                int classifyType = TvDataTransform.getClassifyPositionTemp();
                CommonChannel commonChannel = TvDataTransform.getmAllChannels().get(classifyType);
                mRvSelect.setLayoutManager(new MyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                List<TvChannel> tvLives = commonChannel.getTvLives();
                TvDataTransform.getCurrentTvChannels().clear();
                TvDataTransform.getCurrentTvChannels().addAll(tvLives);
                mTvChannels.clear();
                mTvChannels.addAll(tvLives);
                mAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                LogUtils.e(e);
            }

        } else if (id == R.id.tv_tv_play_movie_classify2) {     //点播
            try {
                mTvMovieClassify1.setTextColor(getResources().getColor(R.color.content_color));
                mTvMovieClassify2.setTextColor(getResources().getColor(R.color.theme_color));
                seeMoreShowHiddenWithAnimation(false);
                TvDataTransform.setKindPositionTemp(1);

                int classifyType = TvDataTransform.getClassifyPositionTemp();
                CommonChannel commonChannel = TvDataTransform.getmAllChannels().get(classifyType);
                if (3 == commonChannel.getType()) {
                    if (TvDataTransform.getKindPositionTemp() == 1) {
                        mRvSelect.setLayoutManager(new MyGridLayoutManager(this, 2));
                    } else {
                        mRvSelect.setLayoutManager(new MyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                    }
                } else {
                    mRvSelect.setLayoutManager(new MyLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                }
                List<TvChannel> tvDemands = commonChannel.getOnDemands();
                for (TvChannel tvChannel : tvDemands) {
                    tvChannel.isMovie = true;
                }
                TvDataTransform.getCurrentTvChannels().clear();
                TvDataTransform.getCurrentTvChannels().addAll(tvDemands);
                mTvChannels.clear();
                mTvChannels.addAll(tvDemands);
                mAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                LogUtils.e(e);
            }
        } else if (id == R.id.container_source_setting) {
            startActivity(new Intent(this, SourceSettingActivity.class));
        } else if (id == R.id.add_source) { //添加源
            startActivity(new Intent(this, AddSourceHelpActivity.class));
        } else if (id == R.id.txt_open_function) {  //扫码二维码
            startActivity(new Intent(mContext, OpenFunctionHelpActivity.class));
        }
    }

    private long lastTime = 0;
    //连续两次朝向相同，则改变朝向
    private int lastOrientation = 0;
    private int times = 0;

    /**
     * 屏幕旋转监听
     */
    class MyOrientationDetector extends OrientationEventListener {

        MyOrientationDetector(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                return;  //手机平放时，检测不到有效的角度
            }
            if ((orientation > 65 && orientation < 115) && (lastOrientation > 65 && lastOrientation < 115)) {
                if (!mPlayerView.isFullscreen()) {
                    if (System.currentTimeMillis() - lastTime < 800) {
                        return;
                    }
                    times++;
//                    if (times < 3) {
//                        return;
//                    }
                    times = 0;
                    lastTime = System.currentTimeMillis();
                    LogUtils.e("orientation = 横屏向上");
                    mPlayerView.autoEnterFullScreen();
                }
            } else if ((orientation > 255 && orientation < 295) && (lastOrientation > 255 && lastOrientation < 295)) {
                if (!mPlayerView.isFullscreen()) {
                    if (System.currentTimeMillis() - lastTime < 800) {
                        return;
                    }
                    times = 0;
                    lastTime = System.currentTimeMillis();
                    LogUtils.e("orientation = 横屏向下");
                    mPlayerView.autoEnterFullScreen();
                }
            } else if (((orientation > 325 && orientation < 360) && (lastOrientation > 325 && lastOrientation < 360)) ||
                    ((orientation > 0 && orientation < 35) && (lastOrientation > 0 && lastOrientation < 35))) {
                if (mPlayerView.isFullscreen()) {
                    times = 0;
                    if (System.currentTimeMillis() - lastTime < 800) {
                        return;
                    }
                    lastTime = System.currentTimeMillis();
                    LogUtils.e("orientation = 竖屏");
                    mPlayerView.autoQuitFullScreen();
                    //定位到正在播放的视频位置
                    posPlayingVideo();
                }
            }

            lastOrientation = orientation;
        }
    }

    /**
     * 网络状态监听
     */
    public class NetworkConnectChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                boolean mIsNetConnected = NetWorkUtils.isNetworkAvailable(mContext);
                mPlayerView.setNetConnectedEvent(mIsNetConnected);
            }

            if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
                int netType = NetworkUtils.getNetWorkType(mContext);
                if (netType == 2 || netType == 3 || netType == 4) {     //移动数据连接上时
                    if (TextUtils.isEmpty(mCurSource))
                        return;
                    if (null == networkDialog || !networkDialog.isShowing()) {
                        if (TvDataTransform.getIsShowWifiDialog()) {
                            networkDialog = new NetworkWarnDialog(mContext, new NetworkWarnDialog.OnSelectListener() {
                                @Override
                                public void selectType(boolean confirm) {
                                    if (confirm) {  //确定使用移动网络观看视频
                                        showToast(getResources().getString(R.string.play_is_use_mobile));
                                        resumeVideo();
                                        TvDataTransform.putIsShowWifiDialog(false);
                                    } else {
                                        onFinish();
                                        finish();
                                    }
                                }
                            });
                            if (!isPause) {
                                if (isAlreadyPlaying) {
                                    onVideoPause();
                                }
                                networkDialog.show();
                            }
                        } else {
                            resumeVideo();
                            TvDataTransform.putIsShowWifiDialog(false);
                        }
                    }
                }
            }

            if (ConnectivityManager.TYPE_WIFI == NetworkUtils.getNetWorkType(mContext)) {   //wifi连接上时
                if (null != networkDialog && networkDialog.isShowing()) {
                    networkDialog.cancel();
                    resumeVideo();
                } else {
                    resumeVideo();
                }
            }
        }
    }

    private void resumeVideo() {
        if (isAlreadyPlaying) {
            if (!TextUtils.isEmpty(mCurSource)) {
                mPlayerView.onResume();
            }
        } else {
            playVideo();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //竖屏&&触摸屏幕 时判断切换源列表是否显示  显示的话隐藏掉
        int height = mPlayerView.getHeight();
        if (ev.getY() > height && mPlayerView != null) {
            mPlayerView.disMissSelectSourceView();
        }
        return super.dispatchTouchEvent(ev);
    }


}