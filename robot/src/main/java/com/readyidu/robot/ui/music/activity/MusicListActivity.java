package com.readyidu.robot.ui.music.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.readyidu.basic.utils.AppManager;
import com.readyidu.basic.utils.ToastUtils;
import com.readyidu.robot.AppConfig;
import com.readyidu.robot.R;
import com.readyidu.robot.YDRobot;
import com.readyidu.robot.api.NetSubscriber;
import com.readyidu.robot.api.exception.ExceptionHandle;
import com.readyidu.robot.api.impl.RobotServiceImpl;
import com.readyidu.robot.base.BaseRefreshListActivity;
import com.readyidu.robot.component.music.MusicConstants;
import com.readyidu.robot.component.music.MusicRecord;
import com.readyidu.robot.event.BaseMessageReceiverEvent;
import com.readyidu.robot.event.CloseMusicListEvent;
import com.readyidu.robot.event.music.MusicEvent;
import com.readyidu.robot.model.BaseModel;
import com.readyidu.robot.model.business.music.Music;
import com.readyidu.robot.ui.activity.RobotActivity;
import com.readyidu.robot.ui.adapter.common.wrapper.HeaderAndFooterWrapper;
import com.readyidu.robot.ui.music.adapter.MusicAdapter;
import com.readyidu.robot.ui.widgets.MusicLineView;
import com.readyidu.robot.utils.data.DataTranUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * @Autour: wlq
 * @Description: 音乐列表
 * @Date: 2017/10/12 11:03
 * @Update: 2017/10/12 11:03
 * @UpdateRemark:
 * @Version: V1.0
 */
public class MusicListActivity extends BaseRefreshListActivity {

