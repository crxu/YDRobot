package com.readyidu.robot.ui.news.adapter;

import android.text.Html;
import android.widget.TextView;

import com.readyidu.robot.R;
import com.readyidu.robot.model.business.news.VideoNewsDetail;
import com.readyidu.robot.ui.adapter.common.base.ItemViewDelegate;
import com.readyidu.robot.ui.adapter.common.base.ViewHolder;

/**
 * @author Wlq
 * @description 视频新闻
 * @date 2018/1/10 下午1:54
 */
public class VideoNewsTipItem implements ItemViewDelegate<VideoNewsDetail> {

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_video_news_header;
    }

    @Override
    public boolean isForViewType(VideoNewsDetail item, int position) {
        return item.isTip();
    }

    @Override
    public void convert(ViewHolder holder, VideoNewsDetail news, int position) {
        TextView tvTitle = (TextView) holder.getView(R.id.tv_video_news_fragment_tip);
        tvTitle.setText(Html.fromHtml(news.getTipContent()));
    }
}