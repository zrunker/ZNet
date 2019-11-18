package cc.ibooker.znetlib.util;

import com.google.gson.Gson;

/**
 * Gson操作类
 *
 * @author 邹峰立
 */
public class GsonUtil {
    private static GsonUtil gsonUtil;

    public static GsonUtil getInstance() {
        if (gsonUtil == null) {
            gsonUtil = new GsonUtil();
        }
        return gsonUtil;
    }

    /**
     * 对象转换成JSON字符串
     */
    public String objectToJson(Object object) {
        return new Gson().toJson(object);
    }

}
