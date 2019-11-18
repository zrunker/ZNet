package cc.ibooker.znetlib.service;

import cc.ibooker.znetlib.base.ZNet;
import cc.ibooker.znetlib.request.MyGsonConverterFactory;
import cc.ibooker.znetlib.request.MyOkHttpClient;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * 服务构建工厂
 * @author 邹峰立
 */
public class ServiceCreateFactory {
    public static <T> T createRetrofitService(final Class<T> clazz) {
        Retrofit retrofit = new Retrofit.Builder()
                .client(MyOkHttpClient.getClient())
                .addConverterFactory(MyGsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(ZNet.getBaseUrl())
                .build();

        return retrofit.create(clazz);
    }

}
