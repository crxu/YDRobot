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
import com.readyidu.robot.ui.tv.adapter.MovieOnDemandSelectAdapter;
import com.readyidu.robot.ui.tv.view.MyGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wlq
 * @description 电影点播类列表
 * @date 2017/11/28 下午5:03
 */
public class TvMovieOnDemandTemplate implements ItemViewDelegate<TvChannel> {

    public TvMovieOnDemandTemplate(Context context) {
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_tv_classify_movie_on_demand;
    }

    @Override
    public boolean isForViewType(TvChannel item, int position) {
        return item.type == 2 && item.isMovie;
    }

    @Override
    public void convert(final ViewHolder holder, final TvChannel tvChannel, final int position) {

        TextView tvName = (TextView) holder.getView(R.id.tv_tv_classify_movie_on_demand_classify_name);
        MyGridView gridView = (MyGridView) holder.getView(R.id.gv_movie_on_demand);

        String cString = tvChannel.c;
        tvName.setText(TextUtils.isEmpty(cString) ? "暂无节目信息" : cString);
        List<TvChannel> tvChannels = tvChannel.tvDemands;
        List<TvChannel> tvTemps = new ArrayList<>();
        if (tvChannels.size() > 6) {
            tvTemps.addAll(tvChannels.subList(0, 6));
        } else {
            tvTemps.addAll(tvChannels);
        }

        MovieOnDemandSelectAdapter adapter = new MovieOnDemandSelectAdapter(gridView.getContext(), tvTemps, position);
        gridView.setAdapter(adapter);

        holder.getView(R.id.ll_see_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //查看更多
                RxBus.getInstance().send(new TvEvent.SeeMoreEvent(tvChannel, position));
            }
        });
    }
}