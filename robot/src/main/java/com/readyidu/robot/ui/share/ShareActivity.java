package com.readyidu.robot.ui.share;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.readyidu.baiduvoice.laster.FloatView;
import com.readyidu.baiduvoice.laster.FloatViewInstance;
import com.readyidu.robot.AppConfig;
import com.readyidu.robot.R;
import com.readyidu.robot.api.NetSubscriber;
import com.readyidu.robot.api.impl.RobotServiceImpl;
import com.readyidu.robot.base.BaseVoiceBarActivity;
import com.readyidu.robot.model.BaseModel;
import com.readyidu.robot.model.business.share.Share;
import com.readyidu.robot.utils.data.DataTranUtils;
import com.readyidu.robot.utils.view.DialogUtils;

import java.util.HashMap;
import java.util.List;

/**
 * Created by yuzhang on 2018/4/18.
 */

public class ShareActivity extends BaseVoiceBarActivity {

    ImageView shareImage;
    LinearLayout shareDetailList;
    TextView currentPrice;
    TextView riseValue;
    TextView riseRate;
    TextView highPrice;
    TextView lowPrice;
    TextView openingPrice;
    TextView closingPrice;
    TextView shareTime;
    ImageView shareTopBarBack;
    TextView shareTopBarTitle;
    TextView shareTopBarSubTitle;
    RelativeLayout shareTopBar;
    ScrollView shareTopScroll;
    RelativeLayout shareDataView;

    private HashMap arg;

    private boolean isSDKOutside;

    private LayoutInflater mLayoutInflater;

