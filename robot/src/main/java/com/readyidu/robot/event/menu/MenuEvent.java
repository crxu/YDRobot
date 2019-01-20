package com.readyidu.robot.event.menu;

import com.readyidu.robot.model.business.menu.Menu;

import java.util.List;

/**
 * @Autour: wlq
 * @Description: 菜谱相关事件
 * @Date: 2017/10/14 14:26
 * @Update: 2017/10/14 14:26
 * @UpdateRemark: 菜谱相关事件
 * @Version: V1.0
*/
public class MenuEvent {

    public List<Menu> mMenus;
    public String mSearchKey;
    public String mKeyword;
    public int total;

    public MenuEvent(List<Menu> menus, String searchKey, String keyword) {
        this.mMenus = menus;
        this.mSearchKey = searchKey;
        this.mKeyword = keyword;
    }

    public MenuEvent(List<Menu> mMenus, String mSearchKey, String mKeyword, int total) {
        this.mMenus = mMenus;
        this.mSearchKey = mSearchKey;
        this.mKeyword = mKeyword;
        this.total = total;
    }
}
