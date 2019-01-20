package com.readyidu.robot.ui.tv.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.readyidu.robot.AppConfig;
import com.readyidu.robot.component.rxbus.RxBus;
import com.readyidu.robot.event.tv.TvEvent;
import com.readyidu.robot.model.business.tv.BaseTvChannel;
import com.readyidu.robot.model.business.tv.CheckModel;
import com.readyidu.robot.model.business.tv.TvChannel;
import com.readyidu.robot.utils.data.DataTranUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by gx on 2017/12/28.
 */
public class CustomerSourceUtils {
    private static CheckModel checkModel;
    private static String key = "";

    public static CheckModel getCheckModel() {
        if (checkModel == null) {
            checkModel = new CheckModel();
        }
        return checkModel;
    }

    public static void setCheckModel(CheckModel checkModel) {
        CustomerSourceUtils.checkModel = checkModel;
    }

    public static String getKey() {
        return key;
    }

    public static void setKey(String key) {
        CustomerSourceUtils.key = key;
    }

    private static ArrayList<TvChannel> tvChannels = new ArrayList<>();
    private static LinkedHashMap<String, TvChannel> tvChannelLinkedHashMap = new LinkedHashMap<>();

    public static ArrayList<TvChannel> getTvChannels(Context context) {
        synchronized (DataTranUtils.class) {
            List<TvChannel> source = getSource(context);
            if (tvChannels.size() == 0) {
                tvChannels.clear();
                tvChannelLinkedHashMap.clear();
                for (TvChannel t : source) {
                    if (TextUtils.isEmpty(t.s) || TextUtils.isEmpty(t.c)) {
                        continue;
                    }
                    tvChannels.add(t);
                    String key = t.c + t.s + "";
                    tvChannelLinkedHashMap.put(key, t);
                }
            }
            return tvChannels;
        }
    }

    public static void renameSource(TvChannel t, String name) {
        String key = t.c + t.s + "";

        if (tvChannelLinkedHashMap.containsKey(key)) {
            tvChannelLinkedHashMap.remove(key);
            t.c = name;
            String key2 = t.c + t.s + "";
            tvChannelLinkedHashMap.put(key2, t);
        }
    }

    public static void deleteTvChannel(TvChannel t) {
        String key = t.c + t.s + "";

        if (tvChannelLinkedHashMap.containsKey(key)) {
            tvChannelLinkedHashMap.remove(key);
        }
    }

    private static void addAllSource(Context context, List<TvChannel> temp) {
        List<TvChannel> source = getTvChannels(context);
        for (TvChannel t : temp) {

            String key = t.c + t.s + "";
            if (!tvChannelLinkedHashMap.containsKey(key)) {
                if (TextUtils.isEmpty(t.s) || TextUtils.isEmpty(t.c)) {
                    continue;
                }
                tvChannelLinkedHashMap.put(key, t);
                source.add(t);
            }
        }
        write(context);
    }

    private static List<TvChannel> getSource(Context context) {
        synchronized (DataTranUtils.class) {
            String s = readFile(context);
            ArrayList<TvChannel> temps = new Gson().fromJson(s, new TypeToken<ArrayList<TvChannel>>() {
            }.getType());
            if (temps == null) {
                temps = new ArrayList<>();
            }
            tran(temps);
            return temps;
        }
    }

    public static void resolveSource(final Context context, final String urlString, final ResultSource resultSource) {
        synchronized (DataTranUtils.class) {
            downFile(context, urlString, new ResultPath() {
                @Override
                public void onSuccess(String fileName) {
                    List<TvChannel> source = getSource(fileName);
                    addAllSource(context, source);
                    write(context);
                    resultSource.onSuccess(source);
                }
            });
        }
    }

