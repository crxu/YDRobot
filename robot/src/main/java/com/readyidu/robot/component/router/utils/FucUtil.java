package com.readyidu.robot.component.router.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.readyidu.basic.utils.CommonUtils;
import com.readyidu.robot.component.router.model.RecommendTypeInfo;
import com.readyidu.robot.component.router.model.TvRouterModel;
import com.readyidu.robot.utils.log.LogUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * 功能性函数扩展类
 */
public final class FucUtil {

    /**
     * 读取asset目录下文件。
     *
     * @return content
     */
    public static String readFile(Context mContext, String file, String code) {
        int len = 0;
        byte[] buf = null;
        String result = "";
        try {
            InputStream in = mContext.getAssets().open(file);
            len = in.available();
            buf = new byte[len];
            in.read(buf, 0, len);

            result = new String(buf, code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    //读取方法
    public static String getJson(Context mContext, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = mContext.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 读取asset目录下音频文件。
     *
     * @return 二进制文件数据
     */
    public static byte[] readAudioFile(Context context, String filename) {
        try {
            InputStream ins = context.getAssets().open(filename);
            byte[] data = new byte[ins.available()];

            ins.read(data);
            ins.close();

            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 过滤文本
     */
    public static String filterContent(String content) {
        return TextUtils.isEmpty(content) ? content : content
                .replace("我想看", "")
                .replace("打开", "")
                .replace("想看", "")
                .replace("我想", "")
                .replace("我需要买", "")
                .replace("我要看", "")
                .replace("我要买", "")
                .replace("我买", "")
                .replace("我想买", "")
                .replace("我想要", "")
                .replace("我要", "")
                .replace("我的", "")
                .replace("给我", "")
                .replace("我需要", "");
    }

    //app路由
    private static TvRouterModel mTvRouterModel;

    /**
     * 获取App功能路由
     */
    public static int getTvAppRouter(Context context, String words) {
        try {
            if (null == mTvRouterModel) {
                String contents;
                if (CommonUtils.isAged()) {
                    contents = FucUtil.readFile(context, "tv_app.json", "utf-8");
                } else {
                    contents = FucUtil.readFile(context, "znxy_app.json", "utf-8");
                }
                Gson gson = new Gson();
                mTvRouterModel = gson.fromJson(contents, TvRouterModel.class);
            }
            return getTvAppRouterWords(mTvRouterModel.getTv_app_router(), words);

        } catch (Exception e) {
            LogUtils.e(e);
        }
        return 0;
    }

    /**
     * 判断是否在App功能路由分词库里
     */
    private static int getTvAppRouterWords(List<TvRouterModel.TvAppRouterBean> modelList, String words) {
        for (TvRouterModel.TvAppRouterBean value : modelList) {
            if (words.equals(value.getTvNickName())) {
                return value.getTvId();
            }
        }
        return 0;
    }

    private static List<RecommendTypeInfo> mRecommendList = new ArrayList<>();

    static {
        mRecommendList.clear();
//        mRecommendList.add(new RecommendTypeInfo(R.drawable.ic_type_caipu, 0, "菜谱", "番茄炒蛋菜谱", 0));
//        mRecommendList.add(new RecommendTypeInfo(R.drawable.ic_type_dingwei, 1, "定位", "查看定位", 10009));
//        mRecommendList.add(new RecommendTypeInfo(R.drawable.ic_type_tianqi, 2, "天气", "宁波天气", 0));
//        mRecommendList.add(new RecommendTypeInfo(R.drawable.ic_type_xinwen, 3, "新闻", "军事新闻", 0));
//        mRecommendList.add(new RecommendTypeInfo(R.drawable.ic_type_yinyue, 4, "音乐", "刘德华的歌", 0));
//
//        mRecommendList.add(new RecommendTypeInfo(R.drawable.ic_type_baike, 5, "百科", "我要查百科", 0));
//        mRecommendList.add(new RecommendTypeInfo(R.drawable.ic_type_jiankang, 6, "健康", "我的健康", 10001));
//        mRecommendList.add(new RecommendTypeInfo(R.drawable.ic_type_wenduji, 7, "体温", "查看体温数据", 10005));
//        mRecommendList.add(new RecommendTypeInfo(R.drawable.ic_type_xueya, 8, "血压", "查看血压数据", 10003));
//        mRecommendList.add(new RecommendTypeInfo(R.drawable.ic_type_jiankong, 9, "监控", "查看监控", 10008));
//
//        mRecommendList.add(new RecommendTypeInfo(R.drawable.ic_type_liaotian, 10, "聊天", "我要聊天", 10015));
//        mRecommendList.add(new RecommendTypeInfo(R.drawable.ic_type_hujiaozhongxin, 11, "呼叫中心", "呼叫中心", 10014));
//        mRecommendList.add(new RecommendTypeInfo(R.drawable.ic_type_dianying, 12, "电影", "速度与激情", 2));
//        mRecommendList.add(new RecommendTypeInfo(R.drawable.ic_type_dianshipindao, 13, "电视直播", "宁波卫视", 1));
//        mRecommendList.add(new RecommendTypeInfo(R.drawable.ic_type_dianshiju, 14, "电视剧", "春风十里不如你", 2));
//
//        mRecommendList.add(new RecommendTypeInfo(R.drawable.ic_type_fuwu, 15, "服务", "生活照料", 10011));
//        mRecommendList.add(new RecommendTypeInfo(R.drawable.ic_type_yingyong, 16, "应用", "应用商城", 10026));
//        mRecommendList.add(new RecommendTypeInfo(R.drawable.ic_type_zhinengxiaoyi, 17, "智能小益", "智能助手", 10038));
//        mRecommendList.add(new RecommendTypeInfo(R.drawable.ic_type_rili, 18, "日历", "父亲节几号", 0));
//        mRecommendList.add(new RecommendTypeInfo(R.drawable.ic_type_jisuanqi, 19, "计算器", "367+837等于多少", 0));
//
//        mRecommendList.add(new RecommendTypeInfo(R.drawable.ic_type_xinlv, 20, "心率", "查看心率数据", 10006));
//        mRecommendList.add(new RecommendTypeInfo(R.drawable.ic_type_jibu, 21, "计步", "查看计步数据", 10007));
//        mRecommendList.add(new RecommendTypeInfo(R.drawable.ic_type_xuetang, 22, "血糖", "查看血糖数据", 10004));
    }

    private static int mCurrentIndex = 0;

    public static List<RecommendTypeInfo> getRangeRecommend() {
        List<RecommendTypeInfo> list = mRecommendList.subList(mCurrentIndex * 5, Math.min((mCurrentIndex + 1) * 5, mRecommendList.size()));
        mCurrentIndex += 1;
        if (mCurrentIndex == 5) {
            mCurrentIndex = 0;
        }
        return list;
    }

}
