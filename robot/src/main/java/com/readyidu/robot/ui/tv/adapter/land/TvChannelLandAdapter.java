package com.readyidu.robot.ui.tv.adapter.land;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.readyidu.robot.R;
import com.readyidu.robot.component.rxbus.RxBus;
import com.readyidu.robot.event.tv.TvEvent;
import com.readyidu.robot.model.business.tv.TvChannel;
import com.readyidu.robot.ui.tv.utils.CustomerSourceUtils;
import com.readyidu.robot.ui.tv.utils.TvDataTransform;
import com.readyidu.robot.utils.view.ToastUtil;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

/**
 * Created by gx on 2017/11/29.
 */
public class TvChannelLandAdapter extends CommonAdapter<TvChannel> {

    public TvChannelLandAdapter(Context context, List<TvChannel> datas) {
        super(context, R.layout.list_land_channel, datas);
    }

    public void setDatas(List<TvChannel> datas) {
        this.mDatas = datas;
        notifyDataSetChanged();
    }

    @Override
    protected void convert(ViewHolder holder, final TvChannel tvChannel, final int position) {
        final Context context = holder.getConvertView().getContext();

        if (TvDataTransform.isThisSecondary(position, tvChannel.classifyPosition,
                tvChannel.kindPosition, tvChannel.classifyPositionSecond)) {
            holder.setTextColorRes(R.id.txt_name, R.color.theme_color);
            holder.setTextColorRes(R.id.tv_program_name, R.color.theme_color);
            holder.setImageResource(R.id.iv_port_channel_play, R.drawable.ic_port_channel_play);
        } else {
            holder.setTextColorRes(R.id.txt_name, android.R.color.white);
            holder.setTextColorRes(R.id.tv_program_name, android.R.color.white);
            holder.setImageResource(R.id.iv_port_channel_play, R.drawable.ic_port_channel_play_white);
        }

        if (tvChannel.type == -2) {
            String key = tvChannel.c + tvChannel.s + "";
            if (key.equals(CustomerSourceUtils.getKey())) {
                holder.setTextColorRes(R.id.txt_name, R.color.theme_color);
                holder.setTextColorRes(R.id.tv_program_name, R.color.theme_color);
                holder.setImageResource(R.id.iv_port_channel_play, R.drawable.ic_port_channel_play);
            } else {
                holder.setTextColorRes(R.id.txt_name, android.R.color.white);
                holder.setTextColorRes(R.id.tv_program_name, android.R.color.white);
                holder.setImageResource(R.id.iv_port_channel_play, R.drawable.ic_port_channel_play_white);
            }
        }

        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == TvDataTransform.getChannelPosition()
                        && tvChannel.classifyPosition == TvDataTransform.getClassifyPosition()
                        && tvChannel.kindPosition == TvDataTransform.getKindPosition()
                        && tvChannel.classifyPositionSecond == TvDataTransform.getClassifyPositionSecond())
                    return;

                if (tvChannel.type == 4 || tvChannel.type == -2) {
                    TvDataTransform.setClassifyPosition(tvChannel.classifyPosition);
                    TvDataTransform.setKindPosition(tvChannel.kindPosition);
                    TvDataTransform.setClassifyPositionSecond(tvChannel.classifyPositionSecond);
                    TvDataTransform.setChannelPosition(position);
                    notifyDataSetChanged();
                    RxBus.getInstance().send(new TvEvent.PlayEvent(tvChannel));
                } else {
                    if (tvChannel.o.size() > 0) {
                        TvDataTransform.setClassifyPosition(tvChannel.classifyPosition);
                        TvDataTransform.setKindPosition(tvChannel.kindPosition);
                        TvDataTransform.setClassifyPositionSecond(tvChannel.classifyPositionSecond);
                        TvDataTransform.setChannelPosition(position);
                        notifyDataSetChanged();

                        //延迟执行
                        Flowable.timer(300, TimeUnit.MILLISECONDS)
                                .subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(Long aLong) throws Exception {
                                        RxBus.getInstance().send(new TvEvent.PlayEvent(tvChannel));
                                    }
                                });
                    } else {
                        ToastUtil.showToast(context, "当前节目没有可播放源");
                    }
                }
            }
        });


        if (tvChannel.type == 4 && tvChannel.isMovie) {
            holder.setText(R.id.txt_name, "第" + (position + 1) + "集");
        } else {
            holder.setText(R.id.txt_name, tvChannel.c + "");
        }

        holder.setText(R.id.tv_program_name, TextUtils.isEmpty(tvChannel.n) ? "暂无节目信息" : tvChannel.n + "");
        holder.setTag(R.id.tv_program_name, tvChannel.i);

        if (tvChannel.isMovie) {
            holder.setVisible(R.id.tv_program_name, false);
            holder.setVisible(R.id.iv_port_channel_play, false);
        } else {
            holder.setVisible(R.id.tv_program_name, true);
            holder.setVisible(R.id.iv_port_channel_play, true);
        }

        if (tvChannel.type == -2) {
            holder.setVisible(R.id.tv_program_name, false);
            holder.setVisible(R.id.iv_port_channel_play, false);
        }
    }
}