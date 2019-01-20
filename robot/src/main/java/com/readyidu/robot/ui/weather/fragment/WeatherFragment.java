package com.readyidu.robot.ui.weather.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.readyidu.robot.R;
import com.readyidu.robot.base.BaseFragment;
import com.readyidu.robot.model.business.weather.HeDailyForecast;
import com.readyidu.robot.model.business.weather.HeHourlyForecast;
import com.readyidu.robot.model.business.weather.HeSuggestionInfo;
import com.readyidu.robot.model.business.weather.HeWeatherInfo;
import com.readyidu.robot.ui.adapter.common.wrapper.HeaderAndFooterWrapper;
import com.readyidu.robot.ui.weather.DailyWeatherAdapter;
import com.readyidu.robot.ui.weather.HourWeatherAdapter;
import com.readyidu.robot.ui.weather.SuggestAdapter;
import com.readyidu.robot.ui.weather.activity.WeatherActivity;
import com.readyidu.robot.utils.glide.GlideUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiucheng on 2018/2/28.
 * 功能：天气页面
 */

public class WeatherFragment extends BaseFragment {
    private View imgBack;
    private TextView txtCity;
    private TextView txtTemperature;
    private ImageView imgNow;
    private TextView txtNow;
    private TextView txtWind;
    private TextView txtHum;
    private TextView txtAir;
    private ImageView imgWind;
    private ImageView imgHum;
    private ImageView imgAir;
    private List<HeHourlyForecast> hourlyForecasts = new ArrayList<>();
    private List<HeDailyForecast> dailyForecasts = new ArrayList<>();
    private List<HeSuggestionInfo.Suggestion> suggestions = new ArrayList<>();
    private HourWeatherAdapter hourWeatherAdapter;
    private HeaderAndFooterWrapper dailyWeatherAdapter;
    private SuggestAdapter suggestAdapter;
    private RecyclerView listDaily;
    private RecyclerView gvList;


    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_weather;
    }

    @Override
    protected void bindViews(View contentView) {

        imgBack = contentView.findViewById(R.id.img_back);
        txtCity = (TextView) contentView.findViewById(R.id.txt_city);

        View head = View.inflate(mContext, R.layout.head_weather, null);
        View foot = View.inflate(mContext, R.layout.foot_weather, null);
        foot.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        txtTemperature = (TextView) head.findViewById(R.id.txt_temperature);
        imgNow = (ImageView) head.findViewById(R.id.img_now);
        txtNow = (TextView) head.findViewById(R.id.txt_now);

        txtWind = (TextView) head.findViewById(R.id.txt_wind);
        txtHum = (TextView) head.findViewById(R.id.txt_hum);
        txtAir = (TextView) head.findViewById(R.id.txt_air);
        imgWind = (ImageView) head.findViewById(R.id.img_wind);
        imgHum = (ImageView) head.findViewById(R.id.img_hum);
        imgAir = (ImageView) head.findViewById(R.id.img_air);

        gvList = (RecyclerView) head.findViewById(R.id.gv_list);
        hourWeatherAdapter = new HourWeatherAdapter(mContext, hourlyForecasts);
        gvList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        gvList.setAdapter(hourWeatherAdapter);

        RecyclerView suggest = (RecyclerView) foot.findViewById(R.id.suggest);
        suggest.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        suggest.setLayoutManager(new GridLayoutManager(mContext, 3));
        suggestAdapter = new SuggestAdapter(mContext, suggestions);
        suggest.setAdapter(suggestAdapter);

        listDaily = (RecyclerView) contentView.findViewById(R.id.list_daily);
        listDaily.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        DailyWeatherAdapter adapter = new DailyWeatherAdapter(mContext, dailyForecasts);
        dailyWeatherAdapter = new HeaderAndFooterWrapper(adapter);
        dailyWeatherAdapter.addHeaderView(head);
        dailyWeatherAdapter.addFootView(foot);
        listDaily.setAdapter(dailyWeatherAdapter);

    }

    @Override
    protected void bindEvents() {

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WeatherActivity activity = (WeatherActivity) mContext;
                activity.clickBack();
            }
        });


        Bundle arguments = getArguments();
        if (arguments != null) {
            HeWeatherInfo data = arguments.getParcelable("data");
            setData(data);
        }
    }

    public void setData(HeWeatherInfo heWeatherInfo) {
        if (heWeatherInfo == null) {
            return;
        }
        gvList.scrollToPosition(0);
        listDaily.setVisibility(View.VISIBLE);
        if (heWeatherInfo.basic != null) {
            txtCity.setText(heWeatherInfo.basic.city + "");
        } else {
            txtCity.setText("未知");
            listDaily.setVisibility(View.GONE);
        }

        if (heWeatherInfo.now != null) {
            txtTemperature.setText(heWeatherInfo.now.tmp);
            GlideUtils.loadImageNet(mContext, heWeatherInfo.now.cond.code_url, imgNow);
            txtNow.setText(heWeatherInfo.now.cond.txt);
            imgWind.setImageResource(R.drawable.ic_fengxiang);
            txtWind.setText(heWeatherInfo.now.wind.dir + " " + heWeatherInfo.now.wind.sc + "级");
            imgHum.setImageResource(R.drawable.ic_shidu);
            txtHum.setText("湿度 " + heWeatherInfo.now.hum + "%");
        } else {
            listDaily.setVisibility(View.GONE);
        }

        if (heWeatherInfo.aqi != null && heWeatherInfo.aqi.city != null) {
            imgAir.setImageResource(R.drawable.ic_air);
            txtAir.setText(" " + heWeatherInfo.aqi.city.aqi + " " + heWeatherInfo.aqi.city.qlty + "");

            imgAir.setVisibility(View.VISIBLE);
            txtAir.setVisibility(View.VISIBLE);
        } else {
            imgAir.setVisibility(View.INVISIBLE);
            txtAir.setVisibility(View.INVISIBLE);
        }

        if (heWeatherInfo.hourly_forecast != null) {
            hourlyForecasts.clear();
            hourlyForecasts.addAll(heWeatherInfo.hourly_forecast);
            hourWeatherAdapter.notifyDataSetChanged();
        } else {
            listDaily.setVisibility(View.GONE);
        }

        if (heWeatherInfo.daily_forecast != null) {
            dailyForecasts.clear();
            dailyForecasts.addAll(heWeatherInfo.daily_forecast);
            dailyWeatherAdapter.notifyDataSetChanged();
        } else {
            listDaily.setVisibility(View.GONE);
        }

        if (heWeatherInfo.suggestion != null) {
            suggestions.clear();
            for (int i = 0; i < heWeatherInfo.suggestion.size(); i++) {
                suggestions.add(heWeatherInfo.suggestion.get(i));
            }
            suggestAdapter.notifyDataSetChanged();
        } else {
            listDaily.setVisibility(View.GONE);
        }
    }
}
