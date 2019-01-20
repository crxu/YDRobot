package com.readyidu.robot.ui.weather.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.readyidu.baiduvoice.laster.FloatView;
import com.readyidu.baiduvoice.laster.FloatViewInstance;
import com.readyidu.basic.utils.ToastUtils;
import com.readyidu.robot.AppConfig;
import com.readyidu.robot.R;
import com.readyidu.robot.api.NetSubscriber;
import com.readyidu.robot.api.impl.RobotServiceImpl;
import com.readyidu.robot.base.BaseVoiceBarActivity;
import com.readyidu.robot.event.BaseMessageReceiverEvent;
import com.readyidu.robot.model.BaseModel;
import com.readyidu.robot.model.business.weather.HeWeatherInfo;
import com.readyidu.robot.model.business.weather.TabEntity;
import com.readyidu.robot.model.business.weather.Weather;
import com.readyidu.robot.ui.weather.adapter.ViewPagerAdapter;
import com.readyidu.robot.ui.weather.fragment.WeatherFragment;
import com.readyidu.robot.ui.widgets.slidingtab.CustomTabEntity;
import com.readyidu.robot.ui.widgets.slidingtab.OnTabSelectListener;
import com.readyidu.robot.ui.widgets.slidingtab.SlidingTabLayout;
import com.readyidu.robot.utils.data.DataTranUtils;
import com.readyidu.robot.utils.view.DialogUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

public class WeatherActivity extends BaseVoiceBarActivity {

    private SlidingTabLayout commonTabLayout;
    private ViewPager viewPager;
    private RelativeLayout mBackRl;

    private ViewPagerAdapter mAdapter;
    private List<Fragment> fragmentList = new ArrayList<>();
    private boolean isSDKOutside;   //是否从sdk外部直接进入


    @Override
    protected void onResume() {
        super.onResume();
        AppConfig.mCurrentCageType = AppConfig.TYPE_WEATHER;
        FloatViewInstance.attach(findViewById(android.R.id.content), this, new FloatView.OnMessageResult() {
            @Override
            public void onMessageResult(String s) {
                searchContent = s;
                requestVoiceBarNet();
            }
        });
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_weather;
    }

    @Override
    protected void bindViews() {
        super.bindViews();
        commonTabLayout = (SlidingTabLayout) findViewById(R.id.commontablayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        mBackRl = (RelativeLayout) findViewById(R.id.rl_back);

    }

    @Override
    protected void bindEvents() {
        super.bindEvents();
        mBackRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickBack();
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            isSDKOutside = intent.getBooleanExtra("isSDKOutside", false);
            if (isSDKOutside) {
                Weather data = intent.getParcelableExtra("data");
                if (null != data) {
                    setData(data);
                } else {
                    requestData();
                }
            } else {
                Weather data = intent.getParcelableExtra("data");
                setData(data);
            }
        }
        //检测有没有定位权限
        voicePermission();

        registerRxBus(BaseMessageReceiverEvent.class, new Consumer<BaseMessageReceiverEvent>() {
            @Override
            public void accept(BaseMessageReceiverEvent messageReceiverEvent) throws Exception {
                if (messageReceiverEvent != null && messageReceiverEvent.message != null) {
                    Weather weather = DataTranUtils.tranWeather(messageReceiverEvent.message);
                    isSDKOutside = messageReceiverEvent.isSDKOutside();
                    if (isSDKOutside) {
                        if (null != weather) {
                            setData(weather);
                        } else {
                            requestData();
                        }
                    } else {
                        setData(weather);
                    }
                } else {
                    ToastUtils.showShortToast(mContext, "没有搜到数据");
                }
            }
        });
    }

    private boolean voicePermission() {
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.
                checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0x123);
            return false;
        }
    }

    @Override
    protected void bindKeyBoardShowEvent() {

    }

    @Override
    protected void requestVoiceBarNet() {
        requestData();
    }

    private void requestData() {
        //TODO 网络接口要修改
        DialogUtils.showProgressDialog(this);
//        listDaily.scrollToPosition(0);
        if (TextUtils.isEmpty(searchContent)) {
            searchContent = "我要查天气";
        }
        addDisposable(RobotServiceImpl.getRobotWeatherResponse(searchContent, false)
                .subscribeWith(new NetSubscriber<BaseModel>() {
                    @Override
                    protected void onSuccess(BaseModel baseModel) {
                        Weather weather = DataTranUtils.tranWeather(baseModel);
                        if (weather != null && weather.getHeWeather5() != null && weather.getHeWeather5().size() > 0) {
                            setData(weather);
                        } else {
                            showToast("小益没有找到您要的天气信息");
                        }
                    }

                    @Override
                    protected void onFailure(int errorCode, String errorMessage) {

                    }
                }));
    }

    private void setData(Weather weather) {
        if (weather == null || weather.getHeWeather5() == null || weather.getHeWeather5().size() == 0) {
            return;
        }
        fragmentList.clear();
        try {

            ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();

            String[] titleArray = new String[weather.getHeWeather5().size()];
            for (int i = 0; i < weather.getHeWeather5().size(); i++) {
                HeWeatherInfo heWeatherInfo = weather.getHeWeather5().get(i);
                String title = heWeatherInfo.basic.parent_city + "·" + heWeatherInfo.basic.city;
                titleArray[i] = title;

                mTabEntities.add(new TabEntity(title, R.drawable.ic_location, 0));
                WeatherFragment fragment = new WeatherFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("data", heWeatherInfo);
                fragment.setArguments(bundle);
                fragmentList.add(fragment);
            }


            mAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentList, null);
            viewPager.setAdapter(mAdapter);
            viewPager.setOffscreenPageLimit(fragmentList.size());
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    commonTabLayout.setCurrentTab(position);
                    viewPager.setCurrentItem(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
            commonTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
                @Override
                public void onTabSelect(int position) {
                    commonTabLayout.setCurrentTab(position);
                    viewPager.setCurrentItem(position);
                }

                @Override
                public void onTabReselect(int position) {

                }
            });
            commonTabLayout.setViewPager(viewPager, titleArray, mTabEntities);
            commonTabLayout.setCurrentTab(0);
            commonTabLayout.updateTabSelection(0);
            mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            showToast("数据异常");
            e.printStackTrace();
        }
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

}