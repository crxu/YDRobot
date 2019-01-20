package com.readyidu.robot.ui.fm.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.readyidu.basic.base.BaseFragment;
import com.readyidu.robot.R;
import com.readyidu.robot.ui.fm.adapetr.FmRadioContentPlayAdapter;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.model.live.schedule.ScheduleList;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuzhang on 2018/6/2.
 */

public class FmRadioContentPlayFragment extends BaseFragment {
    ListView fmRadioContentPlayList;
    private String radioId;
    private int dayTab;
    private long liveId;
    private int index;

    private FmRadioContentPlayAdapter mFmRadioContentPlayAdapter;


    @Override
    protected int getLayoutId() {
        return R.layout.fm_radio_content_play_layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        radioId = getArguments().getString("radioId");
        dayTab = getArguments().getInt("dayTab");
        liveId = getArguments().getLong("liveId");
        index = getArguments().getInt("index");
        fmRadioContentPlayList = (ListView) view.findViewById(R.id.fm_radio_content_play_list);
        fmRadioContentPlayList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Schedule data = mFmRadioContentPlayAdapter.getData().get(position);
//                if(data.getListenBackUrl() != null && !data.getListenBackUrl().equals("")){
//                    ((FmRadioPlayActivity)getActivity()).playRadio(mFmRadioContentPlayAdapter.getData(),position);
//                    mFmRadioContentPlayAdapter.setSelectId(data.getDataId());
//                }

                ((FmRadioPlayActivity) getActivity()).playRadio(mFmRadioContentPlayAdapter.getData(), position);
                mFmRadioContentPlayAdapter.setSelectId(data.getDataId());

//                Schedule schedule = mFmRadioContentPlayAdapter.getData().get(position);
////                String time = schedule.getStartTime() + "-" + schedule.getEndTime();
////                if (ToolUtil.isInTime(time) < 0) {
////
////                }
//                FmPlayUtil.getPlayerManager().playSchedule(mFmRadioContentPlayAdapter.getData(),position);
//                mFmRadioContentPlayAdapter.setSelectId(data.getDataId());
            }
        });
        mFmRadioContentPlayAdapter = new FmRadioContentPlayAdapter(getContext());
        fmRadioContentPlayList.setAdapter(mFmRadioContentPlayAdapter);

        getSchedules();
    }

    private int getWeek() {
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        return c.get(Calendar.DAY_OF_WEEK) - 1;
    }

    private void getSchedules() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.RADIOID, radioId);
        if (dayTab != 1) {
            int week = getWeek();
            if (dayTab == 0) {
                int bweek = 0;
                if (week == 0) {
                    bweek = 6;
                } else {
                    bweek = week - 1;
                }
                map.put(DTransferConstants.WEEKDAY, String.valueOf(bweek));
            }

            if (dayTab == 2) {
                int fweek = 0;
                if (week == 6) {
                    fweek = 0;
                } else {
                    fweek = week + 1;
                }
                map.put(DTransferConstants.WEEKDAY, String.valueOf(fweek));
            }
        }
        CommonRequest.getSchedules(map, new IDataCallBack<ScheduleList>() {
            @Override
            public void onSuccess(@Nullable ScheduleList scheduleList) {
                mFmRadioContentPlayAdapter.setData(scheduleList.getmScheduleList());
                mFmRadioContentPlayAdapter.setLiveId(liveId);
                mFmRadioContentPlayAdapter.notifyDataSetChanged();

                if(index != -1){
                    mFmRadioContentPlayAdapter.setSelectId(scheduleList.getmScheduleList().get(index).getDataId());
                }

            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }


}
