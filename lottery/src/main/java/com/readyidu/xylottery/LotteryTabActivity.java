package com.readyidu.xylottery;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.readyidu.api.response.LotteryMenu;
import com.readyidu.base.BaseActivity;
import com.readyidu.fragment.LotteryFragment;
import com.readyidu.utils.UIhelp;
import com.readyidu.view.DownLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class LotteryTabActivity extends BaseActivity {

    private Toolbar             mTbToolbar;
    private TabLayout           mTlTab;
    private ViewPager           mVpContent;
    private List<Fragment>      tabFragments;
    private List<LotteryMenu>   menuList = new ArrayList<>();
    private String[]            tabs;
    private ContentPagerAdapter contentAdapter;
    private ImageView           mDownimg;
    private DownLayout          mDownlayout;
    private boolean             is_show  = true;

    protected int initView() {
        return R.layout.activity_lottery_tab;
    }

    protected void bindView() {
        mTlTab = (TabLayout) findViewById(R.id.tl_tab);
        mVpContent = (ViewPager) findViewById(R.id.vp_content);
        mDownimg = (ImageView) findViewById(R.id.downimg);
        mDownlayout = (DownLayout) findViewById(R.id.downlayout);
        mDownimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_show) {
                    UIhelp.show(LotteryTabActivity.this, mDownlayout);
                    is_show = false;
                } else {
                    UIhelp.hide(LotteryTabActivity.this);
                    is_show = true;
                }

            }
        });

        initContent();
        initTab();
        initToolbar();
    }

    private void initToolbar() {
        mTbToolbar = (Toolbar) findViewById(R.id.tb_toolbar);
        if (mTbToolbar != null) {
            setSupportActionBar(mTbToolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
        mTbToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initTab() {
        mTlTab.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTlTab.setTabTextColors(ContextCompat.getColor(this, android.R.color.black),
                ContextCompat.getColor(this, R.color.tabs_bg));
        mTlTab.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.tabs_bg));
        ViewCompat.setElevation(mTlTab, 10);
        mTlTab.setupWithViewPager(mVpContent);
    }

    private void initContent() {
        tabs = this.getResources().getStringArray(R.array.tabs);
        addMenuData();
        tabFragments = new ArrayList<>();
        for (LotteryMenu lotteryMenu : menuList) {
            tabFragments.add(LotteryFragment.newInstance(lotteryMenu));
        }
        contentAdapter = new ContentPagerAdapter(getSupportFragmentManager());
        mVpContent.setAdapter(contentAdapter);
        mTlTab.setupWithViewPager(mVpContent);
    }

    public void addMenuData() {
        menuList.clear();
        for (int i = 0; i < tabs.length; i++) {
            menuList.add(new LotteryMenu(tabs[i], i + 1, false));
        }
    }

    class ContentPagerAdapter extends FragmentPagerAdapter {

        public ContentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return tabFragments.get(position);
        }

        @Override
        public int getCount() {
            return tabFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }

    }

    private int getbytabsindex(String indexs) {
        for (int i = 0; i < tabs.length; i++) {
            if (indexs.equalsIgnoreCase(tabs[i])) {
                return i;
            }
        }
        return 0;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        String indexs = messageEvent.getMessage();
        int index = getbytabsindex(indexs);
        mVpContent.setCurrentItem(index);
    }

}
