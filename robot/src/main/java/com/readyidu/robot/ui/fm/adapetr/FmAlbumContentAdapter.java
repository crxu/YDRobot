package com.readyidu.robot.ui.fm.adapetr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.readyidu.robot.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by yuzhang on 2018/2/21.
 */

public class FmAlbumContentAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater layoutInflater;
    private List<Track> mData;

    private long selectId = -1;
    private int selectSteps;
    private String sort;
    private int playCount;
    private boolean isSelectSteps;

    public FmAlbumContentAdapter(Context context) {
        mContext = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setData(List<Track> data,String sort,int playCount,boolean isSelectSteps) {
        mData = data;
        this.sort = sort;
        this.playCount = playCount;
        this.isSelectSteps = isSelectSteps;
    }

    public void setData(List<Track> data,int selectSteps,String sort,int playCount,boolean isSelectSteps) {
        mData = data;
        this.selectSteps = selectSteps;
        this.sort = sort;
        this.playCount = playCount;
        this.isSelectSteps = isSelectSteps;
    }

    public void addData(List<Track> data) {
        mData.addAll(data);
    }

    public List<Track> getData() {
        return mData;
    }

    public void setSelectId(long selectId){
        this.selectId = selectId;
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
            convertView = layoutInflater.inflate(R.layout.fm_album_content_cell, null);
            holder = new ViewHolder(convertView);
            holder.fmAlbumContentPlayImg = (ImageView) convertView.findViewById(R.id.fm_album_content_play_img);
            holder.fmAlbumContentSteps = (TextView) convertView.findViewById(R.id.fm_album_content_steps);
            holder.fmAlbumContentPlayFl = (FrameLayout) convertView.findViewById(R.id.fm_album_content_play_fl);
            holder.fmAlbumContentListTitle = (TextView) convertView.findViewById(R.id.fm_album_content_list_title);
            holder.fmAlbumContentListPlayCount = (TextView) convertView.findViewById(R.id.fm_album_content_list_play_count);
            holder.fmAlbumContentListTime = (TextView) convertView.findViewById(R.id.fm_album_content_list_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Track data = mData.get(position);

        if(selectId == data.getDataId()){
            holder.fmAlbumContentPlayImg.setVisibility(View.VISIBLE);
            holder.fmAlbumContentSteps.setVisibility(View.GONE);
            holder.fmAlbumContentListTitle.setTextColor(mContext.getResources().getColor(R.color.material_blue));
        }else{
            holder.fmAlbumContentPlayImg.setVisibility(View.GONE);
            holder.fmAlbumContentSteps.setVisibility(View.VISIBLE);
            holder.fmAlbumContentListTitle.setTextColor(mContext.getResources().getColor(R.color.color_share_gray));
        }
        holder.fmAlbumContentListTitle.setText(data.getTrackTitle());

        if(isSelectSteps){
            if(sort.equals("asc")){
                holder.fmAlbumContentSteps.setText(String.valueOf(selectSteps +position + 1));
            }else{
                holder.fmAlbumContentSteps.setText(String.valueOf(selectSteps + 50 - position));
            }
        }else{
            if(sort.equals("asc")){
                holder.fmAlbumContentSteps.setText(String.valueOf(position + 1));
            }else{
                holder.fmAlbumContentSteps.setText(String.valueOf(playCount - position));
            }
        }


        holder.fmAlbumContentListPlayCount.setText(data.getPlayCount() + "次");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm  yyyy.MM.dd");
        holder.fmAlbumContentListTime.setText(sdf.format(data.getCreatedAt()));
        return convertView;
    }


    static class ViewHolder {
        ImageView fmAlbumContentPlayImg;
        TextView fmAlbumContentSteps;
        FrameLayout fmAlbumContentPlayFl;
        TextView fmAlbumContentListTitle;
        TextView fmAlbumContentListPlayCount;
        TextView fmAlbumContentListTime;

        ViewHolder(View view) {
        }
    }
}
