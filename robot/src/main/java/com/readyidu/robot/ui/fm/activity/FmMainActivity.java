package com.readyidu.robot.ui.fm.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.readyidu.baiduvoice.laster.FloatView;
import com.readyidu.baiduvoice.laster.FloatViewInstance;
import com.readyidu.basic.utils.DialogUtils;
import com.readyidu.robot.AppConfig;
import com.readyidu.robot.R;
import com.readyidu.robot.api.NetSubscriber;
import com.readyidu.robot.api.impl.RobotServiceImpl;
import com.readyidu.robot.base.BaseVoiceBarActivity;
import com.readyidu.robot.component.voice.VoiceBar;
import com.readyidu.robot.model.BaseModel;
import com.readyidu.robot.ui.fm.util.AlbumTrackModel;
import com.readyidu.robot.ui.fm.util.FmPlayUtil;
import com.readyidu.robot.ui.fm.util.RadioScheduleModel;
import com.readyidu.robot.ui.fm.view.CycleImageView;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.category.Category;
import com.ximalaya.ting.android.opensdk.model.category.CategoryList;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yuzhang on 2018/5/22.
 */

public class FmMainActivity extends BaseVoiceBarActivity {

    ImageButton fmMainBack;
    ImageButton fmMainHeadHistory;
    ImageButton fmMainAll;
    EditText fmMainHeadEdt;
    RelativeLayout fmMainTitle;
    TabLayout fmMainTabs;
    ViewPager fmMainTitleViewpager;
    VoiceBar voiceBar;

    List<Category> categories;
    CycleImageView fmMainBottomPlayCover;
    RelativeLayout fmMainBottomPlay;

