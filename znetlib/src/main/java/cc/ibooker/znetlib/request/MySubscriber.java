package cc.ibooker.znetlib.request;

import android.text.TextUtils;

import java.net.SocketTimeoutException;

import cc.ibooker.znetlib.dto.ErrorData;
import retrofit2.HttpException;
import rx.Subscriber;

/**
 * 自定义Subscriber
 *
 * @author 邹峰立
 */
public abstract class MySubscriber<T> extends Subscriber<T> {

    @Override
    public void onError(Throwable e) {
        if (e == null || e instanceof NullException) {
            onError(new ErrorData());
            return;
        }
        ErrorData errorData = new ErrorData("发生未知异常", -2);
        if (e instanceof SocketTimeoutException) {
            errorData.setMsg("网络超时，请稍后再试");
            errorData.setCode(-1);
        } else if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            int code = httpException.code();
            errorData.setCode(code);
            if (200 <= code && code < 300) {
                errorData.setMsg(e.getMessage());
            } else if (400 == code) {
                errorData.setMsg("错误请求");
            } else if (401 == code) {
                errorData.setMsg("未授权");
                onLogin(errorData);
                return;
            } else if (500 <= code && code < 600) {
                errorData.setMsg("服务端异常");
            }
        } else if (e instanceof NoNetException) {
            errorData.setMsg("当前网络不给力！");
            errorData.setCode(-5);
        } else if (e instanceof LoginException) {
            onLogin(((LoginException) e).getErrorData());
            return;
        } else if (e instanceof RunException) {
            errorData = ((RunException) e).getErrorData();
        } else {
            errorData.setMsg(e.getMessage());
        }
        if (TextUtils.isEmpty(errorData.getMsg()))
            errorData.setMsg("发生未知异常！");
        onError(errorData);
    }

    protected abstract void onError(ErrorData errorData);

    public void onLogin(ErrorData errorData) {
    }
}
