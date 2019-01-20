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
import com.readyidu.robot.utils.event.EventUtils;

/**
 * @author Wlq
 * @description 连续剧点播类列表
 * @date 2017/11/28 下午5:03
 */
public class TvSitcomOnDemandTemplate implements ItemViewDelegate<TvChannel> {

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_tv_classify_sitcom_on_demand;
    }

    @Override
    public boolean isForViewType(TvChannel item, int position) {
        return item.type == 4 && item.isMovie;
    }

    @Override
    public void convert(final ViewHolder holder, final TvChannel tvChannel, final int position) {

        TextView tvPlayName = (TextView) holder.getView(R.id.tv_sitcom_on_demand_name);

        String cString = tvChannel.c;
        tvPlayName.setText(TextUtils.isEmpty(cString) ? "暂无节目信息" : cString);

        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (EventUtils.isFastDoubleClick(v))
                    return;

                RxBus.getInstance().send(new TvEvent.SeeMoreEvent(tvChannel, position));
            }
        });

        if (TvDataTransform.getClassifyPosition() == TvDataTransform.getClassifyPositionTemp()) {
            if (1 == TvDataTransform.getKindPosition()) {
                if (position == TvDataTransform.getClassifyPositionSecond()) {
                    tvPlayName.setTextColor(tvPlayName.getContext().getResources().getColor(R.color.theme_color));
                } else {
                    tvPlayName.setTextColor(tvPlayName.getContext().getResources().getColor(R.color.content_color));
                }
            } else {
                tvPlayName.setTextColor(tvPlayName.getContext().getResources().getColor(R.color.content_color));
            }
        } else {
            tvPlayName.setTextColor(tvPlayName.getContext().getResources().getColor(R.color.content_color));
        }
    }
}