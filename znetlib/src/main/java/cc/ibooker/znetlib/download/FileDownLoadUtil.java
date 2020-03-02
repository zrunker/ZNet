package cc.ibooker.znetlib.download;

import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import cc.ibooker.znetlib.base.ZNet;
import cc.ibooker.znetlib.util.NetFileUtil;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * OkHttp3文件下载管理类
 *
 * @author 邹峰立
 */
public class FileDownLoadUtil implements DownloadProgressListener {
    private ExecutorService executor = Executors.newCachedThreadPool();
    private Subscription fileDownLoadSubscription;
    private DownLoadService downLoadService;
    private DownLoadInfoBean downLoadInfoBean = new DownLoadInfoBean();
    private MyHandler mHandler = new MyHandler(this);

    // 单例模式
    private static FileDownLoadUtil fileDownLoadUtil;

    public static FileDownLoadUtil getInstance() {
        if (fileDownLoadUtil == null) {
            synchronized (FileDownLoadUtil.class) {
                if (fileDownLoadUtil == null)
                    fileDownLoadUtil = new FileDownLoadUtil();
            }
        }
        return fileDownLoadUtil;
    }

    /**
     * 销毁相关变量
     */
    public void destoryFileDownLoad() {
        if (executor != null)
            executor.shutdownNow();
        if (mHandler != null) {
            mHandler.removeCallbacks(null);
        }
        if (fileDownLoadSubscription != null)
            fileDownLoadSubscription.unsubscribe();
        if (downLoadInfoBean != null)
            downLoadInfoBean = new DownLoadInfoBean();
        if (downLoadService != null)
            downLoadService = null;
    }

    /**
     * 停止下载
     */
    public void stopFileDownLoad() {
        if (executor != null) {
            executor.shutdownNow();
        }
        if (mHandler != null) {
            mHandler.removeCallbacks(null);
        }
        if (fileDownLoadSubscription != null) {
            fileDownLoadSubscription.unsubscribe();
        }
    }

    /**
     * 自定义Handler进入UI线程
     */
    private static class MyHandler extends Handler {
        WeakReference<FileDownLoadUtil> mWeakRef;

