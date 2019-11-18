package cc.ibooker.znetlib.dto;

/**
 * Http Status 错误数据处理
 *
 * @author 邹峰立
 */
public class ErrorData {
    private String msg;
    private int code;

    public ErrorData() {
        super();
    }

    public ErrorData(String msg, int code) {
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "ErrorData{" +
                "msg='" + msg + '\'' +
                ", code=" + code +
                '}';
    }
}
