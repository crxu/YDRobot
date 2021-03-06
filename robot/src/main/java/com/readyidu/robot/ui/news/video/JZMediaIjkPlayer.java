package com.readyidu.robot.ui.news.video;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.Surface;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;

/**
 * @author Wlq
 * @description 实现ijk播放器
 * @date 2017/12/29 上午11:36
 */
public class JZMediaIjkPlayer extends JZMediaInterface implements IMediaPlayer.OnPreparedListener,
        IMediaPlayer.OnVideoSizeChangedListener, IMediaPlayer.OnCompletionListener,
        IMediaPlayer.OnErrorListener, IMediaPlayer.OnInfoListener,
        IMediaPlayer.OnBufferingUpdateListener,
        IMediaPlayer.OnSeekCompleteListener, IMediaPlayer.OnTimedTextListener {

    private IjkMediaPlayer ijkMediaPlayer;

    @Override
    public void start() {
        try {
            if (null != ijkMediaPlayer)
                ijkMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void prepare() {
        ijkMediaPlayer = new IjkMediaPlayer();
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);

        ijkMediaPlayer.setOnPreparedListener(JZMediaIjkPlayer.this);
        ijkMediaPlayer.setOnVideoSizeChangedListener(JZMediaIjkPlayer.this);
        ijkMediaPlayer.setOnCompletionListener(JZMediaIjkPlayer.this);
        ijkMediaPlayer.setOnErrorListener(JZMediaIjkPlayer.this);
        ijkMediaPlayer.setOnInfoListener(JZMediaIjkPlayer.this);
        ijkMediaPlayer.setOnBufferingUpdateListener(JZMediaIjkPlayer.this);
        ijkMediaPlayer.setOnSeekCompleteListener(JZMediaIjkPlayer.this);
        ijkMediaPlayer.setOnTimedTextListener(JZMediaIjkPlayer.this);

        try {
            ijkMediaPlayer.setDataSource(currentDataSource.toString());
            ijkMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            ijkMediaPlayer.setScreenOnWhilePlaying(true);
            ijkMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pause() {
        try {
            if (null != ijkMediaPlayer)
                ijkMediaPlayer.pause();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isPlaying() {
        try {
            return ijkMediaPlayer.isPlaying();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void seekTo(long time) {
        try {

            if (null != ijkMediaPlayer)
                ijkMediaPlayer.seekTo(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void release() {
        try {

            if (ijkMediaPlayer != null)
                ijkMediaPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public long getCurrentPosition() {
        if (ijkMediaPlayer != null) {
            return ijkMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public long getDuration() {
        return ijkMediaPlayer.getDuration();
    }

    @Override
    public void setSurface(Surface surface) {
        try {
            ijkMediaPlayer.setSurface(surface);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        try {
            ijkMediaPlayer.start();
            if (currentDataSource.toString().toLowerCase().contains("mp3")) {
                JZMediaManager.instance().mainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (JZVideoPlayerManager.getCurrentJzvd() != null) {
                            JZVideoPlayerManager.getCurrentJzvd().onPrepared();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
        JZMediaManager.instance().currentVideoWidth = iMediaPlayer.getVideoWidth();
        JZMediaManager.instance().currentVideoHeight = iMediaPlayer.getVideoHeight();
        JZMediaManager.instance().mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JZVideoPlayerManager.getCurrentJzvd() != null) {
                    JZVideoPlayerManager.getCurrentJzvd().onVideoSizeChanged();
                }
            }
        });
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        JZMediaManager.instance().mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JZVideoPlayerManager.getCurrentJzvd() != null) {
                    JZVideoPlayerManager.getCurrentJzvd().onAutoCompletion();
                }
            }
        });
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, final int what, final int extra) {
        JZMediaManager.instance().mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JZVideoPlayerManager.getCurrentJzvd() != null) {
                    JZVideoPlayerManager.getCurrentJzvd().onError(what, extra);
                }
            }
        });
        return true;
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, final int what, final int extra) {
        JZMediaManager.instance().mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JZVideoPlayerManager.getCurrentJzvd() != null) {
                    if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                        JZVideoPlayerManager.getCurrentJzvd().onPrepared();
                    } else {
                        JZVideoPlayerManager.getCurrentJzvd().onInfo(what, extra);
                    }
                }
            }
        });
        return false;
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, final int percent) {
        JZMediaManager.instance().mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JZVideoPlayerManager.getCurrentJzvd() != null) {
                    JZVideoPlayerManager.getCurrentJzvd().setBufferProgress(percent);
                }
            }
        });
    }

    @Override
    public void onSeekComplete(IMediaPlayer iMediaPlayer) {
        JZMediaManager.instance().mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JZVideoPlayerManager.getCurrentJzvd() != null) {
                    JZVideoPlayerManager.getCurrentJzvd().onSeekComplete();
                }
            }
        });
    }

    @Override
    public void onTimedText(IMediaPlayer iMediaPlayer, IjkTimedText ijkTimedText) {}
}
