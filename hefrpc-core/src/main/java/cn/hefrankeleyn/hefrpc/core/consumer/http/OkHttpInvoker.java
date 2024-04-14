package cn.hefrankeleyn.hefrpc.core.consumer.http;

import cn.hefrankeleyn.hefrpc.core.api.RpcRequest;
import cn.hefrankeleyn.hefrpc.core.api.RpcResponse;
import cn.hefrankeleyn.hefrpc.core.consumer.HttpInvoker;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @Date 2024/3/24
 * @Author lifei
 */
public class OkHttpInvoker implements HttpInvoker {

    private static final Logger log = LoggerFactory.getLogger(OkHttpInvoker.class);

    private final OkHttpClient client;

    public OkHttpInvoker(){
        this(1000);
    }

    public OkHttpInvoker(long timeout){
        client = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
                .connectTimeout(timeout, TimeUnit.MILLISECONDS) // 建立HTTP连接的超时时间
                .readTimeout(timeout, TimeUnit.MILLISECONDS)  //
                .writeTimeout(timeout, TimeUnit.MILLISECONDS)
                .build();
    }

    @Override
    public RpcResponse<?> post(RpcRequest request, String url) {
        try {

            String jsonRequest = new Gson().toJson(request);
            Request okRequest = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(jsonRequest, MediaType.get("application/json; charset=utf-8")))
                    .build();
            Response okResponse = client.newCall(okRequest).execute();
//            RpcResponse result = new Gson().fromJson(okResponse.body().string(), RpcResponse.class);
            String repJson = okResponse.body().string();
            log.debug(repJson);
            RpcResponse<Object> result = JSON.parseObject(repJson, RpcResponse.class);
            return result;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
