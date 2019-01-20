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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuzhang on 2018/2/21.
 */

public class FmAlbumPlayContentAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater layoutInflater;
    private List<Track> mData;

    private long selectId = -1;
    private int selectSteps;
    private String sort;
    private int playCount;
    private boolean isSelectSteps;

    public FmAlbumPlayContentAdapter(Context context) {
        mContext = context;
        layoutInflater = LayoutInflater.from(context);
        mData = new ArrayList<Track>();
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

    public void addData(List<Track> data,int selectSteps,String sort,int playCount,boolean isSelectSteps) {
        mData.addAll(data);
        this.selectSteps = selectSteps;
        this.sort = sort;
        this.playCount = playCount;
        this.isSelectSteps = isSelectSteps;
    }

    public List<Track> getData() {
        return mData;
    }

    public void setSelectId(long selectId){
        this.selectId = selectId;
        notifyDataSetChanged();
    }

    public long getSelectId() {
        return selectId;
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
            convertView = layoutInflater.inflate(R.layout.fm_album_play_content_cell, null);
            holder = new ViewHolder(convertView);
            holder.fmAlbumPlayContentCellPlayImg = (ImageView) convertView.findViewById(R.id.fm_album_play_content_cell_play_img);
            holder.fmAlbumPlayContentCellSteps = (TextView) convertView.findViewById(R.id.fm_album_play_content_cell_steps);
            holder.fmAlbumPlayContentCellFl = (FrameLayout) convertView.findViewById(R.id.fm_album_play_content_cell_fl);
            holder.fmAlbumPlayContentCellTitle = (TextView) convertView.findViewById(R.id.fm_album_play_content_cell_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Track data = mData.get(position);
        if(selectId == data.getDataId()){
            holder.fmAlbumPlayContentCellPlayImg.setVisibility(View.VISIBLE);
            holder.fmAlbumPlayContentCellSteps.setVisibility(View.GONE);
            holder.fmAlbumPlayContentCellTitle.setTextColor(mContext.getResources().getColor(R.color.material_blue));
        }else{
            holder.fmAlbumPlayContentCellPlayImg.setVisibility(View.GONE);
            holder.fmAlbumPlayContentCellSteps.setVisibility(View.VISIBLE);
            holder.fmAlbumPlayContentCellTitle.setTextColor(mContext.getResources().getColor(R.color.color_share_gray));
        }
        holder.fmAlbumPlayContentCellTitle.setText(data.getTrackTitle());
        if(isSelectSteps){
            if(sort.equals("asc")){
                holder.fmAlbumPlayContentCellSteps.setText(String.valueOf(selectSteps +position + 1));
            }else{
                holder.fmAlbumPlayContentCellSteps.setText(String.valueOf(selectSteps + 50 - position));
            }
        }else{
            if(sort.equals("asc")){
                holder.fmAlbumPlayContentCellSteps.setText(String.valueOf(position + 1));
            }else{
                holder.fmAlbumPlayContentCellSteps.setText(String.valueOf(playCount - position));
            }
        }
        return convertView;
    }



    static class ViewHolder {
        ImageView fmAlbumPlayContentCellPlayImg;
        TextView fmAlbumPlayContentCellSteps;
        FrameLayout fmAlbumPlayContentCellFl;
        TextView fmAlbumPlayContentCellTitle;

        ViewHolder(View view) {
        }
    }

}
