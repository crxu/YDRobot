package com.readyidu.robot.ui.news.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.readyidu.robot.R;
import com.readyidu.robot.db.NewsUtils;
import com.readyidu.robot.model.business.news.News;
import com.readyidu.robot.ui.adapter.common.CommonAdapter;
import com.readyidu.robot.ui.adapter.common.base.ViewHolder;
import com.readyidu.robot.ui.news.activity.NewsDetailsActivity;
import com.readyidu.robot.utils.DateUtils;
import com.readyidu.robot.utils.glide.GlideUtils;

import java.util.List;

/**
 * Created by gx on 2017/10/16.
 */
public class NewsAdapter extends CommonAdapter<News> {
    public NewsAdapter(Context context, List<News> datas) {
        super(context, R.layout.item_news, datas);
    }

    @Override
    protected void convert(final ViewHolder holder, final News news, int position) {

        final News.SourceBean source = news.get_source();
        if (source != null) {
            if (null == source.getNew_img() || source.getNew_img().trim().equals("")) {
                holder.setVisible(R.id.img, false);
            } else {
                GlideUtils.loadImage(mContext, source.getNew_img(), (ImageView) holder.getView(R.id.img));
                holder.setVisible(R.id.img, true);
            }

            holder.setText(R.id.txt_title, source.getNew_title() + "");
            holder.setText(R.id.txt_source, source.getNew_source() + "");
            holder.setText(R.id.txt_date, DateUtils.convertTimeToFormat(source.getNew_date()));
            holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    holder.setTextColorRes(R.id.txt_title, R.color.tip_color);
                    NewsUtils.addNew(news.get_id());
                    Intent intent = new Intent(mContext, NewsDetailsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("data", source);
                    intent.putExtra("isSDKOutside", false);
                    mContext.startActivity(intent);
                }
            });
        } else {
            holder.getConvertView().setOnClickListener(null);
        }
        if (NewsUtils.isReaded(news.get_id())) {
            holder.setTextColorRes(R.id.txt_title, R.color.tip_color);
        } else {
            holder.setTextColorRes(R.id.txt_title, R.color.content_color);
        }
    }
}