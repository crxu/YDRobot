package com.readyidu.robot.component.router.model;


import java.util.List;

public class TvRouterModel {

    private List<TvAppRouterBean> tv_app_router;

    public List<TvAppRouterBean> getTv_app_router() {
        return tv_app_router;
    }

    public void setTv_app_router(List<TvAppRouterBean> tv_app_router) {
        this.tv_app_router = tv_app_router;
    }

    public class TvAppRouterBean {
        /**
         * tvName : 健康、身体状况
         * tvNickName : 健康页
         * isSys: 是否是系统命令
         * tvId : 10001
         */
        private String tvName;
        private String tvNickName;
        private int tvId;

        public String getTvName() {
            return tvName;
        }

        public void setTvName(String tvName) {
            this.tvName = tvName;
        }

        public String getTvNickName() {
            return tvNickName;
        }

        public void setTvNickName(String tvNickName) {
            this.tvNickName = tvNickName;
        }

        public int getTvId() {
            return tvId;
        }

        public void setTvId(int tvId) {
            this.tvId = tvId;
        }
    }
}
