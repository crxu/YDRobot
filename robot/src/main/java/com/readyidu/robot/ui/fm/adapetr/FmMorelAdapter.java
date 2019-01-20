package com.readyidu.robot.ui.fm.adapetr;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.readyidu.robot.R;
import com.ximalaya.ting.android.opensdk.model.category.Category;

import java.util.List;

/**
 * Created by yuzhang on 2018/4/20.
 */

public class FmMorelAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<Category> mData;

    private OnItemClickListener mOnItemClickListener;



    public FmMorelAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<Category> data) {
        mData = data;
    }

    public void addData(List<Category> data) {
        mData.addAll(data);
    }

    public List<Category> getData() {
        return mData;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fm_more_cell, parent, false);
        return new RecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        RecyclerHolder recyclerHolder = (RecyclerHolder) holder;
        Category data = mData.get(position);
        Glide.with(mContext).load(data.getCoverUrlSmall()).into(recyclerHolder.fmMoreImg);
        recyclerHolder.fmMoreTv.setText(data.getCategoryName());
        recyclerHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener != null){
                    mOnItemClickListener.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(mData == null){
            return 0;
        }
        return mData.size();
    }

    class RecyclerHolder extends RecyclerView.ViewHolder {
        TextView fmMoreTv;
        ImageView fmMoreImg;

        private RecyclerHolder(View itemView) {
            super(itemView);
            fmMoreTv = (TextView) itemView.findViewById(R.id.fm_more_tv);
            fmMoreImg = (ImageView) itemView.findViewById(R.id.fm_more_img);
        }
    }

    public interface OnItemClickListener {
        void onClick(int position);

        void onLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
