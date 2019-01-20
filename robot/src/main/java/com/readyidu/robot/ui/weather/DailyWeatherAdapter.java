package com.readyidu.robot.ui.weather;

import android.content.Context;
import android.widget.ImageView;

import com.readyidu.robot.R;
import com.readyidu.robot.model.business.weather.HeDailyForecast;
import com.readyidu.robot.ui.adapter.common.CommonAdapter;
import com.readyidu.robot.ui.adapter.common.base.ViewHolder;
import com.readyidu.robot.utils.glide.GlideUtils;
import com.readyidu.robot.utils.log.LogUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by gx on 2017/10/17.
 */
public class DailyWeatherAdapter extends CommonAdapter<HeDailyForecast> {

    public DailyWeatherAdapter(Context context, List<HeDailyForecast> datas) {
        super(context, R.layout.item_daily_weather, datas);
    }

    @Override
    protected void convert(ViewHolder holder, HeDailyForecast heDailyForecast, int position) {
        if (position == 1) {
            holder.setText(R.id.txt_date_des, "今天");
        } else if (position == 2) {
            holder.setText(R.id.txt_date_des, "明天");
        } else {
            holder.setText(R.id.txt_date_des, getWeek(heDailyForecast.date));
        }

        holder.setText(R.id.txt_date, formatData(heDailyForecast.date));
        GlideUtils.loadImageNet(mContext, heDailyForecast.cond.code_d_url, (ImageView) holder.getView(R.id.img));
        holder.setText(R.id.txt, heDailyForecast.cond.txt_d + "");
        holder.setText(R.id.txt_temperature, heDailyForecast.tmp.min + "°C~" + heDailyForecast.tmp.max + "°C");
    }

    private String getWeek(String loc) {
        try {
            SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
            SimpleDateFormat dateFm2 = new SimpleDateFormat("EEEE", Locale.SIMPLIFIED_CHINESE);
            return dateFm2.format(dateFm.parse(loc)).replace("星期", "周");

        } catch (Exception e) {
            LogUtils.e(e);
        }

        return "";
    }

    //2017-08-23 10:46 -> 8/18
    private String formatData(String loc) {
        try {
            SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
            SimpleDateFormat dateFm2 = new SimpleDateFormat("MM/dd", Locale.SIMPLIFIED_CHINESE);
            return dateFm2.format(dateFm.parse(loc));

        } catch (Exception e) {
            LogUtils.e(e);
        }

        return "";
    }
}