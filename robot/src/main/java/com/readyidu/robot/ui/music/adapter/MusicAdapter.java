package com.readyidu.robot.ui.music.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.readyidu.robot.R;
import com.readyidu.robot.component.music.MusicRecord;
import com.readyidu.robot.model.business.music.Music;
import com.readyidu.robot.ui.adapter.common.CommonAdapter;
import com.readyidu.robot.ui.adapter.common.base.ViewHolder;
import com.readyidu.robot.ui.music.activity.MusicPlayActivity;
import com.readyidu.robot.utils.event.EventUtils;
import com.readyidu.robot.utils.glide.GlideUtils;

import java.util.List;

/**
 * @Autour: wlq
 * @Description: 音乐列表
 * @Date: 2017/10/12 13:51
 * @Update: 2017/10/12
 * @UpdateRemark:
 * @Version: V1.0
 */
public class MusicAdapter extends CommonAdapter<Music> {

    private boolean isSDKOutside;

    public MusicAdapter(Context context, List<Music> musics, boolean isSDKOutside) {
        super(context, R.layout.item_music, musics);
        this.isSDKOutside = isSDKOutside;
    }

    @Override
    public void convert(final ViewHolder holder, final Music music, final int position) {
        final Context context = holder.getConvertView().getContext();
        TextView tvName = holder.getView(R.id.tv_item_music_name);
        TextView tvSinger = holder.getView(R.id.tv_item_music_author);

        Music.SourceBean sourceBean = music.get_source();
        tvName.setText(sourceBean.getMusic_n());
        tvSinger.setText(sourceBean.getSinger_name());
        final String music_id = sourceBean.getMusic_id();

        if (music_id.equals(MusicRecord.getInstance().getMusicId())) {
            tvName.setTextColor(context.getResources().getColor(R.color.theme_color_tran_10));
            tvSinger.setTextColor(context.getResources().getColor(R.color.theme_color_tran_10));

            GlideUtils.loadMusicHeadImageRes(context, R.drawable.ic_playing,
                    (ImageView) holder.getView(R.id.iv_item_music_head));
        } else {
            tvName.setTextColor(context.getResources().getColor(R.color.content_color));
            tvSinger.setTextColor(context.getResources().getColor(R.color.tip_color));

            String url = sourceBean.getAlbum_pic();
            if (TextUtils.isEmpty(url)) {
                GlideUtils.loadMusicHeadImageRes(context, R.drawable.ic_music_play_head_default_background,
                        (ImageView) holder.getView(R.id.iv_item_music_head));
            } else {
                GlideUtils.loadMusicSingerImageNet(context,url, (ImageView) holder.getView(R.id.iv_item_music_head));
            }
        }

        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!EventUtils.isFastDoubleClick(v.getId())) {

                    MusicRecord.getInstance().setmMusics();
                    MusicRecord.getInstance().setCurrentMusic(music);

                    Intent intent = new Intent(context, MusicPlayActivity.class);
                    if (music_id.equals(MusicRecord.getInstance().getMusicId())) {
                        intent.putExtra("isPlaying", true);
                    }
                    intent.putExtra("isSDKOutside", isSDKOutside);
                    context.startActivity(intent);
                }
            }
        });
    }
}