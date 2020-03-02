package cc.ibooker.znetlib.request;

import android.text.TextUtils;

import com.google.gson.Gson;

import java.net.SocketTimeoutException;

import cc.ibooker.znetlib.dto.ErrorData;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import rx.Subscriber;

/**
 * 自定义订阅
 *
 * @author 邹峰立
 */
public abstract class MySubscriber<T> extends Subscriber<T> {

    @Override
    public void onError(Throwable e) {
        ErrorData errorData = new ErrorData("发生未知异常！", -2);
        if (e instanceof SocketTimeoutException) {
            errorData = new ErrorData("网络超时，请稍后再试", -1);
        } else if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            int code = httpException.code();
            if (200 <= code && code < 300) {// 成功-实际上不包括
                errorData = new ErrorData(e.getMessage(), code);
            } else if (401 == code) {// 服务端要求401强制登录
                errorData = new ErrorData("登录失效", code);
                onLogin(errorData);
            } else if (500 <= code && code < 600) {// 服务端异常
                errorData = new ErrorData("服务端异常", code);
            } else {
                ResponseBody responseBody = httpException.response().errorBody();
                if (responseBody != null) {
                    try {
                        String body = responseBody.string();
                        errorData = new Gson().fromJson(body, ErrorData.class);
                    } catch (Exception e1) {
                        errorData = new ErrorData(e1.getMessage(), 10);
                    } finally {
                        responseBody.close();
                    }
                }
            }
        } else {
            errorData = new ErrorData(e.getMessage(), 10);
        }
        if (TextUtils.isEmpty(errorData.getMsg()))
            errorData.setMsg("发生未知异常！");
        onError(errorData);
    }

    protected abstract void onError(ErrorData errorData);

    protected abstract void onLogin(ErrorData errorData);
}
