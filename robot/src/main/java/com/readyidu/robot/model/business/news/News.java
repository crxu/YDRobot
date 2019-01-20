package com.readyidu.robot.model.business.news;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * @Autour: wlq
 * @Description: 天气笼子实体类
 * @Date: 2017/10/11 18:25
 * @Update: 2017/10/11 18:25
 * @UpdateRemark:
 * @Version: V1.0
 */
public class News implements Parcelable {

    /**
     * _source : {"new_source":"新浪新闻","new_type":"国内","new_date":1503352500,"new_title":"北京首轮纪检监察巡察启动 进驻4家纪检机构","new_content":"<p>　　原标题：北京首轮纪检监察巡察启动<\/p><p>　　巡察组将进驻4家纪检监察机构，巡察结果作为纪检监察干部考核选拔任用重要依据<\/p><p>　　新京报讯 （记者沙雪良）日前，北京市纪委启动首轮巡察，防止\u201c灯下黑\u201d。巡察组将进驻延庆区纪委区监委等4家纪检监察机构。<\/p><p>　　市纪委建立纪检监察巡察制度<\/p><p>　　今年6月19日，市委书记蔡奇在北京市第十二次党代会上作报告时提出，坚定不移深化政治巡视，创新方式方法，实现届内巡视巡察全覆盖。这也意味着，本届市委将在巡视全覆盖的基础上，做到巡察全覆盖。<\/p><p>　　昨日，北京纪检监察网发布消息，8月18日，北京市第一轮纪检监察巡察正式启动。<\/p><p>　　本轮巡察，巡察组将进驻延庆区纪委区监委、驻市卫生计生委纪检监察组、北京建工集团有限责任公司纪委和首都经济贸易大学纪委等4家纪检监察机构。<\/p><p>　　这是市纪委制定《关于建立纪检监察巡察制度的意见（试行）》后开展的首轮巡察。《意见》出台的宗旨，是为推动全面从严治党向纵深发展，切实加强北京纪检监察系统内部监督，坚决防止\u201c灯下黑\u201d。<\/p><p>　　《意见》规定，在市纪委一届任期内，对六类巡察对象至少巡察一次，做到巡察无空白、监督无例外。这六类巡察对象是：市纪委市监委机关各部门及直属单位、市委巡视办和市委巡视组、市纪委市监委派驻（出）机构、各区纪委区监委、市属国有企业纪委、市属高等院校纪委。<\/p><p>　　发现问题要专题报告严肃问责<\/p><p>　　对于巡视的重点，《意见》提出，开展纪检监察巡察工作，一是突出政治巡察，紧盯纪检监察机构和纪检监察干部政治是否过硬的问题；二是突出自我监督，紧盯\u201c灯下黑\u201d的问题，认真查找纪检监察队伍建设中的不足和纪检监察业务中存在的问题，着力发现纪检监察干部违规违纪问题和线索。<\/p><p>　　在强化\u201c两个突出\u201d的同时，市纪委还将从政治过硬、队伍建设以及履行职责等三个方面进行重点巡察。<\/p><p>　　在推进全覆盖同时，市纪委把巡察成果运用作为重中之重。<\/p><p>　　《意见》明确了三项要求：对巡察中发现的问题，要专题报告、严肃问责、完善机制；巡察结果作为纪检监察机关绩效考核及纪检监察干部考核评价、选拔任用的重要依据；巡察结束后，及时将巡察工作有关情况报市委巡视工作领导小组办公室备案。通过建立巡察结果跟踪机制，避免出现\u201c重结果、轻结果转化\u201d的不良倾向。<\/p><p>　　北京市纪委表示，开展纪检监察巡察工作是实现自我监督的重要措施。同时，也将把自我监督与党内监督、民主监督、群众监督等有机结合起来，以形成发现问题、纠正偏差的有效机制。<\/p><p>责任编辑：刘德宾SN222<\/p>","new_table":["巡察","北京"]}
     * _id : AV4H-FpSvdHr-MPGGuUw
     */

    private SourceBean _source;
    private String _id;

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

