package cc.ibooker.znetlib.download;

/**
 * 下载情况类
 *
 * @author 邹峰立
 */
public class DownLoadInfoBean {
    private long contentLength;/* 文件总长度 */
    private long readLength;/* 下载长度 */


    public DownLoadInfoBean() {
        super();
    }

    public DownLoadInfoBean(long contentLength, long readLength) {
        this.contentLength = contentLength;
        this.readLength = readLength;
    }

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public long getReadLength() {
        return readLength;
    }

    public void setReadLength(long readLength) {
        this.readLength = readLength;
    }

    @Override
    public String toString() {
        return "DownLoadInfoBean{" +
                "contentLength=" + contentLength +
                ", readLength=" + readLength +
                '}';
    }
}
