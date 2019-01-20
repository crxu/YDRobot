package com.readyidu.robot.model.business.news;

import java.io.Serializable;
import java.util.List;

/**
 * @author Wlq
 * @description 视频新闻详情
 * @date 2017/12/15 上午10:33
 */
public class VideoNewsDetail implements Serializable {

    /**
     * _source : {"vnews_date":1513260036,"vnews_title":"90岁旅美华人\u201c船王\u201d赵锡成：希望中国能够\u201c强起来\u201d",
     * "vnews_table":["旅美华人","船王"],"vnews_source":"中新视频","id":"v2_news746510","vnews_type":"今日推荐",
     * "vnews_url":"http://video.chinanews.com/flv/2017/12/14/400/89773_web.mp4",
     * "vnews_img":"http://i2.chinanews.com/simg/reportmanuscript/2017/12-14/89773_big.jpg"}
     * _id : v2_news746510
     */
    private boolean isTip;
    private String tipContent;
    private SourceBean _source;
    private String _id;

    public boolean isTip() {
        return isTip;
    }

    public void setTip(boolean tip) {
        isTip = tip;
    }

    public String getTipContent() {
        return tipContent;
    }

    public void setTipContent(String tipContent) {
        this.tipContent = tipContent;
    }

    public SourceBean get_source() {
        return _source;
    }

    public void set_source(SourceBean _source) {
        this._source = _source;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public static class SourceBean implements Serializable {
        /**
         * vnews_date : 1513260036
         * vnews_title : 90岁旅美华人“船王”赵锡成：希望中国能够“强起来”
         * vnews_table : ["旅美华人","船王"]
         * vnews_source : 中新视频
         * id : v2_news746510
         * vnews_type : 今日推荐
         * vnews_url : http://video.chinanews.com/flv/2017/12/14/400/89773_web.mp4
         * vnews_img : http://i2.chinanews.com/simg/reportmanuscript/2017/12-14/89773_big.jpg
         */

        private int vnews_date;
        private String vnews_title;
        private String vnews_source;
        private String vnews_blogo;
        private String id;
        private String vnews_url;
        private String vnews_img;
        private List<String> vnews_table;

        public int getVnews_date() {
            return vnews_date;
        }

        public void setVnews_date(int vnews_date) {
            this.vnews_date = vnews_date;
        }

        public String getVnews_title() {
            return vnews_title;
        }

        public void setVnews_title(String vnews_title) {
            this.vnews_title = vnews_title;
        }

        public String getVnews_source() {
            return vnews_source;
        }

        public void setVnews_source(String vnews_source) {
            this.vnews_source = vnews_source;
        }

        public String getVnews_blogo() {
            return vnews_blogo;
        }

        public void setVnews_blogo(String vnews_blogo) {
            this.vnews_blogo = vnews_blogo;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getVnews_url() {
            return vnews_url;
        }

        public void setVnews_url(String vnews_url) {
            this.vnews_url = vnews_url;
        }

        public String getVnews_img() {
            return vnews_img;
        }

        public void setVnews_img(String vnews_img) {
            this.vnews_img = vnews_img;
        }

        public List<String> getVnews_table() {
            return vnews_table;
        }

        public void setVnews_table(List<String> vnews_table) {
            this.vnews_table = vnews_table;
        }
    }
}
