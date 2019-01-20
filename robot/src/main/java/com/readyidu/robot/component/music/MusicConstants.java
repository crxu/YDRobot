package com.readyidu.robot.component.music;

/**
 * @Autour: wlq
 * @Description: 音乐相关常量
 * @Date: 2017/10/14 20:36
 * @Update: 2017/10/14 20:36
 * @UpdateRemark: 音乐相关常量
 * @Version: V1.0
 */
public final class MusicConstants {

    //音乐
    public static String ACTION_MUSIC_TOGGLE_PLAY = "action_music_toggle_play";
    public static String ACTION_MUSIC_PAUSE_PLAY = "action_music_pause_play";
    public static String ACTION_MUSIC_UP = "action_music_up";
    public static String ACTION_MUSIC_NEXT = "action_music_next";
    public static String ACTION_MUSIC_SINGLE_PLAY = "action_music_single_play";
    public static String ACTION_MUSIC_SET_PROGRESS = "action_music_set_progress";
    public static String ACTION_MUSIC_GET_PROGRESS = "action_music_get_progress";
    public static String ACTION_MUSIC_RELEASE = "action_music_release";
    public static String ACTION_MUSIC_IS_PLYING = "action_music_is_playing";
    public static String ACTION_MUSIC_GET_MUSIC_INFO = "action_music_get_music_info";

    public static final String MUSIC_PROGRESS = "music_progress";
    public static final String MUSIC_CURRENT_ID = "music_current_id";
    public static final String MUSIC_PLAYING_ID = "music_playing_id";

    public class PlayMode {
        
        public static final int SINGLE = 0;     //单曲循环

        public static final int ORDER = 1;  //顺序播放

        public static final int RANDOM = 2; //随机播放
    }
}