package cc.ibooker.znetlib.service;

import cc.ibooker.znetlib.base.ZNet;
import cc.ibooker.znetlib.request.MyGsonConverterFactory;
import cc.ibooker.znetlib.request.MyOkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * 服务构建工厂
 *
 * @author 邹峰立
 */
public class ServiceCreateFactory {
    /**
     * 创建服务
     *
     * @param clazz 映射的服务类
     */
    public static <T> T createRetrofitService(final Class<T> clazz) {
        Retrofit retrofit = new Retrofit.Builder()
                .client(MyOkHttpClient.getClient())
                .addConverterFactory(MyGsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(ZNet.getBaseUrl())
                .build();

        return retrofit.create(clazz);
    }

    /**
     * 创建服务
     *
     * @param clazz   映射的服务类
     * @param baseUrl 基址
     */
    public static <T> T createRetrofitService(final Class<T> clazz, String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .client(MyOkHttpClient.getClient())
                .addConverterFactory(MyGsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build();

        return retrofit.create(clazz);
    }
}