    public static class SourceBean implements Parcelable {
        /**
         * new_source : 新浪新闻
         * new_type : 国内
         * new_date : 1503352500
         * new_title : 北京首轮纪检监察巡察启动 进驻4家纪检机构
         * new_content : <p>　　原标题：北京首轮纪检监察巡察启动</p><p>　　巡察组将进驻4家纪检监察机构，巡察结果作为纪检监察干部考核选拔任用重要依据</p><p>　　新京报讯 （记者沙雪良）日前，北京市纪委启动首轮巡察，防止“灯下黑”。巡察组将进驻延庆区纪委区监委等4家纪检监察机构。</p><p>　　市纪委建立纪检监察巡察制度</p><p>　　今年6月19日，市委书记蔡奇在北京市第十二次党代会上作报告时提出，坚定不移深化政治巡视，创新方式方法，实现届内巡视巡察全覆盖。这也意味着，本届市委将在巡视全覆盖的基础上，做到巡察全覆盖。</p><p>　　昨日，北京纪检监察网发布消息，8月18日，北京市第一轮纪检监察巡察正式启动。</p><p>　　本轮巡察，巡察组将进驻延庆区纪委区监委、驻市卫生计生委纪检监察组、北京建工集团有限责任公司纪委和首都经济贸易大学纪委等4家纪检监察机构。</p><p>　　这是市纪委制定《关于建立纪检监察巡察制度的意见（试行）》后开展的首轮巡察。《意见》出台的宗旨，是为推动全面从严治党向纵深发展，切实加强北京纪检监察系统内部监督，坚决防止“灯下黑”。</p><p>　　《意见》规定，在市纪委一届任期内，对六类巡察对象至少巡察一次，做到巡察无空白、监督无例外。这六类巡察对象是：市纪委市监委机关各部门及直属单位、市委巡视办和市委巡视组、市纪委市监委派驻（出）机构、各区纪委区监委、市属国有企业纪委、市属高等院校纪委。</p><p>　　发现问题要专题报告严肃问责</p><p>　　对于巡视的重点，《意见》提出，开展纪检监察巡察工作，一是突出政治巡察，紧盯纪检监察机构和纪检监察干部政治是否过硬的问题；二是突出自我监督，紧盯“灯下黑”的问题，认真查找纪检监察队伍建设中的不足和纪检监察业务中存在的问题，着力发现纪检监察干部违规违纪问题和线索。</p><p>　　在强化“两个突出”的同时，市纪委还将从政治过硬、队伍建设以及履行职责等三个方面进行重点巡察。</p><p>　　在推进全覆盖同时，市纪委把巡察成果运用作为重中之重。</p><p>　　《意见》明确了三项要求：对巡察中发现的问题，要专题报告、严肃问责、完善机制；巡察结果作为纪检监察机关绩效考核及纪检监察干部考核评价、选拔任用的重要依据；巡察结束后，及时将巡察工作有关情况报市委巡视工作领导小组办公室备案。通过建立巡察结果跟踪机制，避免出现“重结果、轻结果转化”的不良倾向。</p><p>　　北京市纪委表示，开展纪检监察巡察工作是实现自我监督的重要措施。同时，也将把自我监督与党内监督、民主监督、群众监督等有机结合起来，以形成发现问题、纠正偏差的有效机制。</p><p>责任编辑：刘德宾SN222</p>
         * new_table : ["巡察","北京"]
         */

        private String new_source;
        private String new_type;
        private long new_date;
        private String new_img;
        private String new_title;
        private String new_content;
        private List<String> new_table;

        public String getNew_img() {
            return new_img;
        }

        public void setNew_img(String new_img) {
            this.new_img = new_img;
        }

        public String getNew_source() {
            return new_source;
        }

        public void setNew_source(String new_source) {
            this.new_source = new_source;
        }

        public String getNew_type() {
            return new_type;
        }

        public void setNew_type(String new_type) {
            this.new_type = new_type;
        }

        public long getNew_date() {
            return new_date;
        }

        public void setNew_date(long new_date) {
            this.new_date = new_date;
        }

        public String getNew_title() {
            return new_title;
        }

        public void setNew_title(String new_title) {
            this.new_title = new_title;
        }

        public String getNew_content() {
            return new_content;
        }

        public void setNew_content(String new_content) {
            this.new_content = new_content;
        }

        public List<String> getNew_table() {
            return new_table;
        }

        public void setNew_table(List<String> new_table) {
            this.new_table = new_table;
        }

        public SourceBean() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.new_source);
            dest.writeString(this.new_type);
            dest.writeLong(this.new_date);
            dest.writeString(this.new_img);
            dest.writeString(this.new_title);
            dest.writeString(this.new_content);
            dest.writeStringList(this.new_table);
        }

        protected SourceBean(Parcel in) {
            this.new_source = in.readString();
            this.new_type = in.readString();
            this.new_date = in.readLong();
            this.new_img = in.readString();
            this.new_title = in.readString();
            this.new_content = in.readString();
            this.new_table = in.createStringArrayList();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this._source, flags);
        dest.writeString(this._id);
    }

    public News() {
    }

    protected News(Parcel in) {
        this._source = in.readParcelable(SourceBean.class.getClassLoader());
        this._id = in.readString();
    }

    public static final Parcelable.Creator<News> CREATOR = new Parcelable.Creator<News>() {
        @Override
        public News createFromParcel(Parcel source) {
            return new News(source);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };
}
