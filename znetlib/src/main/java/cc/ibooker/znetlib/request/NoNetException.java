package cc.ibooker.znetlib.request;

/**
 * 无网络异常
 *
 * @author 邹峰立
 */
public class NoNetException extends RuntimeException{

    public NoNetException(String message) {
        super(message);
    }
}
