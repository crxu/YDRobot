package com.readyidu.robot.ui.fm.adapetr;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.readyidu.robot.R;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * Created by yuzhang on 2018/2/21.
 */

public class FmAlbumAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater layoutInflater;
    private List<Album> mData;

    public FmAlbumAdapter(Context context) {
        mContext = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setData(List<Album> data) {
        mData = data;
    }

    public void addData(List<Album> data) {
        mData.addAll(data);
    }

    public List<Album> getData() {
        return mData;
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
            convertView = layoutInflater.inflate(R.layout.fm_album_list_cell, null);
            holder = new ViewHolder(convertView);
            holder.fmChannelCellImage = (ImageView) convertView.findViewById(R.id.fm_album_cell_image);
            holder.fmChannelCellTitle = (TextView) convertView.findViewById(R.id.fm_album_cell_title);
            holder.fmChannelCellContent = (TextView) convertView.findViewById(R.id.fm_album_cell_content);
            holder.fmChannelCellPlayCount = (TextView) convertView.findViewById(R.id.fm_album_cell_play_count);
            holder.fmAlbumCellPlaySteps = (TextView) convertView.findViewById(R.id.fm_album_cell_play_steps);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Album data = mData.get(position);
        Glide.with(mContext).load(data.getCoverUrlSmall()).transform(new GlideRoundTransform(mContext)).into(holder.fmChannelCellImage);
        holder.fmChannelCellTitle.setText(data.getAlbumTitle());
        holder.fmChannelCellContent.setText(data.getAlbumIntro());
        holder.fmChannelCellPlayCount.setText(data.getPlayCount() + "次");
        holder.fmAlbumCellPlaySteps.setText(data.getIncludeTrackCount() + "集");
        return convertView;
    }

    static class ViewHolder {
        ImageView fmChannelCellImage;
        TextView fmChannelCellTitle;
        TextView fmChannelCellContent;
        TextView fmChannelCellPlayCount;
        TextView fmAlbumCellPlaySteps;

        ViewHolder(View view) {

        }
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
