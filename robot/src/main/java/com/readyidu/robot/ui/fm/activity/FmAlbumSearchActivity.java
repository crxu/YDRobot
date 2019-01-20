package com.readyidu.robot.ui.fm.activity;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.readyidu.baiduvoice.laster.FloatView;
import com.readyidu.baiduvoice.laster.FloatViewInstance;
import com.readyidu.basic.utils.DialogUtils;
import com.readyidu.robot.R;
import com.readyidu.robot.api.NetSubscriber;
import com.readyidu.robot.api.impl.RobotServiceImpl;
import com.readyidu.robot.base.BaseVoiceBarActivity;
import com.readyidu.robot.model.BaseModel;
import com.readyidu.robot.ui.fm.adapetr.FmAlbumAdapter;
import com.readyidu.robot.ui.fm.adapetr.FmBroadCastAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioList;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuzhang on 2018/6/3.
 */

public class FmAlbumSearchActivity extends BaseVoiceBarActivity {

    TextView fmAlbumSearchCancel;
    EditText fmAlbumSearchHeadEdt;
    RelativeLayout fmAlbumSearchTitle;
    ListView fmAlbumSearchList;
    SmartRefreshLayout fmAlbumSearchRefresh;

    private FmAlbumAdapter mFmAlbumAdapter;
    private FmBroadCastAdapter mFmBroadCastAdapter;
    private int page;
    private String searchkey;
    private String searchType;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void bindViews() {
        super.bindViews();

        page = 1;
        searchkey = getIntent().getStringExtra("searchkey");
        searchType = getIntent().getStringExtra("searchType");
        if(searchType ==  null || searchType.equals("")){
            searchType = "album";
        }

        fmAlbumSearchHeadEdt = (EditText)findViewById(R.id.fm_album_search_head_edt);
        if(searchkey == null){
            searchkey = "";
        }else{
            fmAlbumSearchHeadEdt.setText(searchkey);
        }
        fmAlbumSearchCancel = (TextView)findViewById(R.id.fm_album_search_cancel);
        fmAlbumSearchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        fmAlbumSearchTitle = (RelativeLayout)findViewById(R.id.fm_album_search_title);
        fmAlbumSearchList = (ListView)findViewById(R.id.fm_album_search_list);
        fmAlbumSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(searchType.equals("album")){
                    Intent detailIntent = new Intent(FmAlbumSearchActivity.this, FmAlbumDetailActivity.class);
                    detailIntent.putExtra("album", mFmAlbumAdapter.getData().get(position));
                    detailIntent.putExtra("albumId", String.valueOf(mFmAlbumAdapter.getData().get(position).getId()));
                    detailIntent.putExtra("nickname", mFmAlbumAdapter.getData().get(position).getAnnouncer().getNickname());
                    detailIntent.putExtra("avatar", mFmAlbumAdapter.getData().get(position).getAnnouncer().getAvatarUrl());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd更新");
                    detailIntent.putExtra("updatedAt", sdf.format(mFmAlbumAdapter.getData().get(position).getUpdatedAt()));
                    detailIntent.putExtra("playConnt", mFmAlbumAdapter.getData().get(position).getPlayCount() + "次");
                    detailIntent.putExtra("trackCount", mFmAlbumAdapter.getData().get(position).getIncludeTrackCount() + "集");
                    startActivity(detailIntent);
                }else{
                    Radio radio = mFmBroadCastAdapter.getData().get(position);
                    Intent radioIntent = new Intent(FmAlbumSearchActivity.this,FmRadioPlayActivity.class);
                    radioIntent.putExtra("radio",radio);
                    startActivity(radioIntent);
                }
            }
        });
        fmAlbumSearchRefresh = (SmartRefreshLayout)findViewById(R.id.fm_album_search_refresh);

        mFmAlbumAdapter = new FmAlbumAdapter(this);
        mFmBroadCastAdapter = new FmBroadCastAdapter(this);

        fmAlbumSearchHeadEdt.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                searchkey = fmAlbumSearchHeadEdt.getText().toString();
                if(searchType.equals("album")){
                    getSearchedAlbums();
                }else{
                    getSearchRadio();
                }
                return false;
            }
        });

        fmAlbumSearchRefresh.setEnableLoadmore(true);
        fmAlbumSearchRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                if(searchType.equals("album")){
                    getSearchedAlbums();
                }else{
                    getSearchRadio();
                }
            }
        });
        fmAlbumSearchRefresh.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                page++;
                if(searchType.equals("album")){
                    getSearchedAlbums();
                }else{
                    getSearchRadio();
                }
            }
        });
        if(searchType.equals("album")){
            getSearchedAlbums();
        }else{
            getSearchRadio();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        FloatViewInstance.attach(findViewById(android.R.id.content), this, new FloatView.OnMessageResult() {
            @Override
            public void onMessageResult(String s) {
                searchContent = s;
                requestVoiceBarNet();
            }
        });
    }



    @Override
    protected void onStart() {
        super.onStart();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void bindEvents() {
        super.bindEvents();

//        Share data = null;
//        Intent intent = getIntent();
//        if (intent != null) {
//            isSDKOutside = intent.getBooleanExtra("isSDKOutside", false);
//            if (isSDKOutside) {
//                data = intent.getParcelableExtra("data");
//                if (null != data) {
//                    setData(data);
//                } else {
//                    requestData();
//                }
//            } else {
//                data = intent.getParcelableExtra("data");
//                setData(data);
//            }
//
//        }

    }

    @Override
    protected void bindKeyBoardShowEvent() {

    }

    @Override
    protected void requestVoiceBarNet() {
        DialogUtils.showProgressDialog(this);
        if (TextUtils.isEmpty(searchContent)) {
            searchContent = "";
        }
        addDisposable(RobotServiceImpl.fmApp(searchContent,false)
                .subscribeWith(new NetSubscriber<BaseModel>() {

                    @Override
                    protected void onSuccess(BaseModel baseModel) {
                        DialogUtils.closeProgressDialog();
                        if("history".equals(baseModel.data.contentType)){
                            startActivity(new Intent(FmAlbumSearchActivity.this, FmHistoryActivity.class));
                        }else{
                            if (baseModel.data.contentType != null && baseModel.data.contentType.length() > 0 && baseModel.data.keyword != null && baseModel.data.keyword.length() > 0) {
                                searchkey = baseModel.data.keyword;
                                fmAlbumSearchHeadEdt.setText(searchkey);
                                if(baseModel.data.contentType.equals("album")){
                                    getSearchedAlbums();
                                }else{
                                    getSearchRadio();
                                }
                            }
                        }
                    }

                    @Override
                    protected void onFailure(int errorCode, String errorMessage) {
                        DialogUtils.closeProgressDialog();
                    }
                }));
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fm_album_search_layout;
    }

    private void getSearchedAlbums(){
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.SEARCH_KEY, searchkey);
        map.put(DTransferConstants.PAGE, String.valueOf(page));
        CommonRequest.getSearchedAlbums(map, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(@Nullable SearchAlbumList searchAlbumList) {
                fmAlbumSearchList.setAdapter(mFmAlbumAdapter);
                if(page == 1){
                    mFmAlbumAdapter.setData(searchAlbumList.getAlbums());
                }else{
                    mFmAlbumAdapter.addData(searchAlbumList.getAlbums());
                }
                mFmAlbumAdapter.notifyDataSetChanged();
                fmAlbumSearchRefresh.finishRefresh();
                fmAlbumSearchRefresh.finishLoadmore();
            }

            @Override
            public void onError(int i, String s) {
                fmAlbumSearchRefresh.finishRefresh();
                fmAlbumSearchRefresh.finishLoadmore();
            }
        });
    }

    private void getSearchRadio(){
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.SEARCH_KEY, searchkey);
        CommonRequest.getSearchedRadios(map, new IDataCallBack<RadioList>() {
            @Override
            public void onSuccess(@Nullable RadioList radioList) {
                fmAlbumSearchList.setAdapter(mFmBroadCastAdapter);
                if(page == 1){
                    mFmBroadCastAdapter.setData(radioList.getRadios());
                }else{
                    mFmBroadCastAdapter.addData(radioList.getRadios());
                }
                mFmBroadCastAdapter.notifyDataSetChanged();
                fmAlbumSearchRefresh.finishRefresh();
                fmAlbumSearchRefresh.finishLoadmore();
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

}
