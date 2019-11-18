package cc.ibooker.znetlib.base;

import android.app.Application;
import android.text.TextUtils;

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
}
