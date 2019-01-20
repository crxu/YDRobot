package com.readyidu.robot.ui.tv.adapter.template;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.readyidu.robot.R;
import com.readyidu.robot.component.rxbus.RxBus;
import com.readyidu.robot.event.tv.TvEvent;
import com.readyidu.robot.model.business.tv.TvChannel;
import com.readyidu.robot.ui.adapter.common.base.ItemViewDelegate;
import com.readyidu.robot.ui.adapter.common.base.ViewHolder;
import com.readyidu.robot.ui.tv.utils.TvDataTransform;

/**
 * @author Wlq
 * @description 动画直播类列表
 * @date 2017/11/28 下午5:03
 */
public class TvCartoonLiveTemplate implements ItemViewDelegate<TvChannel> {


    public TvCartoonLiveTemplate(Context context) {
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_tv_classify_cartoon_live;
    }

    @Override
    public boolean isForViewType(TvChannel item, int position) {
        return item.type == 3 && !item.isMovie;
    }

    @Override
    public void convert(final ViewHolder holder, final TvChannel tvChannel, final int position) {
        TextView tvPlayName = holder.getView(R.id.tv_tv_classify_cartoon_live_playing);
        TextView tvPlay = holder.getView(R.id.tv_tv_classify_cartoon_live_start_play);

        tvPlay.setOnClickListener(new View.OnClickListener() {
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
        tvPlayName.setText((TextUtils.isEmpty(cString) ? "暂无节目信息" : cString));

        if (TvDataTransform.getClassifyPositionTemp() == TvDataTransform.getClassifyPosition()
                && TvDataTransform.getKindPosition() == 0
                && position == TvDataTransform.getChannelPosition()) {
            tvPlay.setSelected(true);
            holder.setTextColorRes(R.id.tv_tv_classify_cartoon_live_playing, R.color.theme_color);
            holder.setImageResource(R.id.iv_tv_classify_cartoon_live_play, R.drawable.ic_port_channel_play);
        } else {
            tvPlay.setSelected(false);
            holder.setTextColorRes(R.id.tv_tv_classify_cartoon_live_playing, R.color.content_color);
            holder.setImageResource(R.id.iv_tv_classify_cartoon_live_play, R.drawable.ic_port_channel_play_default);
        }
    }
}