package com.readyidu.robot.model.business.menu;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * @Autour: wlq
 * @Description: 菜谱笼子实体类
 * @Date: 2017/10/11 18:13
 * @Update: 2017/10/11 18:13
 * @UpdateRemark:
 * @Version: V1.0
 */
public class Menu implements Serializable, Parcelable {

    private SourceBean _source;
    private String _id;

    public SourceBean get_source() {
        return _source;
    }

    public static class SourceBean implements Serializable {

        private String menu_n;
        private String menu_image;
        private String menu_fav;
        private String menu_id;
        private List<?> menu_f;
        private List<?> menu_e;
        private List<?> menu_d;
        private List<MeterialBean> meterial;
        private List<?> menu_s;
        private List<?> menu_p;
        private List<?> menu_w;
        private List<?> menu_t;
        private List<StepBean> step;
        private String describe;
        private String[] tip;

        public String getDescribe() {
            return describe;
        }

        public String[] getTip() {
            return tip;
        }

        public String getMenu_n() {
            return menu_n;
        }

        public String getMenu_image() {
            return menu_image;
        }

        public String getMenu_fav() {
            return menu_fav;
        }

        public String getMenu_id() {
            return menu_id;
        }

        public List<MeterialBean> getMeterial() {
            return meterial;
        }

        public List<StepBean> getStep() {
            return step;
        }

        public static class MeterialBean implements Serializable {
            /**
             * name : 鸡蛋黄
             * value : 5个
             */

            private String name;
            private String value;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }

        public static class StepBean implements Serializable {
            /**
             * image : http://7xpkhu.com2.z0.glb.qiniucdn.com/1501228300341-9730f2fc
             * detail : 1 准备好所有材料
             * order : 1
             */

            private String image;
            private String detail;
            private String order;

            public String getImage() {
                return image;
            }

            public String getDetail() {
                return detail;
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this._source);
        dest.writeString(this._id);
    }

    public Menu() {
    }

    protected Menu(Parcel in) {
        this._source = (SourceBean) in.readSerializable();
        this._id = in.readString();
    }

    public static final Parcelable.Creator<Menu> CREATOR = new Parcelable.Creator<Menu>() {
        @Override
        public Menu createFromParcel(Parcel source) {
            return new Menu(source);
        }

        @Override
        public Menu[] newArray(int size) {
            return new Menu[size];
        }
    };
}
