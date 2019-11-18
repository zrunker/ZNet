package cc.ibooker.znetlib.request;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;

import static okhttp3.internal.Util.UTF_8;

/**
 * 网络请求响应Converter
 *
 * @author 邹峰立
 */
public class MyResponseConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    MyResponseConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(@NonNull ResponseBody value) throws IOException {
//        JsonReader jsonReader = gson.newJsonReader(value.charStream());
//        jsonReader.setLenient(true);
//        try {
//            return adapter.read(jsonReader);
//        } finally {
//            value.close();
//        }

        InputStream is = null;
        Reader reader = null;
        JsonReader jsonReader = null;
        try {
            // 将ResponseBody转换为UTF8
            MediaType contentType = value.contentType();
            Charset charset = contentType != null ? contentType.charset(UTF_8) : UTF_8;

            // 转换成JSON
            is = value.byteStream();
            reader = new InputStreamReader(is, charset != null ? charset : UTF_8);
            jsonReader = gson.newJsonReader(reader);
            return adapter.read(jsonReader);
        } finally {
            if (is != null)
                is.close();
            if (reader != null)
                reader.close();
            if (jsonReader != null)
                jsonReader.close();
            value.close();
        }
    }
}
