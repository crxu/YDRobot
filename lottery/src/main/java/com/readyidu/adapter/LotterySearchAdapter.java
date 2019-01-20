package com.readyidu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.readyidu.api.response.LotteryContent;
import com.readyidu.utils.JLog;
import com.readyidu.xylottery.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 */
public class LotterySearchAdapter extends RecyclerView.Adapter<LotterySearchAdapter.ViewHolder> {

    private Context                         mContext;
    private List<LotteryContent.ResultBean> mData     = new ArrayList<>();
    private boolean                         isLottery = false;
    public interface OnItemListener {
        void onItemChange(int position, LotteryContent.ResultBean info);
    }

    private int type;

    public void setType(int type) {
        this.type = type;
    }

    private OnItemListener mListener;

    public void setOnItemClickListener(OnItemListener mListener) {
        this.mListener = mListener;
    }

    public LotterySearchAdapter(Context context, List<LotteryContent.ResultBean> mData) {
        this.mContext = context;
        this.mData = mData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_lottery_info, parent,
                false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.title_tv = view.findViewById(R.id.title_tv);
        viewHolder.main_lay = view.findViewById(R.id.main_lay);
        viewHolder.view_lay = view.findViewById(R.id.view_lay);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final LotteryContent.ResultBean category = mData.get(position);
        if (position % 2 == 0) {
            holder.main_lay.setBackgroundResource(R.drawable.bg_shuang_select);
        } else {
            holder.main_lay.setBackgroundResource(R.drawable.bg_dan_select);
        }

        holder.main_lay.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (mListener != null) {
                    if (hasFocus) {
                        mListener.onItemChange(position, category);
                    }
                }
            }
        });

        String content = "";
        if (isLottery) {
            content = category.getName() + "    第" + category.getIssue() + "期";
        } else {
            content = "第" + category.getIssue() + "期";
        }
        holder.title_tv.setText(content);
        String number = category.getNumber();
        List<String> data = new ArrayList<>();
        data.clear();
        try {
            if (!TextUtils.isEmpty(number)) {
                String[] spit = number.split(",");
                if (spit.length > 0) {
                    for (int i = 0; i < spit.length; i++) {
                        if (spit[i].contains("|")) {
                            String hh[] = spit[i].split("\\|");
                            data.add(hh[0]);
                            data.add(hh[1]);
                        } else {
                            data.add(spit[i]);
                        }
                    }
                }
                holder.view_lay.removeAllViews();
                holder.view_lay.addView(addView1(data));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        JLog.d(number + "=========asdasdas===" + data);
    }

    public void setIsLottery(boolean isLottery) {
        this.isLottery = isLottery;
    }

    private View addView1(List<String> data) {
        LinearLayout view = new LinearLayout(mContext);
        for (int i = 0; i < data.size(); i++) {
            View view_lottery = LayoutInflater.from(mContext).inflate(R.layout.view_lottery, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, calculateDpToPx(), 0);
            view_lottery.setLayoutParams(lp);
            TextView title_tv = (TextView) view_lottery.findViewById(R.id.title_tv);
            title_tv.setText(data.get(i));
            switch (type) {
                case 1:
                case 4:
                    if (i < data.size() - 1) {
                        title_tv.setBackgroundResource(R.drawable.hong_qiu);
                    } else {
                        title_tv.setBackgroundResource(R.drawable.lan_qiu);
                    }
                    break;
                case 2:
                    if (i < data.size() - 2) {
                        title_tv.setBackgroundResource(R.drawable.hong_qiu);
                    } else {
                        title_tv.setBackgroundResource(R.drawable.lan_qiu);
                    }
                    break;
                case 3:
                case 5:
                case 6:
                case 7:
                case 8:
                    title_tv.setBackgroundResource(R.drawable.hong_qiu);
                    break;
                case 9:
                case 10:
                case 11:
                    title_tv.setBackgroundResource(R.drawable.shap_item_normal);
                    break;
            }
            view.addView(view_lottery);
        }
        return view;
    }

    private int calculateDpToPx() {
        int padding_in_dp = 0;
        switch (type) {
            case 1://7
                padding_in_dp = 40;
                break;
            case 2://7
                padding_in_dp = 40;
                break;
            case 3://3
                padding_in_dp = 70;
                break;
            case 4://8
                padding_in_dp = 30;
                break;
            case 5://3
                padding_in_dp = 70;
                break;
            case 6://7
                padding_in_dp = 40;
                break;
            case 7://5
                padding_in_dp = 60;
                break;
            case 8://5
                padding_in_dp = 60;
                break;
            case 9://14
                padding_in_dp = 40;
                break;
            case 10://8
                padding_in_dp = 60;
                break;
            case 11://12
                padding_in_dp = 50;
                break;
        }
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (padding_in_dp * scale + 0.5f);
    }

    @Override
    public int getItemCount() {
        return null == mData ? 0 : mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView     title_tv;
        LinearLayout main_lay;
        LinearLayout view_lay;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

}
