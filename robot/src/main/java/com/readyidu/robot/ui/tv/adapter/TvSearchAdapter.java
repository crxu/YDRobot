package com.readyidu.robot.ui.tv.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.readyidu.robot.R;
import com.readyidu.robot.model.business.tv.TvChannel;

import java.util.List;

/**
 * @Autour: wlq
 * @Description: 频道列表
 * @Date: 2017/10/12 13:51
 * @Update: 2017/10/12
 * @UpdateRemark:
 * @Version: V1.0
 */
public class TvSearchAdapter extends RecyclerView.Adapter<TvSearchAdapter.StateHolder> {

    private Context context;
    private List<TvChannel> mDatas;
    private int selectedPosition = -1;

    public TvSearchAdapter(Context context, List<TvChannel> tvStations) {
        this.context = context;
        this.mDatas = tvStations;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public StateHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new StateHolder(LayoutInflater.from(context).inflate(R.layout.item_tv_search, parent, false));
    }

    @Override
    public void onBindViewHolder(final StateHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPosition = holder.getLayoutPosition();
                onItemClickListener.OnItemClick(view, holder, selectedPosition);
                notifyDataSetChanged();
            }
        });

        TvChannel tvType = mDatas.get(position);
        holder.tvChannelName.setText(tvType.c + "");
    }

    public class StateHolder extends RecyclerView.ViewHolder {
        TextView tvChannelName;

        public StateHolder(View itemView) {
            super(itemView);
            tvChannelName = (TextView) itemView.findViewById(R.id.tv_search_name);
        }
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {  //定义接口，实现Recyclerview点击事件
        void OnItemClick(View view, StateHolder holder, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {   //实现点击
        this.onItemClickListener = onItemClickListener;
    }
}