    private static List<TvChannel> getSource(String filePath) {
        synchronized (DataTranUtils.class) {
            String s = readFile(filePath);
            ArrayList<TvChannel> temps = null;
            try {
                JSONObject jsonObject = new JSONObject(s);
                String data = jsonObject.getString("data");
                temps = new Gson().fromJson(data, new TypeToken<ArrayList<TvChannel>>() {
                }.getType());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (temps == null) {
                temps = new ArrayList<>();
            }
            tran(temps);
            return temps;
        }
    }

    public static void resolveSource2(final Context context, final String urlString, final ResultSource resultSource) {
        synchronized (DataTranUtils.class) {
            downFile(context, urlString, new ResultPath() {
                @Override
                public void onSuccess(String fileName) {
                    List<TvChannel> source = getSource2(fileName);
                    addAllSource(context, source);
                    write(context);
                    resultSource.onSuccess(source);
                }
            });
        }
    }

    private static List<TvChannel> getSource2(String filePath) {
        synchronized (DataTranUtils.class) {
            String s = readFile(filePath);
            ArrayList<TvChannel> temps = null;
            temps = new Gson().fromJson(s, new TypeToken<ArrayList<TvChannel>>() {
            }.getType());

            if (temps == null) {
                temps = new ArrayList<>();
            }
            tran(temps);
            return temps;
        }
    }

    private static void tran(List<TvChannel> temp) {
        for (int i = 0; i < temp.size(); i++) {
            TvChannel tvChannel = temp.get(i);
            tvChannel.type = -2;
            tvChannel.classifyPosition = 0;
            tvChannel.classifyPositionSecond = 0;
            tvChannel.kindPosition = 0;
            tvChannel.setSelect(false);
        }
    }

    public static void write(Context context) {
        synchronized (DataTranUtils.class) {
            String s = "";
            if (tvChannels != null) {
                s = new Gson().toJson(tvChannels, new TypeToken<ArrayList<BaseTvChannel>>() {
                }.getType());
            }

            writeFile(context, s);
            RxBus.getInstance().send(new TvEvent.CustomerSourceChange());
        }
    }

    public static File writeSyscSource(Context context, List<TvChannel> selectData) {
        String s = "";
        if (selectData != null) {
            s = new Gson().toJson(selectData, new TypeToken<ArrayList<BaseTvChannel>>() {
            }.getType());
        }

        return writeSyscFile(context, s);
    }


    private static String readFile(String fileName) {
        File file = new File(fileName);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            int length = inputStream.available();
            byte[] buffer = new byte[length];
            inputStream.read(buffer);

            return new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    private static String readFile(Context context) {
        File file = new File(context.getFilesDir(), AppConfig.APP_USERID + "source.json");
        return readFile(file.getPath());
    }

    private static File writeSyscFile(Context context, String content) {
        File file = new File(context.getFilesDir(), content.hashCode() + "source.json");
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            byte[] bytes = content.getBytes();
            fileOutputStream.write(bytes, 0, bytes.length);
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }


    private static void writeFile(Context context, String content) {
        File file = new File(context.getFilesDir(), AppConfig.APP_USERID + "source.json");
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            byte[] bytes = content.getBytes();
            fileOutputStream.write(bytes, 0, bytes.length);
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
//
//    private static String readArea(Context context) {
//        try {
//            InputStream is = context.getAssets().open("customer_source.json");
//            int size = is.available();
//            byte[] buffer = new byte[size];
//            is.read(buffer);
//            is.close();
//            return new String(buffer, "utf-8");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }

    private static void downFile(final Context context, final String url, final ResultPath resultPath) {
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                .readTimeout(1000, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(1000, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(1000, TimeUnit.SECONDS)//设置连接超时时间
                .build();
        final Request request = new Request.Builder().url(url).build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Guo", "onFailure", e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                InputStream inputStream = response.body().byteStream();
                FileOutputStream fileOutputStream;
                try {
                    String path = context.getCacheDir() + File.separator + url.hashCode() + ".txt";
                    fileOutputStream = new FileOutputStream(path);
                    byte[] buffer = new byte[2048];
                    int len = 0;
                    while ((len = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, len);
                    }
                    fileOutputStream.flush();
                    resultPath.onSuccess(path);
                } catch (IOException e) {
                    Log.e("Guo", "IOException", e);
                }
            }
        });
    }

    interface ResultPath {
        void onSuccess(String fileName);
    }

    public interface ResultSource {
        void onSuccess(List<TvChannel> tvChannels);
    }
}