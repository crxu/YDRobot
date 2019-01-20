package com.readyidu.robot.ui.fm.util;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.service.IXmDataCallback;
import com.ximalaya.ting.android.sdkdownloader.XmDownloadManager;

import java.util.List;

/**
 * Created by yuzhang on 2018/6/1.
 */

public class FmPlayUtil {
    private static Context mContext;
    private static XmPlayerManager mPlayerManager;
    private static FmPlayInitListerner mFmPlayInitListerner;

    private static AlbumTrackModel curAlbumTrackModel;
    private static RadioScheduleModel curRadioScheduleModel;

    public static void initFmPlay(Context context, FmPlayInitListerner listerner) {
        mContext = context;
        mFmPlayInitListerner = listerner;

        mPlayerManager = XmPlayerManager.getInstance(mContext.getApplicationContext());
//        Notification mNotification = XmNotificationCreater.getInstanse(mContext.getApplicationContext()).initNotification(mContext.getApplicationContext(), FmPlayActivity.class);

        // 如果之前贵方使用了 `XmPlayerManager.init(int id, Notification notification)` 这个初始化的方式
        // 请参考`4.8 播放器通知栏使用`重新添加新的通知栏布局,否则直接升级可能导致在部分手机播放时崩溃
        // 如果不想使用sdk内部搞好的notification,或者想自建notification 可以使用下面的  init()函数进行初始化

        if (mPlayerManager.isConnected()) {
            if(mFmPlayInitListerner != null){
                mFmPlayInitListerner.isInit();
            }
        } else {
//            mPlayerManager.init((int) System.currentTimeMillis(), mNotification);
            mPlayerManager.init();
            mPlayerManager.addOnConnectedListerner(new XmPlayerManager.IConnectListener() {
                @Override
                public void onConnected() {
                    mPlayerManager.removeOnConnectedListerner(this);
                    if(mFmPlayInitListerner != null){
                        mFmPlayInitListerner.isInit();
                    }
                }
            });

            XmPlayerManager.getInstance(mContext).setCommonBusinessHandle(XmDownloadManager.getInstance());

            mPlayerManager.setPlayListChangeListener(new IXmDataCallback() {
                @Override
                public void onDataReady(List<Track> list, boolean hasMorePage, boolean isNextPage) throws RemoteException {
                }

                @Override
                public void onError(int code, String message, boolean isNextPage) throws RemoteException {
                }

                @Override
                public IBinder asBinder() {
                    return null;
                }
            });
        }
    }

    public static AlbumTrackModel getCurAlbumTrackModel() {
        return curAlbumTrackModel;
    }

    public static void setCurAlbumTrackModel(AlbumTrackModel curAlbumTrackModel) {
        FmPlayUtil.curRadioScheduleModel = null;
        FmPlayUtil.curAlbumTrackModel = curAlbumTrackModel;
//        if (!curAlbumTrackModel.getSort().equals("asc")) {
//            FmPlayUtil.curAlbumTrackModel.setSteps(FmPlayUtil.curAlbumTrackModel.getCurTrackList().size() - FmPlayUtil.curAlbumTrackModel.getSteps() - 1);
//        }
    }

    public static void setCurRadioScheduleModel(RadioScheduleModel curRadioScheduleModel) {
        FmPlayUtil.curAlbumTrackModel = null;
        FmPlayUtil.curRadioScheduleModel = curRadioScheduleModel;
    }

    public static RadioScheduleModel getCurRadioScheduleModel() {
        return curRadioScheduleModel;
    }

    public static XmPlayerManager getPlayerManager() {
        if(mPlayerManager == null){
            initFmPlay(mContext,null);
        }
        return mPlayerManager;
    }

    public interface FmPlayInitListerner {
        public void isInit();
    }
}
