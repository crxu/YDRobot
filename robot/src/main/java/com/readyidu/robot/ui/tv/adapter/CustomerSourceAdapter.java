package com.readyidu.robot.ui.tv.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.readyidu.robot.R;
import com.readyidu.robot.component.rxbus.RxBus;
import com.readyidu.robot.event.CustomerSourceNotifyEvent;
import com.readyidu.robot.model.business.tv.TvChannel;
import com.readyidu.robot.ui.adapter.common.CommonAdapter;
import com.readyidu.robot.ui.adapter.common.base.ViewHolder;
import com.readyidu.robot.ui.tv.activity.SourceRenameActivity;
import com.readyidu.robot.ui.tv.utils.CustomerSourceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gx on 2017/12/25.
 */
public class CustomerSourceAdapter extends CommonAdapter<TvChannel> {
    private int status;//0 正常状态； 1 删除状态； 2 排序状态；3 同步到TV盒子状态

    public CustomerSourceAdapter(Context context, List<TvChannel> datas) {
        super(context, R.layout.item_customer_source, datas);
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void selectAll() {
        if (mDatas != null && mDatas.size() > 0) {
            for (TvChannel source : mDatas) {
                source.setSelect(true);
            }
        }
    }

    public void cancelSelectAll() {
        if (mDatas != null && mDatas.size() > 0) {
            for (TvChannel source : mDatas) {
                source.setSelect(false);
            }
        }
    }

    public void delete() {
        for (int i = 0; i < mDatas.size(); i++) {
            TvChannel tvChannel = mDatas.get(i);
            if (tvChannel.isSelect()) {
                mDatas.remove(tvChannel);
                CustomerSourceUtils.deleteTvChannel(tvChannel);
                i--;
            }
        }
    }

    public List<TvChannel> getSelectData() {
        List<TvChannel> selectData = new ArrayList<>();
        for (TvChannel tvChannel : mDatas) {
            if (tvChannel.isSelect()) {
                selectData.add(tvChannel);
            }
        }
        return selectData;
    }

    public boolean isNormal() {
        return status == 0;
    }

    public boolean isSort() {
        return status == 2;
    }

    @Override
    protected void convert(ViewHolder holder, final TvChannel source, final int position) {

        holder.setText(R.id.txt_source_name, source.c + "");

        if (source.isSelect()) {
            holder.setImageResource(R.id.img_select, R.drawable.ic_source_select);
        } else {
            holder.setImageResource(R.id.img_select, R.drawable.ic_source_no_select);
        }

        switch (status) {
            case 0:
                holder.setVisible(R.id.img_select, false);
                holder.setVisible(R.id.img_tag, false);
                holder.setVisible(R.id.txt_rename, true);
                break;
            case 1:
            case 3:
                holder.setVisible(R.id.img_select, true);
                holder.setVisible(R.id.img_tag, false);
                holder.setVisible(R.id.txt_rename, false);
                break;
            case 2:
                holder.setVisible(R.id.img_select, false);
                holder.setVisible(R.id.img_tag, true);
                holder.setVisible(R.id.txt_rename, false);
                break;
            default:
                break;
        }
        holder.setOnClickListener(R.id.txt_rename, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SourceRenameActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("position", position - 1);
                mContext.startActivity(intent);
            }
        });

        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                source.toggleSelece();
                RxBus.getInstance().send(new CustomerSourceNotifyEvent());
            }
        });

        if (mDatas.size() == position) {
            holder.setVisible(R.id.line, false);
        } else {
            holder.setVisible(R.id.line, true);
        }
    }
}