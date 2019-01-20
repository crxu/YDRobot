package com.readyidu.robot.model.business.tv;

import com.readyidu.robot.utils.SPUtils;

/**
 * Created by gx on 2017/12/27.
 */
public class CheckModel {
    private String confUrl;//无自建 此参数空串	string
    private String defineName;//无自建 此参数空字符串	string
    private boolean isBindling;//是否绑定 绑定“true” 未绑定“false”	string
    private boolean isDefine;//是否有自建 有“true” 未自建“false”	string

    public String getConfUrl() {
        return confUrl;
    }

    public void setConfUrl(String confUrl) {
        this.confUrl = confUrl;
    }

    public String getDefineName() {
        return defineName;
    }

    public void setDefineName(String defineName) {
        this.defineName = defineName;
    }

    public boolean isBindling() {
        return isBindling;
    }

    public void setBindling(boolean bindling) {
        isBindling = bindling;
        SPUtils.put("tvBinded", bindling);
    }

    public boolean isDefine() {
        return isDefine;
    }

    public void setDefine(boolean define) {
        isDefine = define;
    }
}