package com.readyidu.robot.ui.tv.adapter.template;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.readyidu.robot.R;
import com.readyidu.robot.component.rxbus.RxBus;
import com.readyidu.robot.event.tv.TvEvent;
import com.readyidu.robot.model.business.tv.TvChannel;
import com.readyidu.robot.ui.adapter.common.base.ItemViewDelegate;
import com.readyidu.robot.ui.adapter.common.base.ViewHolder;
import com.readyidu.robot.ui.tv.utils.CustomerSourceUtils;
import com.readyidu.robot.ui.tv.utils.TvDataTransform;

/**
 * @author Wlq
 * @description 连续剧直播类列表
 * @date 2017/11/28 下午5:03
 */
public class TvCustomerTemplate implements ItemViewDelegate<TvChannel> {

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_tv_classify_sitcom_live;
    }

    @Override
    public boolean isForViewType(TvChannel item, int position) {
        return item.type == -2;
    }

    @Override
    public void convert(final ViewHolder holder, final TvChannel tvChannel, final int position) {
        Context context = holder.getConvertView().getContext();
        TextView tvPlayName = holder.getView(R.id.tv_tv_classify_sitcom_live_playing);
        TextView tvPlay = (TextView) holder.getView(R.id.tv_tv_classify_sitcom_live_start_play);
        holder.getConvertView().setBackgroundColor(Color.WHITE);

        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == TvDataTransform.getChannelPosition()
                        && tvChannel.kindPosition == TvDataTransform.getKindPosition()
                        && tvChannel.classifyPosition == TvDataTransform.getClassifyPosition()
                        && tvChannel.classifyPositionSecond == TvDataTransform.getClassifyPositionSecond()) {
                    return;
                }
                TvDataTransform.setClassifyPosition(TvDataTransform.getClassifyPositionTemp());
                TvDataTransform.setKindPosition(0);
                TvDataTransform.setChannelPosition(position);

                RxBus.getInstance().send(new TvEvent.PlayEvent(tvChannel));
            }
        });
        String cString = tvChannel.c;
        tvPlayName.setText(TextUtils.isEmpty(cString) ? "暂无节目信息" : cString);


        String key = tvChannel.c + tvChannel.s + "";

        if (key.equals(CustomerSourceUtils.getKey())) {
            tvPlay.setSelected(true);
            holder.setTextColorRes(R.id.tv_tv_classify_sitcom_live_playing, R.color.theme_color);
            holder.setImageResource(R.id.iv_tv_classify_sitcom_live_play, R.drawable.ic_port_channel_play);
        } else {
            tvPlay.setSelected(false);
            holder.setTextColorRes(R.id.tv_tv_classify_sitcom_live_playing, R.color.content_color);
            holder.setImageResource(R.id.iv_tv_classify_sitcom_live_play, R.drawable.ic_port_channel_play_default);
        }

        holder.setVisible(R.id.iv_tv_classify_sitcom_live_play, false);
        holder.setVisible(R.id.tv_tv_classify_sitcom_live_start_play, false);

        if (CustomerSourceUtils.getTvChannels(context).size() - 1 == position) {
            holder.setVisible(R.id.view_tv_classify_sitcom_live_line1, false);
        } else {
            holder.setVisible(R.id.view_tv_classify_sitcom_live_line1, true);
        }
    }
}