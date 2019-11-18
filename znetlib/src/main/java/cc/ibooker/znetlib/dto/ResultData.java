package cc.ibooker.znetlib.dto;

/**
 * 返回数据格式类
 *
 * @author 邹峰立
 */
public class ResultData<T> {
    private int code;// 状态码
    private String msg;// 提示信息
    private T data;// 数据

    public ResultData() {
        super();
    }

    public ResultData(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResultData{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
