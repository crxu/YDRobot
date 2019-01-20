package com.readyidu.robot.ui.music.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * @Autour: wlq
 * @Description: 音乐播放Fragment
 * @Date: 2017/10/14 18:17 
 * @Update: 2017/10/14 18:17
 * @UpdateRemark: 音乐播放Fragment
 * @Version:
*/
public class MusicFragmentAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;

    public MusicFragmentAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
