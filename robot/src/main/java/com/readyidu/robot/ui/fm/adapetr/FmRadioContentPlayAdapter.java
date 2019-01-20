package com.readyidu.robot.ui.fm.adapetr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.readyidu.robot.R;
import com.ximalaya.ting.android.opensdk.model.live.schedule.LiveAnnouncer;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;

import java.util.List;

/**
 * Created by yuzhang on 2018/6/2.
 */

public class FmRadioContentPlayAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater layoutInflater;
    private List<Schedule> mData;

    private long selectId = -1;
    private long liveId = -1;

    public FmRadioContentPlayAdapter(Context context) {
        mContext = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setData(List<Schedule> data) {
        mData = data;
    }

    public void addData(List<Schedule> data) {
        mData.addAll(data);
    }

    public List<Schedule> getData() {
        return mData;
    }

    public void setSelectId(long selectId) {
        this.selectId = selectId;
        notifyDataSetChanged();
    }

    public void setLiveId(long liveId) {
        this.liveId = liveId;
        this.selectId = liveId;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.fm_radio_content_play_cell, null);
            holder = new ViewHolder(convertView);
            holder.fmRadioContentPlayCellTime = (TextView) convertView.findViewById(R.id.fm_radio_content_play_cell_time);
            holder.fmRadioContentPlayCellLive = (TextView) convertView.findViewById(R.id.fm_radio_content_play_cell_live);
            holder.fmRadioContentPlayCellTitle = (TextView) convertView.findViewById(R.id.fm_radio_content_play_cell_title);
            holder.fmRadioContentPlayCellActor = (TextView) convertView.findViewById(R.id.fm_radio_content_play_cell_actor);
            holder.fmRadioContentPlayCellPlay = (ImageView) convertView.findViewById(R.id.fm_radio_content_play_cell_play);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Schedule data = mData.get(position);

        if(selectId == data.getDataId()){
            holder.fmRadioContentPlayCellPlay.setVisibility(View.VISIBLE);
            holder.fmRadioContentPlayCellTitle.setTextColor(mContext.getResources().getColor(R.color.material_blue));
            holder.fmRadioContentPlayCellTime.setTextColor(mContext.getResources().getColor(R.color.material_blue));
        }else{
            holder.fmRadioContentPlayCellPlay.setVisibility(View.GONE);
            holder.fmRadioContentPlayCellTitle.setTextColor(mContext.getResources().getColor(R.color.color_share_gray));
            holder.fmRadioContentPlayCellTime.setTextColor(mContext.getResources().getColor(R.color.color_share_gray));
        }
        holder.fmRadioContentPlayCellTitle.setText(data.getRelatedProgram().getProgramName());
        if (liveId == data.getDataId()) {
            holder.fmRadioContentPlayCellLive.setVisibility(View.VISIBLE);
        } else {
            holder.fmRadioContentPlayCellLive.setVisibility(View.INVISIBLE);
        }
        holder.fmRadioContentPlayCellTime.setText(data.getStartTime() + "-" + data.getEndTime());
        List<LiveAnnouncer> announcers = data.getRelatedProgram().getAnnouncerList();
        if (announcers.size() > 0) {
            StringBuffer announcerSb = new StringBuffer();
            for (LiveAnnouncer liveAnnouncer : announcers) {
                announcerSb.append(liveAnnouncer.getNickName() + " ");
            }
            holder.fmRadioContentPlayCellActor.setText(announcerSb.toString());
        } else {
            holder.fmRadioContentPlayCellActor.setText("未知");
        }

        return convertView;
    }


    static class ViewHolder {
        TextView fmRadioContentPlayCellTime;
        TextView fmRadioContentPlayCellLive;
        TextView fmRadioContentPlayCellTitle;
        TextView fmRadioContentPlayCellActor;
        ImageView fmRadioContentPlayCellPlay;

        ViewHolder(View view) {
        }
    }
}
