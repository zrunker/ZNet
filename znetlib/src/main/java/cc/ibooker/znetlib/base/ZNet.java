package cc.ibooker.znetlib.base;

import android.app.Application;
import android.text.TextUtils;

import java.io.File;

import cc.ibooker.znetlib.request.MyOkHttpClient;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * 初始化网络组件类
 * 必填项：上下文对象，网络请求基址
 *
 * @author 邹峰立
 */
public class ZNet {
    private static Application instance;
    private static String baseUrl;
    private static String appVersion = "1.0";
    private static String deviceType = "1";

    public static void init(Application application, String url) {
        instance = application;
        baseUrl = url;
    }

    public static void init(Application application, String url, String version, String device) {
        if (!TextUtils.isEmpty(version))
            appVersion = version;
        if (!TextUtils.isEmpty(device))
            deviceType = device;
        init(application, url);
    }

    // 获取全局上下文对象
    public static Application getInstance() {
        return instance;
    }

    // 获取App版本
    public static String getAppVersion() {
        return appVersion;
    }

    // 获取baseUrl
    public static String getBaseUrl() {
        return baseUrl;
    }

    // 获取设备类型
    public static String getDeviceType() {
        return deviceType;
    }

    // 获取默认超时时间
    public static int getDefaultTimeout() {
        return MyOkHttpClient.getDefaultTimeout();
    }

    public static void setDefaultTimeout(int defaultTimeout) {
        MyOkHttpClient.setDefaultTimeout(defaultTimeout);
    }

    // 获取缓存文件
    public static File getCacheFile() {
        return MyOkHttpClient.getCacheFile();
    }

    public static void setCacheFile(File cacheFile) {
        MyOkHttpClient.setCacheFile(cacheFile);
    }

    // 获取缓存大小
    public static long getCacheSize() {
        return MyOkHttpClient.getCacheSize();
    }

    public static void setCacheSize(long cacheSize) {
        MyOkHttpClient.setCacheSize(cacheSize);
    }

    // 获取OkHttpClient
    public static OkHttpClient getOkHttpClient() {
        return MyOkHttpClient.getClient();
    }

    // 设置拦截器
    public static void addInterceptor(Interceptor interceptor) {
        MyOkHttpClient.setInterceptor(interceptor);
    }

    // 获取ClientBuilder
    public static OkHttpClient.Builder getClientBuilder() {
        return MyOkHttpClient.getClientBuilder();
    }
}
