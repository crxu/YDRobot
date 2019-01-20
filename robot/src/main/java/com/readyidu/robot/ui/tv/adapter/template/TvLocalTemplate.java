package com.readyidu.robot.ui.tv.adapter.template;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.readyidu.robot.R;
import com.readyidu.robot.component.rxbus.RxBus;
import com.readyidu.robot.event.tv.TvEvent;
import com.readyidu.robot.model.business.tv.TvChannel;
import com.readyidu.robot.ui.adapter.common.base.ItemViewDelegate;
import com.readyidu.robot.ui.adapter.common.base.ViewHolder;
import com.readyidu.robot.ui.tv.utils.TvDataTransform;
import com.readyidu.robot.ui.tv.view.flowlayout.FlowLayout;
import com.readyidu.robot.ui.tv.view.flowlayout.TagAdapter;
import com.readyidu.robot.ui.tv.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wlq
 * @description 地方台列表
 * @date 2017/11/28 下午5:03
 */
public class TvLocalTemplate implements ItemViewDelegate<TvChannel> {

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_tv_classify_local;
    }

    @Override
    public boolean isForViewType(TvChannel item, int position) {
        return item.type == 1;
    }

    @Override
    public void convert(final ViewHolder holder, final TvChannel tvChannel, final int classifyPos) {
        TextView mTvTitle = (TextView) holder.getView(R.id.tv_tv_classify_local_city_name);
        LinearLayout llSeeMore = (LinearLayout) holder.getView(R.id.ll_see_more);
        final TagFlowLayout tagFlowLayout = (TagFlowLayout) holder.getView(R.id.tfl_tv_local_see_more);

        if (classifyPos == TvDataTransform.getClassifyPositionSecond()
                && tvChannel.classifyPosition == TvDataTransform.getClassifyPosition()) {

            mTvTitle.setTextColor(mTvTitle.getContext().getResources().getColor(R.color.theme_color));
            TvChannel currentTvChannel = TvDataTransform.getCurrentTvChannel();
            if (currentTvChannel != null) {
                mTvTitle.setText(tvChannel.g + "：" + currentTvChannel.c);
            } else {
                mTvTitle.setText(tvChannel.g + "");
            }
        } else {
            mTvTitle.setTextColor(mTvTitle.getContext().getResources().getColor(R.color.content_color));
            mTvTitle.setText(tvChannel.g);
        }

        final ArrayList<TvChannel> tvChannels = tvChannel.tvDemands;
        final ArrayList<TvChannel> tvTemps = new ArrayList<>();
        if (tvChannels.size() > 4) {
            tvTemps.addAll(tvChannels.subList(0, 4));
        } else {
            tvTemps.addAll(tvChannels);
        }
        List<String> list = new ArrayList<>();
        for (TvChannel tc : tvTemps) {
            list.add(tc.c);
        }
        tagFlowLayout.setAdapter(new TagAdapter<String>(list) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) LayoutInflater.from(tagFlowLayout.getContext()).inflate(R.layout.
                        layout_tv_classify_local_see_more_textview, tagFlowLayout, false);
                tv.setText(s);
                return tv;
            }
        });

        tagFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                TvDataTransform.setChannelPosition(position);
                TvDataTransform.setClassifyPosition(TvDataTransform.getClassifyPositionTemp());
                TvDataTransform.setClassifyPositionSecond(classifyPos);
                TvDataTransform.getCurrentTvChannels().clear();
                TvDataTransform.getCurrentTvChannels().addAll(tvChannel.tvDemands);
                RxBus.getInstance().send(new TvEvent.PlayEvent(tvTemps.get(position)));
                return true;
            }
        });

        for (int i = 0; i < tagFlowLayout.getChildCount(); i++) {
            View tv = tagFlowLayout.getChildAt(i);
            if (TvDataTransform.getClassifyPosition() == TvDataTransform.getClassifyPositionTemp()) {
                if (TvDataTransform.getClassifyPositionSecond() == classifyPos) {
                    if (TvDataTransform.getChannelPosition() == i) {
                        tv.setSelected(true);
                    } else {
                        tv.setSelected(false);
                    }
                } else {
                    tv.setSelected(false);
                }
            } else {
                tv.setSelected(false);
            }
        }

        llSeeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //查看更多
                RxBus.getInstance().send(new TvEvent.SeeMoreEvent(tvChannel, classifyPos));
            }
        });
    }
}