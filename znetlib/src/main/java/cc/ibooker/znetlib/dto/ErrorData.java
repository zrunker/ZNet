package cc.ibooker.znetlib.dto;

/**
 * Http Status 错误数据处理
 *
 * @author 邹峰立
 */
public class ErrorData {
    private String msg;
    private int code;
    private String subCode;

    public ErrorData() {
        super();
    }

    public ErrorData(String msg, int code) {
        this.msg = msg;
        this.code = code;
    }

    public ErrorData(String msg, int code, String subCode) {
        this.msg = msg;
        this.code = code;
        this.subCode = subCode;
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

    public String getSubCode() {
        return subCode;
    }

    public void setSubCode(String subCode) {
        this.subCode = subCode;
    }

    @Override
    public String toString() {
        return "ErrorData{" +
                "msg='" + msg + '\'' +
                ", code=" + code +
                ", subCode='" + subCode + '\'' +
                '}';
    }
}