    private XmPlayerManager mPlayerManager;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void bindViews() {
        super.bindViews();

        fmMainBottomPlayCover = (CycleImageView) findViewById(R.id.fm_main_bottom_play_cover);
        fmMainBottomPlay = (RelativeLayout) findViewById(R.id.fm_main_bottom_play);
        fmMainBottomPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FmPlayUtil.getCurAlbumTrackModel() == null) {
                    RadioScheduleModel model = FmPlayUtil.getCurRadioScheduleModel();
                    Intent playIntent = new Intent(FmMainActivity.this, FmRadioPlayActivity.class);
//                    playIntent.putParcelableArrayListExtra("schedules", (ArrayList<? extends Parcelable>) model.getSchedules());
                    playIntent.putExtra("radio", model.getRadio());
                    playIntent.putExtra("scheduleIndex", model.getScheduleIndex());
                    FmMainActivity.this.startActivity(playIntent);
                } else {
                    int steps = FmPlayUtil.getCurAlbumTrackModel().getSteps();
                    if(FmPlayUtil.getCurAlbumTrackModel().getCurTrackList().size() <= steps){
                        steps = steps % 50;
                    }
                    PlayableModel model = FmPlayUtil.getCurAlbumTrackModel().getCurTrackList().get(steps);
                    if (model instanceof Track) {
                        Track info = (Track) model;
                        Intent playIntent = new Intent(FmMainActivity.this, FmPlayActivity.class);
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

                        FmMainActivity.this.startActivity(playIntent);
                    }
                }
            }
        });
        fmMainBack = (ImageButton) findViewById(R.id.fm_main_back);
        fmMainBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        fmMainHeadHistory = (ImageButton) findViewById(R.id.fm_main_head_history);
        fmMainHeadHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FmMainActivity.this, FmHistoryActivity.class));
            }
        });
        fmMainAll = (ImageButton) findViewById(R.id.fm_main_all);
        fmMainAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(FmMainActivity.this, FmMoreActivity.class), 88);
            }
        });
        fmMainHeadEdt = (EditText) findViewById(R.id.fm_main_head_edt);
        fmMainHeadEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FmMainActivity.this, FmAlbumSearchActivity.class));
            }
        });
        fmMainTitle = (RelativeLayout) findViewById(R.id.fm_main_title);
        fmMainTabs = (TabLayout) findViewById(R.id.fm_main_tabs);
        fmMainTitleViewpager = (ViewPager) findViewById(R.id.fm_main_title_viewpager);
        voiceBar = (VoiceBar) findViewById(R.id.voice_bar);


        Map<String, String> map = new HashMap<String, String>();
        CommonRequest.getCategories(map, new IDataCallBack<CategoryList>() {
            @Override
            public void onSuccess(@Nullable CategoryList categoryList) {
                categories = categoryList.getCategories();
                Category broadCategory = new Category();
                broadCategory.setCategoryName("广播");
                broadCategory.setId(-2);
                categories.add(0, broadCategory);

                Category recommendCategory = new Category();
                recommendCategory.setCategoryName("推荐");
                recommendCategory.setId(-1);
                categories.add(1, recommendCategory);

                initViewPager();

                reflexTab(fmMainTabs);
            }

            @Override
            public void onError(int i, String s) {
            }
        });

        String xiaoyiSearchType = getIntent().getStringExtra("xiaoyiSearchType");
        String xiaoyiSearchKey = getIntent().getStringExtra("xiaoyiSearchKey");

        if("history".equals(xiaoyiSearchType)){
            startActivity(new Intent(FmMainActivity.this, FmHistoryActivity.class));
        }else{
            if (xiaoyiSearchType != null && xiaoyiSearchType.length() > 0 && xiaoyiSearchKey != null && xiaoyiSearchKey.length() > 0) {
                Intent searchIntent = new Intent(FmMainActivity.this, FmAlbumSearchActivity.class);
                searchIntent.putExtra("searchkey", xiaoyiSearchKey);
                searchIntent.putExtra("searchType", xiaoyiSearchType);
                startActivity(searchIntent);
            }
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

        mPlayerManager = FmPlayUtil.getPlayerManager();
        mPlayerManager.addPlayerStatusListener(mPlayerStatusListener);
        mPlayerManager.addAdsStatusListener(mAdsListener);

        if ((FmPlayUtil.getCurAlbumTrackModel() == null || FmPlayUtil.getCurAlbumTrackModel().getCurTrackList() == null) && FmPlayUtil.getCurRadioScheduleModel() == null) {
            fmMainBottomPlay.setVisibility(View.GONE);
        } else if (FmPlayUtil.getCurAlbumTrackModel() != null) {
            fmMainBottomPlay.setVisibility(View.VISIBLE);
            int steps = FmPlayUtil.getCurAlbumTrackModel().getSteps();
            if(FmPlayUtil.getCurAlbumTrackModel().getCurTrackList().size() <= steps){
                steps = steps % 50;
            }
            PlayableModel model = FmPlayUtil.getCurAlbumTrackModel().getCurTrackList().get(steps);
            if (model instanceof Track) {
                Track info = (Track) model;
                Glide.with(FmMainActivity.this).load(info.getCoverUrlSmall()).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        fmMainBottomPlayCover.setImageBitmap(resource);
                    }
                });
                if (FmPlayUtil.getPlayerManager().isPlaying()) {
                    fmMainBottomPlayCover.setCycle(true);
                } else {
                    fmMainBottomPlayCover.setCycle(false);
                }
            }
        } else {
            fmMainBottomPlay.setVisibility(View.VISIBLE);
            PlayableModel model = FmPlayUtil.getCurRadioScheduleModel().getRadio();
            if (model instanceof Radio) {
                Radio info = (Radio) model;
                Glide.with(FmMainActivity.this).load(info.getCoverUrlSmall()).asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        fmMainBottomPlayCover.setImageBitmap(resource);
                    }
                });
                if (FmPlayUtil.getPlayerManager().isPlaying()) {
                    fmMainBottomPlayCover.setCycle(true);
                } else {
                    fmMainBottomPlayCover.setCycle(false);
                }
            }

        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 88 && data != null) {
            int selectIndex = data.getIntExtra("selectIndex", 0);
            TabLayout.Tab tab = fmMainTabs.getTabAt(selectIndex + 2);
            tab.select();
        }
    }

    protected void initViewPager() {

        fmMainTitleViewpager.setOffscreenPageLimit(10);

        //设置适配器
        fmMainTitleViewpager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public CharSequence getPageTitle(int position) {
                return categories.get(position).getCategoryName();
            }

            @Override
            public Fragment getItem(int position) {
                if (position == 0) {
                    FmBroadcastSubFragment subFragment = new FmBroadcastSubFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("name", categories.get(position).getCategoryName());
                    bundle.putLong("id", categories.get(position).getId());

                    subFragment.setArguments(bundle);
                    return subFragment;
                } else if (position == 1) {
                    FmRecommendSubFragment subFragment = new FmRecommendSubFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("name", categories.get(position).getCategoryName());
                    bundle.putLong("id", categories.get(position).getId());
                    subFragment.setArguments(bundle);
                    return subFragment;
                } else {
                    FmRecommendSubFragment subFragment = new FmRecommendSubFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("name", categories.get(position).getCategoryName());
                    bundle.putLong("id", categories.get(position).getId());
                    subFragment.setArguments(bundle);
                    return subFragment;
                }
            }

            @Override
            public int getCount() {
                return categories.size();
            }
        });

        fmMainTabs.setupWithViewPager(fmMainTitleViewpager);
        fmMainTitleViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
                            startActivity(new Intent(FmMainActivity.this, FmHistoryActivity.class));
                        }else{
                            if (baseModel.data.contentType != null && baseModel.data.contentType.length() > 0 && baseModel.data.keyword != null && baseModel.data.keyword.length() > 0) {
                                Intent searchIntent = new Intent(FmMainActivity.this, FmAlbumSearchActivity.class);
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
        return R.layout.fm_main_layout;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppConfig.mCurrentCageType = AppConfig.CAGE_NONE;

        FloatViewInstance.detach(findViewById(android.R.id.content));
    }

    @Override
    public void clickBack() {
        super.clickBack();
        AppConfig.mCurrentCageType = AppConfig.CAGE_NONE;

        FloatViewInstance.detach(findViewById(android.R.id.content));
    }


    public void reflexTab(final TabLayout tabLayout) {
        //了解源码得知 线的宽度是根据 tabView的宽度来设置的
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    //拿到tabLayout的mTabStrip属性
                    LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);

                    int dp10 = (int) getResources().getDimension(R.dimen.size_10);

                    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                        View tabView = mTabStrip.getChildAt(i);

                        //拿到tabView的mTextView属性  tab的字数不固定一定用反射取mTextView
                        Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                        mTextViewField.setAccessible(true);

                        TextView mTextView = (TextView) mTextViewField.get(tabView);

                        tabView.setPadding(0, 0, 0, 0);

                        //因为我想要的效果是   字多宽线就多宽，所以测量mTextView的宽度
                        int width = 0;
                        width = mTextView.getWidth();
                        if (width == 0) {
                            mTextView.measure(0, 0);
                            width = mTextView.getMeasuredWidth();
                        }

                        //设置tab左右间距为10dp  注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                        params.width = width;
                        params.leftMargin = dp10;
                        params.rightMargin = dp10;
                        tabView.setLayoutParams(params);

                        tabView.invalidate();
                    }

                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
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
                            fmMainBottomPlayCover.setImageBitmap(resource);
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

    @Override
    public void finish() {
        super.finish();
        mPlayerManager.pause();
        mPlayerManager.stop();
    }

}
