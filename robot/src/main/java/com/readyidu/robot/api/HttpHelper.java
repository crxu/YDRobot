package com.readyidu.robot.api;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.readyidu.robot.AppConfig;
import com.readyidu.robot.BuildConfig;
import com.readyidu.robot.YDRobot;
import com.readyidu.robot.api.impl.RobotServiceImpl;
import com.readyidu.robot.api.impl.TVServiceImpl;
import com.readyidu.robot.component.router.api.ApiService;
import com.readyidu.robot.component.router.api.ApiServiceImpl;
import com.readyidu.robot.utils.log.LogUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by bruce on 16/4/22.
 *
 * @modify:wlq
 * @modify-date:2017.10.11
 */
public class HttpHelper {

    public static void initialize() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request()
                        .newBuilder()
                        .removeHeader("Accept-Encoding")
                        .build();

                StringBuilder sb = new StringBuilder();
                if (request.body() instanceof FormBody) {
                    FormBody body = (FormBody) request.body();
                    for (int i = 0; i < body.size(); i++) {
                        sb.append(body.encodedName(i)).append("=").append(new String(body.encodedValue(i).getBytes("utf-8"), "gbk")).append(",");
                    }
                    if (sb.length() >= 1) {
                        sb.delete(sb.length() - 1, sb.length());
                    }
                    LogUtils.i("YDRobot", "%s\n\n%s\n{%s}",
                            request.url().toString(),
                            request.headers().toString(),
                            sb.toString());
                } else {
                    LogUtils.i("YDRobot", "%s\n\n%s",
                            request.url().toString(),
                            request.headers().toString());
                }

                Response response = chain.proceed(request);
                String responseString = new String(response.body().bytes());
                LogUtils.d(responseString);

                return response.newBuilder()
                        .body(ResponseBody.create(response.body().contentType(), responseString))
                        .build();
            }
        };

        Interceptor headerInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request.Builder builder = chain.request().newBuilder();
                builder.addHeader("appId", AppConfig.APP_ID);
                builder.addHeader("password", AppConfig.APP_PASSWORD);
                builder.addHeader("AppUserId", AppConfig.APP_USERID);
                builder.addHeader("version", "1.1.5");
                builder.addHeader("locationId", AppConfig.location_id);
                builder.addHeader("platform", "android");
                builder.addHeader("lat", YDRobot.getInstance().getLat());
                builder.addHeader("lon", YDRobot.getInstance().getLon());
                builder.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
                return chain.proceed(builder.build());
            }
        };

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
//                .addNetworkInterceptor(interceptor)
                .addInterceptor(interceptor)
                .addInterceptor(headerInterceptor)  //添加请求头文件
                .retryOnConnectionFailure(true)
                .readTimeout(15, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(15, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(15, TimeUnit.SECONDS);//设置连接超时时间

        if (BuildConfig.DEBUG) {
            builder.addNetworkInterceptor(new StethoInterceptor());
        }

        OkHttpClient client = builder.build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConfig.BASE_API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        Retrofit tvRetrofit = new Retrofit.Builder()
                .baseUrl(AppConfig.BASE_API_URL_TV)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        RobotServiceImpl.robotService = retrofit.create(RobotService.class);
        ApiServiceImpl.apiService = retrofit.create(ApiService.class);
        TVServiceImpl.tvService = tvRetrofit.create(TVService.class);
    }

    public static RobotService downloadFileInit(String url) {

        OkHttpClient client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .readTimeout(15, TimeUnit.SECONDS)//设置读取超时时间
                .writeTimeout(15, TimeUnit.SECONDS)//设置写的超时时间
                .connectTimeout(15, TimeUnit.SECONDS)//设置连接超时时间
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url + "/")
                .client(client)
                .build();

        return retrofit.create(RobotService.class);
    }
}