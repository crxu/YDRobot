package com.readyidu.robot.ui.tv.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
 * @description 电影点播查看更多gridview
 * @date 2017/11/29 下午1:55
 */
public class MovieOnDemandSeeMoreAdapter extends GridCommonAdapter<TvChannel> {

    private int mClassifyPos;

    public MovieOnDemandSeeMoreAdapter(Context context, List<TvChannel> datas) {
        super(context, R.layout.item_movie_on_demand_select, datas);
    }

    public void setmClassifyPos(int classifyPos) {
        this.mClassifyPos = classifyPos;
    }

    @Override
    public void convert(ViewHolder holder, final TvChannel tvChannel, final int position) {
        RelativeLayout relativeLayout = holder.getView(R.id.rl_movie_on_demand_item);
        TextView tvName = holder.getView(R.id.tv_movie_on_demand_name);
        ImageView ivPlay = holder.getView(R.id.iv_movie_on_demand_play);
        TextView tvTip = holder.getView(R.id.tv_movie_on_demand_item_tip);

        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TvDataTransform.setClassifyPosition(TvDataTransform.getClassifyPositionTemp());
                TvDataTransform.setKindPosition(1);
                TvDataTransform.setClassifyPositionSecond(mClassifyPos);
                TvDataTransform.setChannelPosition(position);
                notifyDataSetChanged();

                RxBus.getInstance().send(new TvEvent.PlayEvent(tvChannel));
            }
        });

        int total = mDatas.size();
        if (total > 3) {
            if (total % 2 == 0) {
                if (position == total - 1 || position == total - 2) {
                    tvTip.setVisibility(View.VISIBLE);
                } else {
                    tvTip.setVisibility(View.GONE);
                }
            } else {
                if (position == total - 1) {
                    tvTip.setVisibility(View.VISIBLE);
                } else {
                    tvTip.setVisibility(View.GONE);
                }
            }
        } else if (total == 1 || total == 2){
            tvTip.setVisibility(View.VISIBLE);
        }

        tvName.setText(tvChannel.c);

        if (TvDataTransform.getClassifyPosition() == TvDataTransform.getClassifyPositionTemp()) {
            if (TvDataTransform.getKindPosition() == 1) {
                if (mClassifyPos == TvDataTransform.getClassifyPositionSecond()) {
                    if (TvDataTransform.getChannelPosition() == position) {
                        setUI(true, relativeLayout, tvName, ivPlay);
                    } else {
                        setUI(false, relativeLayout, tvName, ivPlay);
                    }
                } else {
                    setUI(false, relativeLayout, tvName, ivPlay);
                }
            } else {
                setUI(false, relativeLayout, tvName, ivPlay);
            }
        } else {
            setUI(false, relativeLayout, tvName, ivPlay);
        }
    }

    public void setUI(boolean isSelect, RelativeLayout relativeLayout, TextView tvName, ImageView ivPlay) {
        if (isSelect) {
            relativeLayout.setBackgroundResource(R.drawable.bg_theme_color_round);
            tvName.setTextColor(Color.WHITE);
            ivPlay.setBackgroundResource(R.drawable.ic_sitcom_play_select);
        } else {
            relativeLayout.setBackgroundResource(R.drawable.bg_movie_on_demand_unselect);
            tvName.setTextColor(tvName.getContext().getResources().getColor(R.color.content_color));
            ivPlay.setBackgroundResource(R.drawable.ic_sitcom_play_unselect);
        }
    }

}