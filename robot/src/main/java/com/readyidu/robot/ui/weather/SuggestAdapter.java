package com.readyidu.robot.ui.weather;

import android.content.Context;

import com.readyidu.robot.R;
import com.readyidu.robot.model.business.weather.HeSuggestionInfo;
import com.readyidu.robot.ui.adapter.common.CommonAdapter;
import com.readyidu.robot.ui.adapter.common.base.ViewHolder;

import java.util.List;

/**
 * Created by gx on 2017/10/17.
 */
public class SuggestAdapter extends CommonAdapter<HeSuggestionInfo.Suggestion> {

    public SuggestAdapter(Context context, List<HeSuggestionInfo.Suggestion> datas) {
        super(context, R.layout.item_weather_suggest, datas);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    protected void convert(ViewHolder holder, HeSuggestionInfo.Suggestion suggestion, int position) {
        holder.setImageResource(R.id.iv_suggestion, suggestion.sug);
        holder.setText(R.id.txt_type, suggestion.brf + "");
        holder.setText(R.id.tv_suggestion_desc, suggestion.type + "");
    }
}