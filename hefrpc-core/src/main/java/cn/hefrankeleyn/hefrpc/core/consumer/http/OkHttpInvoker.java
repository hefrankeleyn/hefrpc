package cn.hefrankeleyn.hefrpc.core.consumer.http;

import cn.hefrankeleyn.hefrpc.core.api.RpcRequest;
import cn.hefrankeleyn.hefrpc.core.api.RpcResponse;
import cn.hefrankeleyn.hefrpc.core.consumer.HttpInvoker;
import com.alibaba.fastjson.JSON;

import static com.google.common.base.Preconditions.*;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Date 2024/3/24
 * @Author lifei
 */
public class OkHttpInvoker implements HttpInvoker {

    private static final Logger log = LoggerFactory.getLogger(OkHttpInvoker.class);

    private static final MediaType MEDIATYPE_JSON = MediaType.parse("application/json; charset=utf-8");

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
                    .post(RequestBody.create(jsonRequest, MEDIATYPE_JSON))
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

    @Override
    public String get(String url) {
        Request request = new Request.Builder().get().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            checkState(response.isSuccessful(), "Unexpected code " + response);
            ResponseBody body = response.body();
            if (Objects.nonNull(body)) {
                String result = body.string();
                log.debug("===> get success: " + result);
                return result;
            }
            return null;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }





    @Override
    public String post(String url, String requestBody) {
        Request request = new Request.Builder().post(RequestBody.create(requestBody, MEDIATYPE_JSON)).url(url).build();
        try {
            Response response = client.newCall(request).execute();
            checkState(response.isSuccessful(), "Unexpected code " + response);
            ResponseBody body = response.body();
            if (Objects.nonNull(body)) {
                String result = body.string();
                log.debug("===> post success: " + result);
                return result;
            }
            return null;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
