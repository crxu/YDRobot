package com.readyidu.robot.ui.tv.adapter.template;

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
 * @description 电影直播类列表
 * @date 2017/11/28 下午5:03
 */
public class TvMovieLiveTemplate implements ItemViewDelegate<TvChannel> {

    public TvMovieLiveTemplate() {
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_tv_classify_movie_live;
    }

    @Override
    public boolean isForViewType(TvChannel item, int position) {
        return item.type == 2 && !item.isMovie;
    }

    @Override
    public void convert(final ViewHolder holder, final TvChannel tvChannel, final int position) {
        TextView tvClassifyName = (TextView) holder.getView(R.id.tv_tv_classify_movie_live_classify_name);
        TextView tvPlayName = holder.getView(R.id.tv_tv_classify_movie_live_playing);
        TextView tvPlay = (TextView) holder.getView(R.id.tv_tv_classify_movie_live_start_play);

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
                TvDataTransform.setChannelPosition(position);
                TvDataTransform.setKindPosition(0);

                RxBus.getInstance().send(new TvEvent.PlayEvent(tvChannel));
            }
        });

        String cstring = tvChannel.c;
        tvClassifyName.setText(TextUtils.isEmpty(cstring) ? "暂无分类信息" : cstring);
        String nstring = tvChannel.n;
        tvPlayName.setText(TextUtils.isEmpty(nstring) ? "暂无节目信息" : nstring);

        if (TvDataTransform.getClassifyPosition() == TvDataTransform.getClassifyPositionTemp()
                && TvDataTransform.getKindPosition() == 0
                && position == TvDataTransform.getChannelPosition()) {
            tvPlay.setSelected(true);
            holder.setTextColorRes(R.id.tv_tv_classify_movie_live_playing, R.color.theme_color);
            holder.setImageResource(R.id.iv_tv_classify_movie_live_play, R.drawable.ic_port_channel_play);
        } else {
            tvPlay.setSelected(false);
            holder.setTextColorRes(R.id.tv_tv_classify_movie_live_playing, R.color.content_color);
            holder.setImageResource(R.id.iv_tv_classify_movie_live_play, R.drawable.ic_port_channel_play_default);
        }
    }
}