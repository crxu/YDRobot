package com.readyidu.api;


import com.readyidu.utils.DeviceUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiInit {

    private volatile static ApiManager mNetDataManager;

    private ApiInit() {
    }

    public static ApiManager getApiManager() {
        if (null == mNetDataManager) {
            synchronized (ApiInit.class) {
                if (null == mNetDataManager) {
                    mNetDataManager = new ApiManager(init());
                }
            }
        }
        return mNetDataManager;
    }

    private static ApiService init() {
        //请求拦截器
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                Request.Builder builder = request.newBuilder();
                builder.addHeader("appId", AppConfig.APP_ID);
                builder.addHeader("password", AppConfig.APP_PASSWORD);
                builder.addHeader("AppUserId", DeviceUtils.getDeviceId());
                builder.addHeader("version", AppConfig.APP_API_VERSION);
                builder.addHeader("time", String.valueOf(System.currentTimeMillis()));
                builder.addHeader("lat", "0.0");
                builder.addHeader("lon", "0.0");
                builder.addHeader("platform", "tv");

                return chain.proceed(builder.build());
            }
        };

        //日志拦截器
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor()
                .setLevel(AppConfig.isOpenLog ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

        //缓存
//        Cache cache = new Cache(new File(AppConfig.FOLDER_RESPONSE_CACHE), 10 * 1024 * 1024);

        //初始化OkHttpClient
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.addInterceptor(interceptor);
        httpClientBuilder.addNetworkInterceptor(httpLoggingInterceptor);
        httpClientBuilder.retryOnConnectionFailure(true);
        httpClientBuilder.connectTimeout(15, TimeUnit.SECONDS);
        httpClientBuilder.readTimeout(15, TimeUnit.SECONDS);
        httpClientBuilder.writeTimeout(15, TimeUnit.SECONDS);
//        httpClientBuilder.cache(cache);
        OkHttpClient httpClient = httpClientBuilder.build();

        //初始化Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConfig.BASE_API_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
//                .addConverterFactory(CustomGsonConverterFactory.create())
                .client(httpClient)
                .build();

        return retrofit.create(ApiService.class);
    }

}
