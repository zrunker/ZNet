package cc.ibooker.znetlib.listeners;

import cc.ibooker.znetlib.dto.ErrorData;

/**
 * 网络请求回调接口
 *
 * @author 邹峰立
 */
public interface OnModelListener<T> {
    void onStart();

    void onError(ErrorData errorData);

    void onLogin(ErrorData errorData);

    void onCompleted();

    void onNext(T data);
}