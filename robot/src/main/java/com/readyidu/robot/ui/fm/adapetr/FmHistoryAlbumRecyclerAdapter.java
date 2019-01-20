package com.readyidu.robot.ui.fm.adapetr;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.google.gson.Gson;
import com.readyidu.robot.R;
import com.readyidu.robot.ui.fm.util.AlbumHistoryModel;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yuzhang on 2018/2/21.
 */

public class FmHistoryAlbumRecyclerAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private LayoutInflater layoutInflater;
    List<AlbumHistoryModel> mData;
    List<Album> albumData;

    private Map<Integer, Long> selectIndex;

    private OnItemClickListener mOnItemClickListener;

    private boolean isEdit;

    public FmHistoryAlbumRecyclerAdapter(Context context) {
        mContext = context;
        layoutInflater = LayoutInflater.from(context);
        selectIndex = new HashMap<Integer, Long>();
    }

    public void setData(List<AlbumHistoryModel> data) {
        albumData = new ArrayList<Album>();
        for (AlbumHistoryModel albumHistoryModel : data) {
            Album album = null;
            try {
                album = new Gson().fromJson(albumHistoryModel.getAlbumOrColumnOrRadioJson(), Album.class);
            } catch (Exception e) {

            }
            if (album != null) {
                albumData.add(album);
            }
        }
        mData = data;
    }

    public void addData(List<AlbumHistoryModel> data) {
        for (AlbumHistoryModel albumHistoryModel : data) {
            Album album = null;
            try {
                album = new Gson().fromJson(albumHistoryModel.getAlbumOrColumnOrRadioJson(), Album.class);
            } catch (Exception e) {

            }
            if (album != null) {
                albumData.add(album);
            }
        }
        mData.addAll(data);
    }

    public List<Album> getData() {
        return albumData;
    }

    public void select(Integer index) {
        index = index - 1;
        if (selectIndex.containsKey(index)) {
            selectIndex.remove(index);
        } else {
            selectIndex.put(index, mData.get(index).getHistRecdId());
        }
        notifyDataSetChanged();
    }

    public void allSelect() {
        int size = mData.size();
        for (int i = 0; i < size; i++) {
            selectIndex.put(i, mData.get(i).getHistRecdId());
        }
        notifyDataSetChanged();
    }

    public void allCancelSelect() {
        selectIndex.clear();
        notifyDataSetChanged();
    }

    public String getSelect() {
        StringBuffer selectSb = new StringBuffer();
        Set<Integer> keySet = selectIndex.keySet();
        for (Integer key :
                keySet) {
            selectSb.append(mData.get(key).getHistRecdId() + ",");
        }
        return selectSb.toString().substring(0, selectSb.length() - 1);
    }


    public void setEdit(boolean edit) {
        isEdit = edit;
        if(edit == false){
            allCancelSelect();
        }
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.fm_album_list_cell, parent,false);
        return new RecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        RecyclerHolder recyclerHolder = (RecyclerHolder) holder;
        if (isEdit) {
            recyclerHolder.fmAlbumCellSelect.setVisibility(View.VISIBLE);
            if (selectIndex.containsKey(position)) {
                recyclerHolder.fmAlbumCellSelect.setImageResource(R.mipmap.fm_xuanze);
            } else {
                recyclerHolder.fmAlbumCellSelect.setImageResource(R.mipmap.fm_weixuan);
            }
        } else {
            recyclerHolder.fmAlbumCellSelect.setVisibility(View.GONE);
        }

        Album data = albumData.get(position);
        Glide.with(mContext).load(data.getCoverUrlSmall()).transform(new GlideRoundTransform(mContext)).into(recyclerHolder.fmChannelCellImage);
        recyclerHolder.fmChannelCellTitle.setText(data.getAlbumTitle());
        recyclerHolder.fmChannelCellContent.setText(data.getAlbumIntro());
        recyclerHolder.fmChannelCellPlayCount.setText(data.getPlayCount() + "次");
        recyclerHolder.fmAlbumCellPlaySteps.setText(data.getIncludeTrackCount() + "集");

        recyclerHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (albumData == null) {
            return 0;
        }
        return albumData.size();
    }

    class RecyclerHolder extends RecyclerView.ViewHolder {
        ImageView fmChannelCellImage;
        TextView fmChannelCellTitle;
        TextView fmChannelCellContent;
        TextView fmChannelCellPlayCount;
        TextView fmAlbumCellPlaySteps;
        ImageView fmAlbumCellSelect;

        private RecyclerHolder(View itemView) {
            super(itemView);
            fmAlbumCellSelect = (ImageView) itemView.findViewById(R.id.fm_album_cell_select);
            fmChannelCellImage = (ImageView) itemView.findViewById(R.id.fm_album_cell_image);
            fmChannelCellTitle = (TextView) itemView.findViewById(R.id.fm_album_cell_title);
            fmChannelCellContent = (TextView) itemView.findViewById(R.id.fm_album_cell_content);
            fmChannelCellPlayCount = (TextView) itemView.findViewById(R.id.fm_album_cell_play_count);
            fmAlbumCellPlaySteps = (TextView) itemView.findViewById(R.id.fm_album_cell_play_steps);
        }
    }

    public interface OnItemClickListener{
        void onClick( int position);
        void onLongClick( int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
        mOnItemClickListener = onItemClickListener;
    }


    public static class GlideRoundTransform extends BitmapTransformation {

        private static float radius = 0f;

        public GlideRoundTransform(Context context) {
            this(context, 4);
        }

        public GlideRoundTransform(Context context, int dp) {
            super(context);
            this.radius = Resources.getSystem().getDisplayMetrics().density * dp;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return roundCrop(pool, toTransform);
        }

        private static Bitmap roundCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;

            Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
            canvas.drawRoundRect(rectF, radius, radius, paint);
            return result;
        }

        @Override
        public String getId() {
            return getClass().getName() + Math.round(radius);
        }
    }
}
