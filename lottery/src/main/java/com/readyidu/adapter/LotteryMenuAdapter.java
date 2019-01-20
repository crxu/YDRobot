package com.readyidu.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.readyidu.api.response.LotteryMenu;
import com.readyidu.xylottery.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 */
public class LotteryMenuAdapter extends RecyclerView.Adapter<LotteryMenuAdapter.ViewHolder> {

    private Context           mContext;
    private List<LotteryMenu> mData = new ArrayList<>();

    public interface OnItemListener {
        void onItemChange(int position, LotteryMenu info);
    }

    private OnItemListener mListener;

    public void setOnItemClickListener(OnItemListener mListener) {
        this.mListener = mListener;
    }

    public LotteryMenuAdapter(Context context, List<LotteryMenu> mData) {
        this.mContext = context;
        this.mData = mData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_lottery_menu, parent,
                false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.title_tv = view.findViewById(R.id.title_tv);
        viewHolder.main_lay = view.findViewById(R.id.main_lay);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final LotteryMenu category = mData.get(position);
        holder.main_lay.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (mListener != null)
                        mListener.onItemChange(position, category);
                }
                holder.title_tv.setTextColor(
                        hasFocus ? Color.parseColor("#ffffff") : Color.parseColor("#80ffffff"));
            }
        });
        holder.title_tv.setText(category.getName());
        if (category.isLeft()) {
            holder.main_lay.setBackgroundResource(R.drawable.noleft);
            holder.title_tv.setTextColor(Color.parseColor("#ffffff"));
        } else {
            holder.title_tv.setTextColor(holder.main_lay.hasFocus() ? Color.parseColor("#ffffff")
                    : Color.parseColor("#80ffffff"));
            holder.main_lay.setBackgroundResource(R.drawable.bg_menu_select);
        }
    }

    @Override
    public int getItemCount() {
        return null == mData ? 0 : mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView     title_tv;
        LinearLayout main_lay;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

}
