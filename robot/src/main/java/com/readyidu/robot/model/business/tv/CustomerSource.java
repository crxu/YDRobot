package com.readyidu.robot.model.business.tv;

/**
 * Created by gx on 2017/12/25.
 */
public class CustomerSource {
    private boolean isSelect;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public void toggleSelece() {
        isSelect = !isSelect;
    }
}