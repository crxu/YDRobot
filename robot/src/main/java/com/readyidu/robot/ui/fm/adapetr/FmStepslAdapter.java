package com.readyidu.robot.ui.fm.adapetr;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.readyidu.robot.R;

/**
 * Created by yuzhang on 2018/4/20.
 */

public class FmStepslAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private int playCount;
    private int selectedIndex;

    private OnItemClickListener mOnItemClickListener;

    public FmStepslAdapter(Context context, int playCount) {
        mContext = context;
        this.playCount = playCount;
        selectedIndex = -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fm_steps_cell, parent, false);
        return new RecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        RecyclerHolder recyclerHolder = (RecyclerHolder) holder;
        if(selectedIndex == position){
            recyclerHolder.fmStepsCellRl.setBackgroundResource(R.drawable.shape_blue_radius_rect);
            recyclerHolder.fmStepsCellTv.setTextColor(Color.WHITE);
        }else{
            recyclerHolder.fmStepsCellRl.setBackgroundResource(R.drawable.shape_gray_radius_rect);
            recyclerHolder.fmStepsCellTv.setTextColor(mContext.getResources().getColor(R.color.color_share_gray));
        }
        if(position == getItemCount() - 1){
            recyclerHolder.fmStepsCellTv.setText((position * 50 + 1) + "-" + playCount);
        }else{
            recyclerHolder.fmStepsCellTv.setText((position * 50 + 1) + "-" + (position + 1) * 50);
        }

        recyclerHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    selectedIndex = position;
                    notifyDataSetChanged();
                    mOnItemClickListener.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (playCount == 0) {
            return 0;
        }
        if (playCount <= 50) {
            return 1;
        }
        if (playCount > 50) {
            if (playCount % 50 == 0) {
                return playCount / 50;
            } else {
                return playCount / 50 + 1;
            }
        }
        return 0;
    }

    class RecyclerHolder extends RecyclerView.ViewHolder {
        TextView fmStepsCellTv;
        RelativeLayout fmStepsCellRl;

        private RecyclerHolder(View itemView) {
            super(itemView);
            fmStepsCellTv = (TextView) itemView.findViewById(R.id.fm_steps_cell_tv);
            fmStepsCellRl = (RelativeLayout) itemView.findViewById(R.id.fm_steps_cell_rl);
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
