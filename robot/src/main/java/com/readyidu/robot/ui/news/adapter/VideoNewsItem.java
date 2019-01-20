package com.readyidu.robot.ui.news.adapter;

import android.text.Html;
import android.text.TextUtils;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.readyidu.robot.AppConfig;
import com.readyidu.robot.R;
import com.readyidu.robot.db.VideoNewsDbDao;
import com.readyidu.robot.model.business.news.VideoNewsDetail;
import com.readyidu.robot.ui.adapter.common.base.ItemViewDelegate;
import com.readyidu.robot.ui.adapter.common.base.ViewHolder;
import com.readyidu.robot.ui.news.video.JZVideoPlayer;
import com.readyidu.robot.ui.news.video.JZVideoPlayerStandard;
import com.readyidu.robot.utils.DateUtils;
import com.readyidu.robot.utils.data.ArithmeticUtils;
import com.readyidu.robot.utils.glide.GlideUtils;

/**
 * @author Wlq
 * @description 视频新闻
 * @date 2018/1/10 下午1:55
 */
public class VideoNewsItem implements ItemViewDelegate<VideoNewsDetail> {

    public String searchContent;

    public void setSearchContent(String searchContent) {
        this.searchContent = searchContent;
    }

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_video_news;
    }

    @Override
    public boolean isForViewType(VideoNewsDetail item, int position) {
        return !item.isTip();
    }

    @Override
    public void convert(ViewHolder holder, VideoNewsDetail news, int position) {
        RoundedImageView ivHead = (RoundedImageView) holder.getView(R.id.iv_item_video_news_head);
        TextView tvTitle = (TextView) holder.getView(R.id.tv_video_news_title);
        TextView tvTime = (TextView) holder.getView(R.id.tv_video_news_time);
        final JZVideoPlayerStandard videoPlayer = (JZVideoPlayerStandard) holder.getView(R.id.player_video_news);

        VideoNewsDetail.SourceBean sourceBean = news.get_source();
        GlideUtils.loadRoundedImageVideoNewsNet(ivHead.getContext(), sourceBean.getVnews_blogo(), 1000, ivHead);
        String title = sourceBean.getVnews_title();
        if (TextUtils.isEmpty(searchContent)) {
            tvTitle.setText(title);
        } else {
            int index = title.indexOf(searchContent);
            if (index >= 0) {
                String newString = "<font color=\'#f46058\'>" + searchContent + "</font>";
                String newTitle = title.replaceAll(searchContent, newString);
                tvTitle.setText(Html.fromHtml(newTitle));
            } else {
                tvTitle.setText(title);
            }
        }
        tvTime.setText(DateUtils.getDateString((long) ArithmeticUtils.mul(sourceBean.getVnews_date(),1000),
                "yyyy/MM/dd"));

        String path = sourceBean.getVnews_url();
        final String videoPath;
        if (!path.contains("http://")) {
            videoPath = "http://" + path;
        } else {
            videoPath = path;
        }
        videoPlayer.setUp(videoPath, JZVideoPlayer.SCREEN_WINDOW_LIST, sourceBean.getVnews_title());
        videoPlayer.setVideoNewsId(sourceBean.getId());
        GlideUtils.loadRoundedImageNet(videoPlayer.getContext(), sourceBean.getVnews_img(), videoPlayer.thumbImageView);
        GlideUtils.loadRoundedImageNet(videoPlayer.getContext(), sourceBean.getVnews_img(), videoPlayer.mIvAlreadyLookThumb);
        GlideUtils.loadRoundedImageNet(videoPlayer.getContext(), sourceBean.getVnews_img(), videoPlayer.mIvReloadThumb);
        GlideUtils.loadRoundedImageNet(videoPlayer.getContext(), sourceBean.getVnews_img(), videoPlayer.mIvReplayThumb);
        if (VideoNewsDbDao.isVideoLooked(sourceBean.getId(), AppConfig.APP_USERID)) {
            videoPlayer.setAlreadyLook(true);
        } else {
            videoPlayer.setAlreadyLook(false);
        }
    }
}