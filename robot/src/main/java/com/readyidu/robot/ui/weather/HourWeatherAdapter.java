package com.readyidu.robot.ui.weather;

import android.content.Context;
import android.widget.ImageView;

import com.readyidu.robot.R;
import com.readyidu.robot.model.business.weather.HeHourlyForecast;
import com.readyidu.robot.ui.adapter.common.CommonAdapter;
import com.readyidu.robot.ui.adapter.common.base.ViewHolder;
import com.readyidu.robot.utils.DateUtils;
import com.readyidu.robot.utils.glide.GlideUtils;

import java.util.List;

/**
 * Created by gx on 2017/10/17.
 */
public class HourWeatherAdapter extends CommonAdapter<HeHourlyForecast> {

    public HourWeatherAdapter(Context context, List<HeHourlyForecast> datas) {
        super(context, R.layout.item_hour_weather, datas);
    }

    @Override
    protected void convert(ViewHolder holder, HeHourlyForecast heHourlyForecast, int position) {
        holder.setText(R.id.txt_date, DateUtils.tranStr(heHourlyForecast.date) + "时");
        GlideUtils.loadImageNet(mContext, heHourlyForecast.cond.code_url, (ImageView) holder.getView(R.id.img));

        holder.setText(R.id.txt_temperature, heHourlyForecast.tmp + "°C");
    }
}