        MyHandler(FileDownLoadUtil fileDownLoadUtil) {
            mWeakRef = new WeakReference<>(fileDownLoadUtil);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mWeakRef.get().onDownLoadListener != null) {
                switch (msg.what) {
                    case 0:// 开始下载
                        mWeakRef.get().onDownLoadListener.onDownLoadStart();
                        break;
                    case 1:// 正在下载
                        mWeakRef.get().onDownLoadListener.onDownLoad((Integer) msg.obj);
                        break;
                    case 2:// 结束下载
                        mWeakRef.get().onDownLoadListener.onDownLoadCompleted((File) msg.obj);
                        break;
                    case 3:// 下载错误
                        mWeakRef.get().onDownLoadListener.onDownLoadError((Exception) msg.obj);
                        break;
                }
            }
        }
    }

    /**
     * 下载进度监听 - 子线程
     *
     * @param read          已下载长度
     * @param contentLength 总长度
     * @param done          是否下载完毕
     */
    @Override
    public void progress(long read, long contentLength, boolean done) {
        // 赋值
        if (downLoadInfoBean.getContentLength() > contentLength)
            read = read + (downLoadInfoBean.getContentLength() - contentLength);
        else
            downLoadInfoBean.setContentLength(contentLength);
        // 重新赋值readLength
        downLoadInfoBean.setReadLength(read);

//        Observable.just(1)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<Integer>() {
//                    @Override
//                    public void call(Integer integer) {
//                        if (onDownLoadListener != null && downLoadInfoBean.getContentLength() != 0)
//                            onDownLoadListener.onDownLoad((int) (100 * downLoadInfoBean.getReadLength() / downLoadInfoBean.getContentLength()));
//                    }
//                });
    }

    /**
     * 开始下载文件 - 单步下载
     *
     * @param url      下载文件地址
     * @param filePath 保存文件地址
     * @param fileName 保存文件名称
     */
    public FileDownLoadUtil startDownloadFile(final String url, final String filePath, final String fileName) {
        try {
            // 创建文件
            File dir = new File(filePath);
            boolean bool = dir.exists();
            if (!bool)
                NetFileUtil.createSDDirs(filePath);
            final File file = new File(filePath, fileName);
            bool = file.exists() && file.isFile();
            if (!bool) {
                try {
                    bool = file.createNewFile();
                } catch (IOException e) {
                    bool = false;
                }
            }
            // 判断文件是否创建成功
            if (bool) {
                if (downLoadService == null) {
                    // 下载拦截器
                    DownloadInterceptor interceptor = new DownloadInterceptor(this);
                    OkHttpClient.Builder builder = new OkHttpClient.Builder()
                            .connectTimeout(15, TimeUnit.SECONDS)// 超时时间15S
                            .writeTimeout(15, TimeUnit.SECONDS)
                            .readTimeout(15, TimeUnit.SECONDS)
                            .retryOnConnectionFailure(true)// 连接失败后是否重新连接
                            .addInterceptor(interceptor);
                    Retrofit retrofit = new Retrofit.Builder()
                            .client(builder.build())
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .baseUrl(ZNet.getBaseUrl())
                            .build();
                    downLoadService = retrofit.create(DownLoadService.class);
                }
                if (downLoadService != null)
                    fileDownLoadSubscription = downLoadService.download("bytes=" + 0 + "-", url)
                            // 指定subscribe()发生在io调度器（读写文件、读写数据库、网络信息交互等）
                            .subscribeOn(Schedulers.io())
                            .unsubscribeOn(Schedulers.io())
                            // 失败后的retry配置
//                        .retryWhen(new RetryWhenNetworkException())
                            .map(new Func1<ResponseBody, DownLoadInfoBean>() {
                                @Override
                                public DownLoadInfoBean call(ResponseBody responseBody) {
                                    /**
                                     * 1. 实现文件的写入
                                     * 2. 传递DownLoadInfoBean对象，- 已实例化对象
                                     */
                                    writeRandomAccessFile(responseBody, file);
                                    if (downLoadInfoBean == null)
                                        downLoadInfoBean = new DownLoadInfoBean();
                                    return downLoadInfoBean;
                                }
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            //实现订阅关系
                            .subscribe(new Subscriber<DownLoadInfoBean>() {
                                @Override
                                public void onCompleted() {
                                    // 下载完成
                                    if (onDownLoadListener != null)
                                        onDownLoadListener.onDownLoadCompleted(file);
                                    stopFileDownLoad();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    // 下载出错
                                    if (onDownLoadListener != null)
                                        onDownLoadListener.onDownLoadError(new Exception(e));
                                    stopFileDownLoad();
                                    destoryFileDownLoad();
                                }

                                @Override
                                public void onNext(DownLoadInfoBean downLoadInfoBean) {
                                    // todo
                                }

                                @Override
                                public void onStart() {
                                    // 开始下载
                                    if (onDownLoadListener != null)
                                        onDownLoadListener.onDownLoadStart();
                                }
                            });

            } else {
                // 创建文件失败
                if (onDownLoadListener != null)
                    onDownLoadListener.onDownLoadError(new Exception("创建文件失败！"));
            }
        } catch (Exception e) {
            // 下载出错
            if (onDownLoadListener != null)
                onDownLoadListener.onDownLoadError(new Exception(e));
        }
        return this;
    }

    /**
     * 下载文件 - 子线程中运行 - 全部下载
     *
     * @param responseBody 文件资源
     * @param filePath     保存文件地址
     */
    public FileDownLoadUtil downloadFile(final ResponseBody responseBody, final String filePath, String fileName) {
        try {
            // 创建文件
            File dir = new File(filePath);
            boolean bool = dir.exists();
            if (!bool)
                NetFileUtil.createSDDirs(filePath);
            final File file = new File(filePath, fileName);
            bool = file.exists() && file.isFile();
            if (!bool) {
                bool = file.createNewFile();
            }
            if (bool) {
                if (mHandler == null)
                    mHandler = new MyHandler(this);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (responseBody != null) {
                            InputStream is = null;
                            FileOutputStream fos = null;
                            long sum = 0;
                            try {
                                /**
                                 * 开始下载回调
                                 */
                                sendDownLoadStartMessage();

                                int limit = 0;

                                // 写入文件
                                is = responseBody.byteStream();
                                fos = new FileOutputStream(file);
                                // 获取文件总长度
                                long contentLength = responseBody.contentLength();
                                byte[] buf = new byte[1024];

                                int len;
                                while ((len = is.read(buf)) != -1) {
                                    fos.write(buf, 0, len);
                                    if (contentLength > 0) {
                                        sum += len;
                                        int progress = (int) (sum * 1.0f / contentLength * 100);

                                        /**
                                         * 下载进度回调 - 30次一更新
                                         */
                                        limit++;
                                        if (limit % 30 == 0 && progress <= 100) {
                                            sendonDownLoadMessage(progress);
                                        }
                                    }
                                }

                                fos.flush();

                                /**
                                 * 下载完成回调
                                 */
                                sendonDownLoadCompletedMessage(file);
                            } catch (Exception e) {
                                /**
                                 * 下载错误回调
                                 */
                                sendDownLoadErrorMessage(e);
                            } finally {
                                try {
                                    if (is != null)
                                        is.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    /**
                                     * 下载错误回调
                                     */
                                    sendDownLoadErrorMessage(e);
                                }
                                try {
                                    if (fos != null)
                                        fos.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    /**
                                     * 下载错误回调
                                     */
                                    sendDownLoadErrorMessage(e);
                                }
                            }
                        } else {
                            /**
                             * 下载错误回调
                             */
                            sendDownLoadErrorMessage(new NullPointerException("ResponseBody为空！"));
                        }
                    }
                });
                if (executor == null || executor.isShutdown())
                    executor = Executors.newCachedThreadPool();
                executor.execute(thread);
            } else {
                /**
                 * 下载错误回调
                 */
                sendDownLoadErrorMessage(new RuntimeException("创建文件失败！"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            /**
             * 下载错误回调
             */
            sendDownLoadErrorMessage(e);
        }
        return this;
    }

    /**
     * 写入文件
     *
     * @param responseBody 待写入内容
     * @param file         存入的文件
     */
    private void writeRandomAccessFile(ResponseBody responseBody, File file) {
        RandomAccessFile randomAccessFile = null;
        InputStream inputStream = null;
        int progress;
        int writeLength = 0;
        try {
            randomAccessFile = new RandomAccessFile(file, "rwd");
            randomAccessFile.seek(downLoadInfoBean.getReadLength());
            inputStream = responseBody.byteStream();
            int len;
            byte[] buffer = new byte[1024 * 100];
            while ((len = inputStream.read(buffer)) != -1) {
                randomAccessFile.write(buffer, 0, len);

                if (onDownLoadListener != null && downLoadInfoBean.getContentLength() != 0) {
                    writeLength = writeLength + len;
                    progress = (int) (100 * downLoadInfoBean.getReadLength() / downLoadInfoBean.getContentLength());
                    if (100 * writeLength / downLoadInfoBean.getContentLength() >= 1 || progress >= 99) {
                        writeLength = 0;
                        sendonDownLoadMessage(progress);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 写入失败
//            if (onDownLoadListener != null)
//                onDownLoadListener.onDownLoadError(e);
            sendDownLoadErrorMessage(e);
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (randomAccessFile != null)
                    randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 开始下载回调
     */
    private void sendDownLoadStartMessage() {
        if (mHandler == null)
            mHandler = new MyHandler(this);
        mHandler.sendEmptyMessage(0);
    }

    /**
     * 正在下载回调
     */
    private void sendonDownLoadMessage(int progress) {
        Message message = Message.obtain();
        message.what = 1;
        message.obj = progress;
        if (mHandler == null)
            mHandler = new MyHandler(this);
        mHandler.sendMessage(message);
    }

    /**
     * 下载错误回调
     */
    private void sendDownLoadErrorMessage(Exception e) {
        Message message = Message.obtain();
        message.what = 3;
        message.obj = e;
        if (mHandler == null) {
            mHandler = new MyHandler(this);
        }
        mHandler.sendMessage(message);
    }

    /**
     * 下载完成回调
     */
    private void sendonDownLoadCompletedMessage(File file) {
        Message message = Message.obtain();
        message.what = 2;
        message.obj = file;
        if (mHandler == null)
            mHandler = new MyHandler(this);
        mHandler.sendMessage(message);
    }

    // 下载回调接口
    public interface OnDownLoadListener {
        void onDownLoadStart();

        void onDownLoad(int progress);

        void onDownLoadError(Exception e);

        void onDownLoadCompleted(File file);
    }

    private OnDownLoadListener onDownLoadListener;

    public FileDownLoadUtil setOnDownLoadListener(OnDownLoadListener onDownLoadListener) {
        this.onDownLoadListener = onDownLoadListener;
        return this;
    }
}
