package cc.ibooker.znetlib.request;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import cc.ibooker.znetlib.base.ZNet;
import cc.ibooker.znetlib.interceptor.CacheInterceptor;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Okhttp 弊端适合对键值对缓存(Get)，不能对加密数据直接缓存
 * Created by 邹峰立 on 2016/12/11.
 */
public class MyOkHttpClient {
    private static OkHttpClient mClient;

    // 构造方法私有
    private MyOkHttpClient() {
        // Okhttp日志拦截器
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(@NonNull String message) {
                int segmentSize = 3 * 1024;
                long length = message.length();
                String TAG = "HttpRequestState";
                if (length <= segmentSize) {
                    Log.d(TAG, "OkHttp: " + message);
                } else {
                    while (message.length() > segmentSize) {// 循环分段打印日志
                        String logContent = message.substring(0, segmentSize);
                        message = message.replace(logContent, "");
                        Log.d(TAG, logContent);
                    }
                    Log.d(TAG, "OkHttp: " + message);
                }
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Okhttp缓存拦截器
        File fileCache = new File(Environment.getExternalStorageDirectory() + File.separator + "aixizhi", "cacheData");
        Cache cache = new Cache(fileCache.getAbsoluteFile(), 1024 * 1024 * 30);//设置缓存30M
        CacheInterceptor caheInterceptor = new CacheInterceptor(ZNet.getInstance());// 缓存拦截器

        // 创建OkHttpClient对象
        int DEFAULT_TIMEOUT = 15;
        mClient = new OkHttpClient.Builder()
//                .addInterceptor(noTokenInterceptor)
                .addInterceptor(logging)
//                .addInterceptor(longLogging)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)// 超时时间15S
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)// 连接失败后是否重新连接
                .cache(cache)//设置缓存
                .addInterceptor(caheInterceptor)// 离线缓存
                .addNetworkInterceptor(caheInterceptor)// 在线缓存
                .addInterceptor(new Interceptor() {//添加请求头
                    @Override
                    public Response intercept(@NonNull Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("device-type", ZNet.getDeviceType())
                                .addHeader("version", ZNet.getAppVersion())
                                .build();
                        return chain.proceed(request);
                    }
                })
                .build();

        // 无缓存
//        mClient = new OkHttpClient.Builder()
//                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)//超时时间15S
//                .retryOnConnectionFailure(true)//连接失败后是否重新连接
//                .build();
    }

    public static OkHttpClient getClient() {
        synchronized (MyOkHttpClient.class) {
            if (mClient == null) {
                new MyOkHttpClient();
            }
        }
        return mClient;
    }

}