package cc.ibooker.znetlib.request;

import cc.ibooker.znetlib.dto.ErrorData;

/**
 * 登录异常
 *
 * @author 邹峰立
 */
public class LoginException extends RunException {

    public LoginException(ErrorData errorData) {
        super(errorData);
    }
}
