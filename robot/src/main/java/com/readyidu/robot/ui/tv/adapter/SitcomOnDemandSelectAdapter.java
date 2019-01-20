package com.readyidu.robot.ui.tv.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.readyidu.basic.base.ViewHolder;
import com.readyidu.robot.R;
import com.readyidu.robot.component.rxbus.RxBus;
import com.readyidu.robot.event.tv.TvEvent;
import com.readyidu.robot.model.business.tv.TvChannel;
import com.readyidu.robot.ui.tv.utils.TvDataTransform;

import java.util.List;

/**
 * @author Wlq
 * @description 连续剧集选择
 * @date 2017/11/29 下午1:55
 */
public class SitcomOnDemandSelectAdapter extends GridCommonAdapter<TvChannel> {

    private int classifyPos;
    private int pageSize;

    public SitcomOnDemandSelectAdapter(Context context, List<TvChannel> datas) {
        super(context, R.layout.item_sitcom_on_demand_select, datas);
    }

    public void setClassifyPos(int classifyPos) {
        this.classifyPos = classifyPos;
    }

    public void setCurrentPage(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public void convert(ViewHolder holder, final TvChannel tvChannel, final int position) {

        TextView textView = holder.getView(R.id.tv_sitcom_select_count);
        final int count = 30 * pageSize + position;
        String countString = count + 1 + "";
        textView.setText(countString);

        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TvDataTransform.setClassifyPosition(TvDataTransform.getClassifyPositionTemp());
                TvDataTransform.setKindPosition(1);
                TvDataTransform.setClassifyPositionSecond(tvChannel.classifyPositionSecond);
                TvDataTransform.setChannelPosition(count);
                notifyDataSetChanged();



                RxBus.getInstance().send(new TvEvent.PlayEvent(tvChannel));
            }
        });

        if (TvDataTransform.getClassifyPosition() == TvDataTransform.getClassifyPositionTemp()
                && tvChannel.kindPosition == TvDataTransform.getKindPosition()
                && classifyPos == TvDataTransform.getClassifyPositionSecond()
                && TvDataTransform.getChannelPosition() == count) {
            setUI(true, textView);
        } else {
            setUI(false, textView);
        }
    }

    private void setUI(boolean isSelect, TextView textView) {
        if (isSelect) {
            textView.setTextColor(Color.WHITE);
            textView.setBackgroundResource(R.drawable.bg_sitcom_select);
        } else {
            textView.setTextColor(textView.getContext().getResources().getColor(R.color.content_color));
            textView.setBackgroundResource(R.drawable.bg_sitcom_unselect);
        }
    }
}