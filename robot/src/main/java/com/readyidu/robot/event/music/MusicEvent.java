package com.readyidu.robot.event.music;

import com.readyidu.robot.model.business.music.Music;

/**
 * @Autour: wlq
 * @Description: 音乐相关事件
 * @Date: 2017/10/14 14:26
 * @Update: 2017/10/14 14:26
 * @UpdateRemark:
 * @Version: V1.0
 */
public class MusicEvent {

    public static class SwitchMusic {

        public Music music;

        public SwitchMusic(Music music) {
            this.music = music;
        }
    }

    public static class MusicLrc {

        public int duration;

        public MusicLrc(int duration) {
            this.duration = duration;
        }
    }

    public static class MusicState {

        public int playState;   //播放状态：0 播放；1 暂停；2恢复；3 停止

        public MusicState(int playState) {
            this.playState = playState;
        }
    }

    /**
     * 播放进度
     */
    public static class MusicPlayProgress {

        public int currentPosition = 0;//当前进度
        public int duration = 0;//总时长

        public MusicPlayProgress(int currentPosition, int duration) {
            this.currentPosition = currentPosition;
            this.duration = duration;
        }
    }

    /**
     * 获取播放状态
     */
    public static class MusicIsPlaying {

        public boolean isPlaying;

        public MusicIsPlaying(boolean isPlaying) {
            this.isPlaying = isPlaying;
        }
    }

    /**
     * 获取正在播放中的音乐信息
     */
    public static class MusicInfo {
        public Object music;

        public MusicInfo(Object music) {
            this.music = music;
        }
    }


}
