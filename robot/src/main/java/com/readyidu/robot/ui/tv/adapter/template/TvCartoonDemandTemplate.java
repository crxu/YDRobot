package com.readyidu.robot.ui.tv.adapter.template;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
 * @description 少儿动画点播类列表
 * @date 2017/12/2 下午5:09
 */
public class TvCartoonDemandTemplate implements ItemViewDelegate<TvChannel> {

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_cartoon_on_demand_select;
    }

    @Override
    public boolean isForViewType(TvChannel item, int position) {
        return item.type == 3 && item.isMovie;
    }

    @Override
    public void convert(final ViewHolder holder, final TvChannel tvChannel, final int position) {
        RelativeLayout relativeLayout = (RelativeLayout) holder.getView(R.id.rl_movie_on_demand_item);
        TextView tvTop = (TextView) holder.getView(R.id.tv_movie_on_demand_item);
        TextView tvName = (TextView) holder.getView(R.id.tv_movie_on_demand_name);
        ImageView imageView = (ImageView) holder.getView(R.id.iv_movie_on_demand_play);

        String cstring = tvChannel.c;
        tvName.setText(TextUtils.isEmpty(cstring) ? "暂无节目信息" : cstring);
        if (position < 2) {
            tvTop.setVisibility(View.VISIBLE);
        } else {
            tvTop.setVisibility(View.GONE);
        }

        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TvDataTransform.setClassifyPosition(TvDataTransform.getClassifyPositionTemp());
                TvDataTransform.setKindPosition(1);
                TvDataTransform.setChannelPosition(position);

                RxBus.getInstance().send(new TvEvent.PlayEvent(tvChannel));
            }
        });

        if (TvDataTransform.getClassifyPosition() == TvDataTransform.getClassifyPositionTemp()) {
            if (TvDataTransform.getKindPosition() == 1) {
                if (position == TvDataTransform.getChannelPosition()) {
                    setUI(true, relativeLayout, tvName, imageView);
                } else {
                    setUI(false, relativeLayout, tvName, imageView);
                }
            } else {
                setUI(false, relativeLayout, tvName, imageView);
            }
        } else {
            setUI(false, relativeLayout, tvName, imageView);
        }
    }

    private void setUI(boolean isSelect, RelativeLayout relativeLayout, TextView tvName, ImageView imageView) {
        if (isSelect) {
            relativeLayout.setBackgroundResource(R.drawable.bg_theme_color_round);
            tvName.setTextColor(Color.WHITE);

            imageView.setImageResource(R.drawable.ic_sitcom_play_select);
        } else {
            relativeLayout.setBackgroundResource(R.drawable.bg_grey_color_round);
            tvName.setTextColor(tvName.getContext().getResources().getColor(R.color.content_color));
            imageView.setImageResource(R.drawable.ic_sitcom_play_unselect);
        }
    }
}
