package cc.ibooker.znetlib.request;

import cc.ibooker.znetlib.dto.ErrorData;

/**
 * 运行期异常
 *
 * @author 邹峰立
 */
public class RunException extends RuntimeException {
    private ErrorData errorData;

    public RunException(ErrorData errorData) {
        this.errorData = errorData;
    }

    public void setErrorData(ErrorData errorData) {
        this.errorData = errorData;
    }

    public ErrorData getErrorData() {
        return errorData;
    }

}
