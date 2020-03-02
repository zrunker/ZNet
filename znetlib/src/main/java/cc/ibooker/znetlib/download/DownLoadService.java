package cc.ibooker.znetlib.download;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * 下载的Service
 *
 * @author 邹峰立
 */
public interface DownLoadService {
    /**
     * @param start 从某个字节开始下载数据
     * @param url   文件下载的url
     */
    @Streaming
    @GET
    Observable<ResponseBody> download(@Header("RANGE") String start, @Url String url);

}
