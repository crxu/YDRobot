package com.readyidu.robot.ui.tv.adapter.template;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.readyidu.robot.R;
import com.readyidu.robot.component.rxbus.RxBus;
import com.readyidu.robot.event.tv.TvEvent;
import com.readyidu.robot.model.business.tv.TvChannel;
import com.readyidu.robot.ui.adapter.common.base.ItemViewDelegate;
import com.readyidu.robot.ui.adapter.common.base.ViewHolder;
import com.readyidu.robot.ui.tv.utils.TvDataTransform;
import com.readyidu.robot.utils.view.ToastUtil;

/**
 * @author Wlq
 * @description 央视&卫视&本地直播类列表
 * @date 2017/11/28 下午5:03
 */
public class TvChannelLiveTemplate implements ItemViewDelegate<TvChannel> {

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_tv_classify_channel_live;
    }

    @Override
    public boolean isForViewType(TvChannel item, int position) {
        return item.type == 0;
    }

    @Override
    public void convert(final ViewHolder holder, final TvChannel tvChannel, final int position) {
        final Context context = holder.itemView.getContext();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position == TvDataTransform.getChannelPosition()
                        && tvChannel.classifyPosition == TvDataTransform.getClassifyPosition())
                    return;

                if (tvChannel.o.size() > 0) {

                    TvDataTransform.setClassifyPosition(TvDataTransform.getClassifyPositionTemp());
                    TvDataTransform.setChannelPosition(position);

                    RxBus.getInstance().send(new TvEvent.PlayEvent(tvChannel));
                } else {
                    ToastUtil.showToast(context, "当前节目没有可播放源");
                }
            }
        });

        ImageView ivPlay = holder.getView(R.id.iv_port_channel_play);

        if (TvDataTransform.isThis(position, tvChannel.classifyPosition)) {
            holder.setBackgroundColor(R.id.parent, Color.WHITE);
            holder.setTextColorRes(R.id.tv_channel_name, R.color.theme_color);
            holder.setTextColorRes(R.id.tv_program_name, R.color.theme_color);
            ivPlay.setVisibility(View.VISIBLE);
        } else {
            holder.setBackgroundColor(R.id.parent, context.getResources().getColor(R.color.background_color));
            holder.setTextColorRes(R.id.tv_channel_name, R.color.content_color);
            holder.setTextColorRes(R.id.tv_program_name, R.color.tip_color);
            ivPlay.setVisibility(View.GONE);
        }

        holder.setText(R.id.tv_channel_name, TextUtils.isEmpty(tvChannel.c) ? "暂无频道信息" : tvChannel.c);
        holder.setText(R.id.tv_program_name, TextUtils.isEmpty(tvChannel.n) ? "暂无节目信息" : tvChannel.n);
        holder.setTag(R.id.tv_program_name, tvChannel.i);
    }
}