    private HeaderAndFooterWrapper mAdapter;
    private MusicLineView iv_status;
    private boolean isSDKOutside;   //是否从sdk外部直接进入
    private List<Music> mMusics;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_music_list;
    }

    @Override
    protected String getType() {
        return getResource(R.string.music);
    }

    @Override
    protected void bindViews() {
        super.bindViews();

        AppConfig.mCurrentCageType = AppConfig.TYPE_MUSIC;
        iv_status = (MusicLineView) findViewById(R.id.iv_status);
        mMusics = new ArrayList<>();

        Intent intent = getIntent();
        isSDKOutside = intent.getBooleanExtra("isSDKOutside", false);
    }

    @Override
    protected void bindEvents() {
        super.bindEvents();

        mAdapter = new HeaderAndFooterWrapper(new MusicAdapter(mContext, mMusics, isSDKOutside));
        setCustomLoadMoreView(mAdapter);
        recycler.setAdapter(mAdapter);

        final Intent intent = getIntent();
        if (intent != null) {
            boolean isSearchList = intent.getBooleanExtra("isSearchList", false);
            if (isSDKOutside) {
                if (!isSearchList) {    //从sdk外部跳进来，且没有用语音搜索音乐
                    searchContent = "我要听音乐";
                    ArrayList<Music> musics = intent.getParcelableArrayListExtra("data");
                    if (musics != null && musics.size() > 0) {
                        mTvMenuTip.setVisibility(View.VISIBLE);
                        MusicRecord.getInstance().getPrestrainMusic().clear();
                        MusicRecord.getInstance().getPrestrainMusic().addAll(musics);
                        replaceData(musics);

                        setStatus(MusicRecord.getInstance().getPrestrainMusic().size(), musics.size() + 1);

                        Music music = musics.get(0);
                        MusicRecord.getInstance().setmMusics();
                        MusicRecord.getInstance().setCurrentMusic(music);
                        MusicRecord.getInstance().setmPlayMode(MusicConstants.PlayMode.ORDER);
                    } else {
                        mSearchType = false;
                        setMenuTip("");
                        refresh();
                    }
                } else {    //从sdk外部跳进音乐播放界面，用语音搜索音乐后又回到此界面
                    String keyword = intent.getStringExtra("keyword");
                    if (!TextUtils.isEmpty(keyword)) {
                        searchContent = keyword;
                        MusicRecord.getInstance().setPrestraiKayWord(searchContent);
                        mSearchType = true;
                    }
                    setMenuTip(searchContent);

                    ArrayList<Music> musics = intent.getParcelableArrayListExtra("data");
                    int total = intent.getIntExtra("total", 0);
                    if (musics != null) {
                        mTvMenuTip.setVisibility(View.VISIBLE);
                        MusicRecord.getInstance().getPrestrainMusic().clear();
                        MusicRecord.getInstance().getPrestrainMusic().addAll(musics);

                        replaceData(musics);
                        setStatus(MusicRecord.getInstance().getPrestrainMusic().size(), total);
                        Flowable.timer(1000, TimeUnit.MICROSECONDS)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(Long aLong) throws Exception {
                                        int position = intent.getIntExtra("classifyPos", 0);
                                        recycler.scrollToPosition(position);
                                    }
                                });
                    } else {
                        refresh();
                    }
                }
            } else {
                String keyword = intent.getStringExtra("keyword");
                if (!TextUtils.isEmpty(keyword)) {
                    searchContent = keyword;
                    MusicRecord.getInstance().setPrestraiKayWord(searchContent);
                    mSearchType = true;
                }

                setMenuTip(searchContent);

                ArrayList<Music> musics = intent.getParcelableArrayListExtra("data");
                int total = intent.getIntExtra("total", 0);
                if (musics != null) {
                    mTvMenuTip.setVisibility(View.VISIBLE);
                    MusicRecord.getInstance().getPrestrainMusic().clear();
                    MusicRecord.getInstance().getPrestrainMusic().addAll(musics);

                    replaceData(musics);
                    setStatus(MusicRecord.getInstance().getPrestrainMusic().size(), total);
                    Flowable.timer(1000, TimeUnit.MICROSECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Long>() {
                                @Override
                                public void accept(Long aLong) throws Exception {
                                    int position = intent.getIntExtra("classifyPos", 0);
                                    recycler.scrollToPosition(position);
                                }
                            });
                } else {
                    refresh();
                }
            }
        } else {
            refresh();
        }

        registerRxBus(MusicEvent.MusicIsPlaying.class, new Consumer<MusicEvent.MusicIsPlaying>() {
            @Override
            public void accept(MusicEvent.MusicIsPlaying musicIsPlaying) throws Exception {
                if (musicIsPlaying.isPlaying) {
                    iv_status.setVisibility(View.VISIBLE);
                } else {
                    iv_status.setVisibility(View.GONE);
                }
            }
        });

        registerRxBus(CloseMusicListEvent.class, new Consumer<CloseMusicListEvent>() {
            @Override
            public void accept(CloseMusicListEvent closeMusicListEvent) throws Exception {
                finish();
            }
        });

        iv_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MusicListActivity.this, MusicPlayActivity.class);
                intent.putExtra("isSDKOutside", isSDKOutside);
                startActivity(intent);
            }
        });

        registerRxBus(MusicEvent.SwitchMusic.class, new Consumer<MusicEvent.SwitchMusic>() {
            @Override
            public void accept(MusicEvent.SwitchMusic switchMusic) throws Exception {
                mAdapter.notifyDataSetChanged();
            }
        });

        registerRxBus(BaseMessageReceiverEvent.class, new Consumer<BaseMessageReceiverEvent>() {
            @Override
            public void accept(BaseMessageReceiverEvent messageReceiverEvent) throws Exception {
                if (messageReceiverEvent != null && messageReceiverEvent.message != null) {
                    ArrayList<Music> musics = (ArrayList<Music>) DataTranUtils.tranMusic(messageReceiverEvent.message);
                    boolean isSearchList = messageReceiverEvent.isSearchList();

                    if (isSDKOutside) {
                        if (!isSearchList) {    //从sdk外部跳进来，且没有用语音搜索音乐
                            searchContent = "我要听音乐";
                            if (musics != null && musics.size() > 0) {
                                mTvMenuTip.setVisibility(View.VISIBLE);
                                MusicRecord.getInstance().getPrestrainMusic().clear();
                                MusicRecord.getInstance().getPrestrainMusic().addAll(musics);

                                replaceData(musics);
                                setStatus(MusicRecord.getInstance().getPrestrainMusic().size(), musics.size() + 1);

                                Music music = musics.get(0);
                                MusicRecord.getInstance().setmMusics();
                                MusicRecord.getInstance().setCurrentMusic(music);
                                MusicRecord.getInstance().setmPlayMode(MusicConstants.PlayMode.ORDER);
                            } else {
                                mSearchType = false;
                                setMenuTip("");
                                refresh();
                            }
                        } else {    //从sdk外部跳进音乐播放界面，用语音搜索音乐后又回到此界面
                            String keyword = messageReceiverEvent.message.data.keyword;
                            if (!TextUtils.isEmpty(keyword)) {
                                searchContent = keyword;
                                MusicRecord.getInstance().setPrestraiKayWord(searchContent);
                                mSearchType = true;
                            }
                            setMenuTip(searchContent);

                            int total = DataTranUtils.getTotal(messageReceiverEvent.message);
                            if (musics != null) {
                                mTvMenuTip.setVisibility(View.VISIBLE);
                                MusicRecord.getInstance().getPrestrainMusic().clear();
                                MusicRecord.getInstance().getPrestrainMusic().addAll(musics);

                                replaceData(musics);
                                setStatus(MusicRecord.getInstance().getPrestrainMusic().size(), total);
                                Flowable.timer(1000, TimeUnit.MICROSECONDS)
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Consumer<Long>() {
                                            @Override
                                            public void accept(Long aLong) throws Exception {
                                                int position = 0;
                                                recycler.scrollToPosition(position);
                                            }
                                        });
                            } else {
                                refresh();
                            }
                        }
                    } else {
                        String keyword = messageReceiverEvent.message.data.keyword;
                        if (!TextUtils.isEmpty(keyword)) {
                            searchContent = keyword;
                            MusicRecord.getInstance().setPrestraiKayWord(searchContent);
                            mSearchType = true;
                        }

                        setMenuTip(searchContent);

                        int total = DataTranUtils.getTotal(messageReceiverEvent.message);
                        if (musics != null) {
                            mTvMenuTip.setVisibility(View.VISIBLE);
                            MusicRecord.getInstance().getPrestrainMusic().clear();
                            MusicRecord.getInstance().getPrestrainMusic().addAll(musics);

                            replaceData(musics);
                            setStatus(MusicRecord.getInstance().getPrestrainMusic().size(), total);
                            Flowable.timer(1000, TimeUnit.MICROSECONDS)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<Long>() {
                                        @Override
                                        public void accept(Long aLong) throws Exception {
                                            int position = 0;
                                            recycler.scrollToPosition(position);
                                        }
                                    });
                        } else {
                            refresh();
                        }
                    }
                } else {
                    ToastUtils.showShortToast(mContext, "没有搜到数据");
                }
            }
        });
    }

    @Override
    protected void requestData() {
        String searchStr;
        if (TextUtils.isEmpty(searchContent)) {
            searchStr = "我要听音乐";
        } else {
            searchStr = searchContent;
        }
        addDisposable(RobotServiceImpl
                .getRobotMusicResponse(searchStr, false, pageNo, pageSize)
                .subscribeWith(new NetSubscriber<BaseModel>() {
                    @Override
                    protected void onSuccess(BaseModel baseModel) {
                        if (null != baseModel.data) {
                            List<Music> musics = DataTranUtils.tranMusic(baseModel);
                            if (musics != null && musics.size() > 0) {
                                setMenuTip(baseModel.data.keyword);
                                //FIXME "热门推荐"提示搜索条件问题
                                MusicRecord.getInstance().setPrestraiKayWord(baseModel.data.keyword);
                                if (pageNo == 1) {
                                    mMusics.clear();
                                    MusicRecord.getInstance().getPrestrainMusic().clear();
                                    recycler.smoothScrollToPosition(0);
                                }
                                mMusics.addAll(musics);
                                MusicRecord.getInstance().getPrestrainMusic().addAll(musics);

                                mAdapter.notifyDataSetChanged();
                            } else {
                                showToast("小益没有找到您要的音乐信息");
                            }

                            if (MusicRecord.getInstance().getPrestrainMusic().size() > 0) {
                                mTvMenuTip.setVisibility(View.VISIBLE);
                            } else {
                                mTvMenuTip.setVisibility(View.GONE);
                                setErrStatus(ExceptionHandle.ERROR.NO_DATA_ERROR, MusicRecord.getInstance().getPrestrainMusic().size() == 0);
                            }

                            setStatus(MusicRecord.getInstance().getPrestrainMusic().size(), DataTranUtils.getTotal(baseModel));
                        }
                    }

                    @Override
                    protected void onFailure(int errorCode, String errorMessage) {
                        onErr(errorCode, MusicRecord.getInstance().getPrestrainMusic().size() == 0, errorMessage);
                    }
                }));
    }


    @Override
    protected void onStop() {
        super.onStop();
        iv_status.stopAnim();
    }

    @Override
    protected void onResume() {
        super.onResume();

        iv_status.startAnim();
        mAdapter.notifyDataSetChanged();
        YDRobot.getInstance().getPlayingMusicInfo();
        int curPos = MusicRecord.getInstance().getCurrentPosition();
        if (curPos < 0 || curPos > mMusics.size() - 1)
            return;
        recycler.smoothScrollToPosition(curPos);
    }

    private void replaceData(ArrayList<Music> musics) {
        mMusics.clear();
        mMusics.addAll(musics);
        mAdapter.notifyDataSetChanged();
    }

    private void release() {
        if (isSDKOutside || AppManager.getInstance().getActivity(RobotActivity.class) == null) {
            MusicRecord.getInstance().release();
            MusicRecord.getInstance().getPrestrainMusic().clear();
            mMusics.clear();
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void clickBack() {
        release();
        super.clickBack();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            release();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
