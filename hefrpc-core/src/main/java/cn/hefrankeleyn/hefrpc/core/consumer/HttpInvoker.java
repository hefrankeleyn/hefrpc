package cn.hefrankeleyn.hefrpc.core.consumer;

import cn.hefrankeleyn.hefrpc.core.api.RpcRequest;
import cn.hefrankeleyn.hefrpc.core.api.RpcResponse;
import cn.hefrankeleyn.hefrpc.core.consumer.http.OkHttpInvoker;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.util.Objects;

/**
 * @Date 2024/3/24
 * @Author lifei
 */
public interface HttpInvoker {

    RpcResponse<?> post(RpcRequest request, String url);

    String get(String url);
    String post(String url, String requestBody);

    HttpInvoker DEFAULT = new OkHttpInvoker(5000);

    static <T> T httpGet(String url, Class<T> clazz) {
        try {
            String res = DEFAULT.get(url);
            if (Objects.isNull(res)) {
                return null;
            }
            return new Gson().fromJson(res, clazz);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static <T> T httpGet(String url, TypeToken<T> typeToken) {
        try {
            String res = DEFAULT.get(url);
            if (Objects.isNull(res)) {
                return null;
            }
            return new Gson().fromJson(res, typeToken.getType());
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    static <T> T httpPost(String url, String requestBody, Class<T> clazz) {
        try {
            String res = DEFAULT.post(url, requestBody);
            if (Objects.isNull(res)) {
                return null;
            }
            return new Gson().fromJson(res, clazz);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static <T> T httpPost(String url, String requestBody, TypeToken<T> typeToken) {
        try {
            String res = DEFAULT.post(url, requestBody);
            if (Objects.isNull(res)) {
                return null;
            }
            return new Gson().fromJson(res, typeToken.getType());
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
