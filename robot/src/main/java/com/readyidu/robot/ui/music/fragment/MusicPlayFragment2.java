package com.readyidu.robot.ui.music.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.readyidu.robot.R;
import com.readyidu.robot.api.HttpHelper;
import com.readyidu.robot.api.config.FolderConfig;
import com.readyidu.robot.base.BaseFragment;
import com.readyidu.robot.component.music.MusicRecord;
import com.readyidu.robot.component.music.lrc.ILrcView;
import com.readyidu.robot.component.music.lrc.LrcDataBuilder;
import com.readyidu.robot.component.music.lrc.LrcRow;
import com.readyidu.robot.component.music.lrc.LrcView;
import com.readyidu.robot.event.music.MusicEvent;
import com.readyidu.robot.model.business.music.Music;
import com.readyidu.robot.utils.log.LogUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.reactivex.functions.Consumer;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @Autour: wlq
 * @Description: 音乐播放歌词
 * @Date: 2017/10/14 18:08
 * @Update: 2017/10/14 18:08
 * @UpdateRemark: 音乐播放歌词
 * @Version:
 */
public class MusicPlayFragment2 extends BaseFragment {

    private LrcView mLrcView;
    private RelativeLayout mRlLrc;
    private Music mMusic;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_music_play2;
    }

    @Override
    protected void bindViews(View contentView) {
        mRlLrc = (RelativeLayout)contentView.findViewById(R.id.rl_lrc_view);
        mLrcView = new LrcView(mContext);
        mRlLrc.addView(mLrcView);
        mLrcView.setEmptyMessage();
    }

    @Override
    protected void bindEvents() {}

    @Override
    public void onResume() {
        super.onResume();

        try {
            String lrcUrl = MusicRecord.getInstance().getCurrentData().get_source().getLyrics();
            if (mLrcView == null) {
                mLrcView = new LrcView(mContext);
                mRlLrc.addView(mLrcView);
                mLrcView.setEmptyMessage();
            }
            requestLrc(lrcUrl);
            registerRxBus(MusicEvent.SwitchMusic.class, new Consumer<MusicEvent.SwitchMusic>() {
                @Override
                public void accept(MusicEvent.SwitchMusic switchMusic) throws Exception {
                    if (null != mLrcView) {
                        mRlLrc.removeAllViews();
                        mLrcView = null;
                    }
                    mLrcView = new LrcView(mContext);
                    mRlLrc.addView(mLrcView);
                    mLrcView.setEmptyMessage();
                    mMusic = switchMusic.music;
                    requestLrc(mMusic.get_source().getLyrics());
                }
            });
            registerRxBus(MusicEvent.MusicLrc.class, new Consumer<MusicEvent.MusicLrc>() {
                @Override
                public void accept(MusicEvent.MusicLrc musicLrc) throws Exception {
                    if (null != mLrcView) {
                        mLrcView.seekLrcToTime(musicLrc.duration);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestLrc(final String url) {
        if (!TextUtils.isEmpty(url)) {
            String fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
            File file = new File(FolderConfig.FOLDER_LRC_TEMP, fileName);
            if (!file.exists()) {
                HttpHelper.downloadFileInit(url)
                        .downloadRobotMusicLrc(url)
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    String fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
                                    if (saveLrc(fileName, response.body())) {
                                        loadLrcFile(new File(FolderConfig.FOLDER_LRC_TEMP, fileName));
                                    }
                                } else {
                                    LogUtils.e("server contact failed");
                                    try {
                                        mLrcView.setEmptyMessage();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                LogUtils.e(t);
                                try {
                                    mLrcView.setEmptyMessage();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            } else {
                loadLrcFile(file);
            }
        }
    }

    private void loadLrcFile(File file) {
        List<LrcRow> lrcRows = new LrcDataBuilder().Buile(file);

        if (null != mLrcView) {
            mLrcView.setLrcData(lrcRows);

            // 设置自定义的LrcView上下拖动歌词时监听
            mLrcView.setLrcViewSeekListener(new ILrcView.LrcViewSeekListener() {
                @Override
                public void onSeek(LrcRow currentlrcrow, long Currenselectrowtime) {

                }
            });
        }
    }

    /**
     * 保存歌词至本地
     * @param fileName
     * @param body
     * @return
     */
    private boolean saveLrc(String fileName, ResponseBody body) {
        try {
            InputStream is = body.byteStream();
            File tempFile = new File(FolderConfig.FOLDER_LRC_TEMP);
            if (!tempFile.exists()) {
                tempFile.mkdir();
            }
            FileOutputStream fos = new FileOutputStream(FolderConfig.FOLDER_LRC_TEMP + File.separator + fileName);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                fos.flush();
            }
            fos.close();
            bis.close();
            is.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        mLrcView = null;
    }
}
