package cn.hefrankeleyn.hefrpc.core.consumer.http;

import cn.hefrankeleyn.hefrpc.core.api.RpcRequest;
import cn.hefrankeleyn.hefrpc.core.api.RpcResponse;
import cn.hefrankeleyn.hefrpc.core.consumer.HttpInvoker;
import cn.hefrankeleyn.hefrpc.core.utils.TypeUtils;
import com.google.gson.Gson;
import okhttp3.*;

import java.util.concurrent.TimeUnit;

/**
 * @Date 2024/3/24
 * @Author lifei
 */
public class OkHttpInvoker implements HttpInvoker {

    private final OkHttpClient client;

    public OkHttpInvoker(){
        client = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
                .connectTimeout(1, TimeUnit.SECONDS) // 建立HTTP连接的超时时间
                .readTimeout(1, TimeUnit.SECONDS)  //
                .writeTimeout(1, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public RpcResponse<?> post(RpcRequest request, String url) {
        try {
            Gson gson = new Gson();
            String jsonRequest = gson.toJson(request);
            Request okRequest = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(jsonRequest, MediaType.get("application/json; charset=utf-8")))
                    .build();
            Response okResponse = client.newCall(okRequest).execute();
            RpcResponse result = gson.fromJson(okResponse.body().string(), RpcResponse.class);
            return result;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
