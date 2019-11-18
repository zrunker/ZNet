package cc.ibooker.znet;

import android.app.Application;

import cc.ibooker.znetlib.base.ZNet;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 请求基址url
        String base_url = "http://ibooker.cc";
        ZNet.init(this, base_url);
    }
}
