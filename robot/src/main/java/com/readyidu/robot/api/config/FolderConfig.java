package com.readyidu.robot.api.config;

import android.content.Context;
import android.os.Environment;

import com.readyidu.robot.utils.log.LogUtils;

import java.io.File;

/**
 * @Autour: wlq
 * @Description: 文件目录配置类
 * @Date: 2017/10/17 12:52
 * @Update: 2017/10/17 12:52
 * @UpdateRemark:
 * @Version: V1.0
*/
public final class FolderConfig {

    public static boolean isHasExternalStorage = false;
    //基础目录
    public static String BASE_PATH;
    //基础缓存目录
    public static String BASE_CACHE_PATH;
    //总下载目录
    public static String FOLDER_DOWNLOAD_NAME;
    //应用配置目录
    public static String FOLDER_CONFIG_NAME;
    //帐号目录
    public static String FOLDER_ACCOUNT_NAME;
    //图片总目录
    public static String FOLDER_IMAGE_NAME;
    //闪屏文件夹名称
    public static String FOLDER_SPLASH_NAME;
    //相机文件夹名称
    public static String FOLDER_CAMERA_NAME;
    //临时文件夹名称
    public static String FOLDER_TEMP_NAME;
    //缓存图片文件夹名称
    public static String FOLDER_CACHE_NAME;
    //日志文件夹
    public static String FOLDER_LOG_NAME;
    //网络请求缓存
    public static String FOLDER_RESPONSE_CACHE;
    //保存的图片目录
    public static String FOLDER_PHOTO_NAME;
    //歌词缓存目录
    public static String FOLDER_LRC_TEMP;

    public static synchronized void init(Context context) {
        //初始化文件夹设置
        createFolderForApp(context);
    }

    /**
     * 初始化文件夹设置
     */
    public static synchronized void initFolders() {
        FOLDER_LOG_NAME = BASE_PATH + File.separator + "log";
        FOLDER_DOWNLOAD_NAME = BASE_PATH + File.separator + "download";
        FOLDER_CONFIG_NAME = BASE_PATH + File.separator + "config";
        FOLDER_ACCOUNT_NAME = BASE_PATH + File.separator + "account";

        FOLDER_IMAGE_NAME = BASE_PATH + File.separator + "image";
        FOLDER_SPLASH_NAME = FOLDER_IMAGE_NAME + File.separator + "splash";

        FOLDER_TEMP_NAME = BASE_CACHE_PATH + File.separator + "temp";
        FOLDER_CACHE_NAME = BASE_CACHE_PATH + File.separator + "imageCache";
        FOLDER_RESPONSE_CACHE = BASE_CACHE_PATH + File.separator + "response";

        FOLDER_LRC_TEMP = BASE_CACHE_PATH + File.separator + "lrcTemp";
    }

    /**
     * 判断是否存在外部存储
     */
    public static boolean isExternalStorageMounted() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * 创建文件夹
     *
     * @param context
     */
    public static void createFolderForApp(Context context) {
        try {
            if (isExternalStorageMounted()) {
                FolderConfig.BASE_PATH = context.getExternalFilesDir(null).getAbsolutePath();
                FolderConfig.BASE_CACHE_PATH = context.getExternalCacheDir().getAbsolutePath();
                FolderConfig.isHasExternalStorage = true;

            } else {
                FolderConfig.BASE_PATH = context.getFilesDir().getAbsolutePath();
                FolderConfig.BASE_CACHE_PATH = context.getCacheDir().getAbsolutePath();
                FolderConfig.isHasExternalStorage = false;
            }

            //创建文件夹
            initFolders();

            //应用下载目录
            File fl = new File(FolderConfig.FOLDER_DOWNLOAD_NAME);
            if (!fl.exists() || !fl.isDirectory()) {
                fl.mkdirs();
            }

            //应用配置目录
            fl = new File(FolderConfig.FOLDER_CONFIG_NAME);
            if (!fl.exists() || !fl.isDirectory()) {
                fl.mkdirs();
            }

            //应用帐号目录
            fl = new File(FolderConfig.FOLDER_ACCOUNT_NAME);
            if (!fl.exists() || !fl.isDirectory()) {
                fl.mkdirs();
            }

            //日志目录
            fl = new File(FolderConfig.FOLDER_LOG_NAME);
            if (!fl.exists() || !fl.isDirectory()) {
                fl.mkdirs();
            }

            //缓存目录
            fl = new File(FolderConfig.FOLDER_CACHE_NAME);
            if (!fl.exists() || !fl.isDirectory()) {
                fl.mkdirs();
            }

            if (FolderConfig.isHasExternalStorage) {
                //长图目录
                fl = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "smart_xy");
                if (!fl.exists() || !fl.isDirectory()) {
                    fl.mkdirs();
                }
                FolderConfig.FOLDER_PHOTO_NAME = fl.getAbsolutePath();
            }

        } catch (Exception e) {
            LogUtils.e(e);
        }
    }
}
