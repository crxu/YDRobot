package com.readyidu.robot.ui.fm.adapetr;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.readyidu.robot.R;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioCategory;

import java.util.List;

/**
 * Created by yuzhang on 2018/4/20.
 */

public class FmChannelAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<RadioCategory> mData;
    private boolean showAll;
    private int showSize;

    private OnItemClickListener mOnItemClickListener;



    public FmChannelAdapter(Context context, boolean showAll) {
        mContext = context;
        this.showAll = showAll;
    }

    public void setData(List<RadioCategory> data) {
        mData = data;
    }

    public void addData(List<RadioCategory> data) {
        mData.addAll(data);
    }

    public List<RadioCategory> getData() {
        return mData;
    }

    public void setShowAll(boolean showAll) {
        this.showAll = showAll;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fm_channel_cell, parent, false);
        return new RecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        RecyclerHolder recyclerHolder = (RecyclerHolder) holder;
        if (position == showSize - 1) {
            recyclerHolder.fmChannelImg.setVisibility(View.VISIBLE);
            if(showAll){
                recyclerHolder.fmChannelImg.setImageResource(R.mipmap.fm_channel_up);
            }else{
                recyclerHolder.fmChannelImg.setImageResource(R.mipmap.fm_channel_down);
            }
            recyclerHolder.fmChannelTv.setVisibility(View.GONE);
            recyclerHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == showSize - 1) {
                        showAll = !showAll;
                        notifyDataSetChanged();
                    }else{
                        if(mOnItemClickListener != null){
                            mOnItemClickListener.onClick(position);
                        }
                    }
                }
            });
        } else {
            recyclerHolder.fmChannelTv.setVisibility(View.VISIBLE);
            recyclerHolder.fmChannelImg.setVisibility(View.GONE);
            if(position < mData.size()){
                RadioCategory data = mData.get(position);
                recyclerHolder.fmChannelTv.setText(data.getRadioCategoryName());

                recyclerHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (position == showSize - 1) {
                            showAll = !showAll;
                            notifyDataSetChanged();
                        }else{
                            if(mOnItemClickListener != null){
                                mOnItemClickListener.onClick(mData.get(position).getId());
                            }
                        }
                    }
                });
            }else{
                recyclerHolder.fmChannelTv.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mData == null || mData.size() == 0) {
            showSize = 0;
        }else{
            if (showAll) {
                showSize = mData.size() + mData.size() % 4;
            } else {
                showSize = 8;
            }
        }
        return showSize;
    }

    class RecyclerHolder extends RecyclerView.ViewHolder {
        TextView fmChannelTv;
        ImageView fmChannelImg;

        private RecyclerHolder(View itemView) {
            super(itemView);
            fmChannelTv = (TextView) itemView.findViewById(R.id.fm_channel_tv);
            fmChannelImg = (ImageView) itemView.findViewById(R.id.fm_channel_img);
        }
    }

    public interface OnItemClickListener {
        void onClick(long id);

        void onLongClick(long id);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
