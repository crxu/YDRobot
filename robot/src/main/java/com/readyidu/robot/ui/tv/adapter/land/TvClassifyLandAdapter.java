package com.readyidu.robot.ui.tv.adapter.land;

import android.content.Context;
import android.view.View;

import com.readyidu.robot.R;
import com.readyidu.robot.component.rxbus.RxBus;
import com.readyidu.robot.event.tv.TvEvent;
import com.readyidu.robot.model.business.tv.TvType;
import com.readyidu.robot.ui.tv.utils.TvDataTransform;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * Created by gx on 2017/11/29.
 */
public class TvClassifyLandAdapter extends CommonAdapter<TvType> {

    public TvClassifyLandAdapter(Context context, List<TvType> datas) {
        super(context, R.layout.list_land_classify, datas);
    }

    @Override
    protected void convert(ViewHolder holder, TvType tvType, final int position) {
        holder.setText(R.id.txt_name, tvType.type + "");

        if (TvDataTransform.isThis(position)) {
            holder.setTextColorRes(R.id.txt_name, R.color.theme_color);
        } else {
            holder.setTextColorRes(R.id.txt_name, android.R.color.white);
        }

        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TvDataTransform.getClassifyPositionTemp() != position) {
                    TvDataTransform.setClassifyPositionTemp(position);
                    notifyDataSetChanged();

                    RxBus.getInstance().send(new TvEvent.ChannelChangedEvent(position));
                }
            }
        });
    }
}