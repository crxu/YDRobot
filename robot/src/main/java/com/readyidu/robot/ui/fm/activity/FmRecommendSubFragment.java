package com.readyidu.robot.ui.fm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.readyidu.basic.base.BaseFragment;
import com.readyidu.robot.R;
import com.readyidu.robot.ui.fm.adapetr.FmAlbumAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;

/**
 * Created by yuzhang on 2018/5/24.
 */

public class FmRecommendSubFragment extends BaseFragment {
    ListView fmSubList;
    SmartRefreshLayout fmSubRefresh;
    private int page;

    private FmAlbumAdapter mFmAlbumAdapter;

    private long id;


    @Override
    protected int getLayoutId() {
        return R.layout.fm_sub_layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        id = getArguments().getLong("id");

        fmSubRefresh = (SmartRefreshLayout) view.findViewById(R.id.fm_sub_refresh);

        fmSubList = (ListView) view.findViewById(R.id.fm_sub_list);
        mFmAlbumAdapter = new FmAlbumAdapter(getContext());
        fmSubList.setAdapter(mFmAlbumAdapter);
        fmSubList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent detailIntent = new Intent(FmRecommendSubFragment.this.getContext(), FmAlbumDetailActivity.class);
                detailIntent.putExtra("album", mFmAlbumAdapter.getData().get(position));
                detailIntent.putExtra("albumId", String.valueOf(mFmAlbumAdapter.getData().get(position).getId()));
                detailIntent.putExtra("nickname", mFmAlbumAdapter.getData().get(position).getAnnouncer().getNickname());
                detailIntent.putExtra("avatar", mFmAlbumAdapter.getData().get(position).getAnnouncer().getAvatarUrl());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd更新");
                detailIntent.putExtra("updatedAt", sdf.format(mFmAlbumAdapter.getData().get(position).getUpdatedAt()));
                detailIntent.putExtra("playConnt", mFmAlbumAdapter.getData().get(position).getPlayCount() + "次");
                detailIntent.putExtra("trackCount", mFmAlbumAdapter.getData().get(position).getIncludeTrackCount() + "集");
                startActivity(detailIntent);
            }
        });


        fmSubRefresh.setEnableLoadmore(true);
        fmSubRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                getAlbum();
            }
        });
        fmSubRefresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                page++;
                getAlbum();
            }
        });
        page = 1;
        getAlbum();
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    private void getAlbum() {
        if (id == -1) {
            Map<String, String> map = new HashMap<String, String>();
            map.put(DTransferConstants.LIKE_COUNT, "50");
            CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
                @Override
                public void onSuccess(@Nullable GussLikeAlbumList gussLikeAlbumList) {
                    if (page == 1) {
                        mFmAlbumAdapter.setData(gussLikeAlbumList.getAlbumList());
                    } else {
                        mFmAlbumAdapter.addData(gussLikeAlbumList.getAlbumList());
                    }
                    mFmAlbumAdapter.notifyDataSetChanged();
                    fmSubRefresh.finishRefresh();
                    fmSubRefresh.finishLoadmore();
                }

                @Override
                public void onError(int i, String s) {
                    fmSubRefresh.finishRefresh();
                    fmSubRefresh.finishLoadmore();
                }
            });
        } else {
            Map<String, String> map = new HashMap<String, String>();
            map.put(DTransferConstants.CATEGORY_ID, String.valueOf(id));
            map.put(DTransferConstants.CALC_DIMENSION, "1");
            map.put("page", String.valueOf(page));
            CommonRequest.getAlbumList(map, new IDataCallBack<AlbumList>() {
                @Override
                public void onSuccess(@Nullable AlbumList albumList) {
                    if (page == 1) {
                        mFmAlbumAdapter.setData(albumList.getAlbums());
                    } else {
                        mFmAlbumAdapter.addData(albumList.getAlbums());
                    }
                    mFmAlbumAdapter.notifyDataSetChanged();
                    fmSubRefresh.finishRefresh();
                    fmSubRefresh.finishLoadmore();
                }

                @Override
                public void onError(int i, String s) {
                    fmSubRefresh.finishRefresh();
                    fmSubRefresh.finishLoadmore();
                }
            });
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }
}
