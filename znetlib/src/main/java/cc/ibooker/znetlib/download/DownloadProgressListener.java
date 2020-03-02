package cc.ibooker.znetlib.download;

/**
 * 下载进度监听
 *
 * @author 邹峰立
 */
public interface DownloadProgressListener {

    /**
     * @param read          已下载长度
     * @param contentLength 总长度
     * @param done          是否下载完毕
     */
    void progress(long read, long contentLength, boolean done);

}
