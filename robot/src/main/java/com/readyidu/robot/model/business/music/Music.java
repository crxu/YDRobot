package com.readyidu.robot.model.business.music;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * @Autour: wlq
 * @Description: 音乐笼子实体类
 * @Date: 2017/10/11 18:22
 * @Update: 2017/10/11 18:22
 * @UpdateRemark: 音乐笼子实体类
 * @Version: V1.0
 */
public class Music implements Parcelable {

    /**
     * _source : {"music_n":"斑马,斑马","singer_name":"宋冬野","music_s":["民谣"],"music_style":[],"music_mv":"","singer_id":"59859914","album_name":"安和桥北","singer_pic":"http://7xpkhu.com2.z0.glb.qiniucdn.com/1501559841143-84e898be","music_id":"71266335","music_lan":"国语","album_pic":"http://7xpkhu.com2.z0.glb.qiniucdn.com/1501559842696-85d574c2","music_insts":[],"album_id":"71265988","lyrics":"http://musicdata.baidu.com/data2/lrc/240798334/240798334.lrc"}
     * _id : 71266335
     */
    private SourceBean _source;
    private String _id;
    private boolean isDownloadLink; //是否已下载播放连接
    private int file_duration;
    private String file_link;

    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public SourceBean get_source() {
        return _source;
    }

    public static class SourceBean implements Parcelable {
        /**
         * music_n : 斑马,斑马
         * singer_name : 宋冬野
         * music_s : ["民谣"]
         * music_style : []
         * music_mv :
         * singer_id : 59859914
         * album_name : 安和桥北
         * singer_pic : http://7xpkhu.com2.z0.glb.qiniucdn.com/1501559841143-84e898be
         * music_id : 71266335
         * music_lan : 国语
         * album_pic : http://7xpkhu.com2.z0.glb.qiniucdn.com/1501559842696-85d574c2
         * music_insts : []
         * album_id : 71265988
         * lyrics : http://musicdata.baidu.com/data2/lrc/240798334/240798334.lrc
         */

        private String music_n;
        private String singer_name;
        private String music_mv;
        private String singer_id;
        private String album_name;
        private String singer_pic;
        private String music_id;
        private String music_lan;
        private String album_pic;
        private String album_id;
        private String lyrics;
        private List<String> music_s;

        public String getMusic_n() {
            return music_n;
        }

        public String getSinger_name() {
            return singer_name;
        }

        public String getSinger_id() {
            return singer_id;
        }

        public String getSinger_pic() {
            return singer_pic;
        }

        public String getMusic_id() {
            return music_id;
        }

        public String getAlbum_pic() {
            return album_pic;
        }

        public String getLyrics() {
            return lyrics;
        }

        public SourceBean() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.music_n);
            dest.writeString(this.singer_name);
            dest.writeString(this.music_mv);
            dest.writeString(this.singer_id);
            dest.writeString(this.album_name);
            dest.writeString(this.singer_pic);
            dest.writeString(this.music_id);
            dest.writeString(this.music_lan);
            dest.writeString(this.album_pic);
            dest.writeString(this.album_id);
            dest.writeString(this.lyrics);
            dest.writeStringList(this.music_s);
        }

        protected SourceBean(Parcel in) {
            this.music_n = in.readString();
            this.singer_name = in.readString();
            this.music_mv = in.readString();
            this.singer_id = in.readString();
            this.album_name = in.readString();
            this.singer_pic = in.readString();
            this.music_id = in.readString();
            this.music_lan = in.readString();
            this.album_pic = in.readString();
            this.album_id = in.readString();
            this.lyrics = in.readString();
            this.music_s = in.createStringArrayList();
        }

        public static final Creator<SourceBean> CREATOR = new Creator<SourceBean>() {
            @Override
            public SourceBean createFromParcel(Parcel source) {
                return new SourceBean(source);
            }

            @Override
            public SourceBean[] newArray(int size) {
                return new SourceBean[size];
            }
        };
    }


    public Music() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this._source, flags);
        dest.writeString(this._id);
        dest.writeByte(this.isDownloadLink ? (byte) 1 : (byte) 0);
        dest.writeInt(this.file_duration);
        dest.writeString(this.file_link);
        dest.writeString(this.path);
    }

    protected Music(Parcel in) {
        this._source = in.readParcelable(SourceBean.class.getClassLoader());
        this._id = in.readString();
        this.isDownloadLink = in.readByte() != 0;
        this.file_duration = in.readInt();
        this.file_link = in.readString();
        this.path = in.readString();
    }

    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel source) {
            return new Music(source);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };
}