    boolean isUp;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void bindViews() {
        super.bindViews();

        shareImage = (ImageView) findViewById(R.id.share_image);
        shareDetailList = (LinearLayout) findViewById(R.id.share_detail_list);
        currentPrice = (TextView) findViewById(R.id.currentPrice);
        riseValue = (TextView) findViewById(R.id.riseValue);
        riseRate = (TextView) findViewById(R.id.riseRate);
        highPrice = (TextView) findViewById(R.id.highPrice);
        lowPrice = (TextView) findViewById(R.id.lowPrice);
        openingPrice = (TextView) findViewById(R.id.openingPrice);
        closingPrice = (TextView) findViewById(R.id.closingPrice);
        shareTime = (TextView) findViewById(R.id.share_time);
        shareTopBarBack = (ImageView) findViewById(R.id.share_top_bar_back);
        shareTopBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppConfig.mCurrentCageType = AppConfig.CAGE_NONE;
                FloatViewInstance.detach(findViewById(android.R.id.content));
                finish();
            }
        });
        shareTopBarTitle = (TextView) findViewById(R.id.share_top_bar_title);
        shareTopBarSubTitle = (TextView) findViewById(R.id.share_top_bar_sub_title);
        shareTopBar = (RelativeLayout) findViewById(R.id.share_top_bar);
        shareDataView = (RelativeLayout) findViewById(R.id.share_data_view);
        shareTopScroll = (ScrollView) findViewById(R.id.share_top_scroll);

        mLayoutInflater = LayoutInflater.from(this);
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void bindEvents() {
        super.bindEvents();

        Share data = null;
        Intent intent = getIntent();
        if (intent != null) {
            isSDKOutside = intent.getBooleanExtra("isSDKOutside", false);
            if (isSDKOutside) {
                data = intent.getParcelableExtra("data");
                if (null != data) {
                    setData(data);
                } else {
                    requestData();
                }
            } else {
                data = intent.getParcelableExtra("data");
                setData(data);
            }

        }

    }


    @TargetApi(Build.VERSION_CODES.M)
    private void setData(Share data) {
        String close = data.getClosingPrice();
        shareTopBarTitle.setText(data.getName());
        shareTopBarSubTitle.setText(data.getStockCode());
        shareTime.setText(data.getUpdateDateTime() + " " + "北京时间");

        try {
            float closeFloat = Float.parseFloat(close);
            float currentPriceFloat = Float.parseFloat(data.getCurrentPrice());
            float riseValueFloat = Float.parseFloat(data.getRiseValue());
            float highPriceFloat = Float.parseFloat(data.getHighPrice());
            float lowPriceFloat = Float.parseFloat(data.getLowPrice());
            float openingPriceFloat = Float.parseFloat(data.getOpeningPrice());

            if (currentPriceFloat >= closeFloat) {
                isUp = true;
                currentPrice.setTextColor(getResources().getColor(R.color.color_share_red));
                riseValue.setTextColor(getResources().getColor(R.color.color_share_red));
                riseRate.setTextColor(getResources().getColor(R.color.color_share_red));
            } else {
                isUp = false;
                currentPrice.setTextColor(getResources().getColor(R.color.color_share_green));
                riseValue.setTextColor(getResources().getColor(R.color.color_share_green));
                riseRate.setTextColor(getResources().getColor(R.color.color_share_green));
            }
            if (highPriceFloat >= closeFloat) {
                highPrice.setTextColor(getResources().getColor(R.color.color_share_red));
            } else {
                highPrice.setTextColor(getResources().getColor(R.color.color_share_green));
            }
            if (lowPriceFloat >= closeFloat) {
                lowPrice.setTextColor(getResources().getColor(R.color.color_share_red));
            } else {
                lowPrice.setTextColor(getResources().getColor(R.color.color_share_green));
            }
            if (openingPriceFloat >= closeFloat) {
                openingPrice.setTextColor(getResources().getColor(R.color.color_share_red));
            } else {
                openingPrice.setTextColor(getResources().getColor(R.color.color_share_green));
            }

            currentPrice.setText(data.getCurrentPrice());
            riseValue.setText(data.getRiseValue());
            riseRate.setText(data.getRiseRate());
            highPrice.setText(data.getHighPrice());
            lowPrice.setText(data.getLowPrice());
            openingPrice.setText(data.getOpeningPrice());
            closingPrice.setText(close);

            Glide.with(mContext).load(data.getUrl()).into(shareImage);

            shareDetailList.removeAllViews();
            List<Share.ShareDetail> details = data.getDetail();
            for (Share.ShareDetail detail :
                    details) {
                View cellView = mLayoutInflater.inflate(R.layout.activity_share_detail_cell, null);
                TextView shareDetailType = (TextView) cellView.findViewById(R.id.activity_share_detail_type);
                String type = detail.getRole();
                shareDetailType.setText(type);
                if (type.startsWith("买")) {
                    shareDetailType.setBackgroundResource(R.drawable.shape_blue_radius_rect);
                } else {
                    shareDetailType.setBackgroundResource(R.drawable.shape_red_radius_rect);
                }
                TextView shareDetailCount = (TextView) cellView.findViewById(R.id.activity_share_detail_count);
                shareDetailCount.setText(detail.getCount());
                TextView shareDetailPrice = (TextView) cellView.findViewById(R.id.activity_share_detail_price);
                shareDetailPrice.setText(detail.getPrice());
                LinearLayout.LayoutParams cellLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                cellLayout.topMargin = (int) getResources().getDimension(R.dimen.font_size_12);
                shareDetailList.addView(cellView, cellLayout);
            }

            final Share finalData = data;
            shareTopScroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY > shareDataView.getHeight()) {
                        if (isUp) {
                            shareTopBarSubTitle.setTextColor(getResources().getColor(R.color.color_share_red));
                        } else {
                            shareTopBarSubTitle.setTextColor(getResources().getColor(R.color.color_share_green));
                        }
                        shareTopBarSubTitle.setText(finalData.getCurrentPrice() + "  " + finalData.getRiseValue() + "  " + finalData.getRiseRate());
                    } else {
                        shareTopBarSubTitle.setTextColor(getResources().getColor(R.color.color_share_gray));
                        shareTopBarSubTitle.setText(finalData.getStockCode());
                    }
                }
            });
        } catch (Exception e) {
            showToast("股票数据异常");
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
        DialogUtils.showProgressDialog(this);
//        listDaily.scrollToPosition(0);
        if (TextUtils.isEmpty(searchContent)) {
            searchContent = "";
        }
        addDisposable(RobotServiceImpl.getRobotResponse(searchContent + "股票", false)
                .subscribeWith(new NetSubscriber<BaseModel>() {
                    @Override
                    protected void onSuccess(BaseModel baseModel) {
                        Share share = DataTranUtils.tranShare(baseModel);
                        if (share != null) {
                            setData(share);
                        } else {
                            showToast("小益没有找到您要的股票信息");
                        }
                    }

                    @Override
                    protected void onFailure(int errorCode, String errorMessage) {

                    }
                }));
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_share_layout;
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
