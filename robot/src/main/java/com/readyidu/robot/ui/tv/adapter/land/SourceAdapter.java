package com.readyidu.robot.ui.tv.adapter.land;

import android.content.Context;
import android.view.View;

import com.readyidu.robot.R;
import com.readyidu.robot.component.rxbus.RxBus;
import com.readyidu.robot.event.tv.TvEvent;
import com.readyidu.robot.model.business.tv.TvSourceDetail;
import com.readyidu.robot.ui.tv.utils.TvDataTransform;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;

/**
 * Created by gx on 2017/11/30.
 */
public class SourceAdapter extends CommonAdapter<TvSourceDetail> {

    public SourceAdapter(Context context, List<TvSourceDetail> datas) {
        super(context, R.layout.item_source, datas);
    }

    @Override
    protected void convert(ViewHolder holder, final TvSourceDetail tvSource, final int position) {

        switch (tvSource.m) {
            case 1:
                holder.setText(R.id.txt_source_name, "源" + (position + 1) + "：极速");
                break;
            case 2:
                holder.setText(R.id.txt_source_name, "源" + (position + 1) + "：流畅");
                break;
            case 3:
                holder.setText(R.id.txt_source_name, "源" + (position + 1) + "：一般");
                break;
            default:
                holder.setText(R.id.txt_source_name, "源" + (position + 1) + "：流畅");
                break;
        }

        if (position == TvDataTransform.getSourcePosition()) {
            holder.setImageResource(R.id.img, R.drawable.ic_source_select);
            holder.setTextColorRes(R.id.txt_source_name, R.color.theme_color);
        } else {
            holder.setImageResource(R.id.img, R.drawable.ic_source_no_select);
            holder.setTextColorRes(R.id.txt_source_name, android.R.color.white);
        }

        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sourcePosition = TvDataTransform.getSourcePosition();

                if (sourcePosition != position) {
                    TvDataTransform.setSourcePosition(position);
                    notifyDataSetChanged();

                    //延迟执行
                    Flowable.timer(200, TimeUnit.MILLISECONDS)
                            .subscribe(new Consumer<Long>() {
                                @Override
                                public void accept(Long aLong) throws Exception {
                                    RxBus.getInstance().send(new TvEvent.ChangeSourceEvent(tvSource));
                                }
                            });
                } else {
                    RxBus.getInstance().send(new TvEvent.ChangeSourceEvent(null));
                }
            }
        });
    }
}