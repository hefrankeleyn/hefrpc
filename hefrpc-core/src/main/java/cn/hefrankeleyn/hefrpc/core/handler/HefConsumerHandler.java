package cn.hefrankeleyn.hefrpc.core.handler;

import cn.hefrankeleyn.hefrpc.core.api.RpcRequest;
import cn.hefrankeleyn.hefrpc.core.api.RpcResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @Date 2024/3/11
 * @Author lifei
 */
public class HefConsumerHandler implements InvocationHandler {

    private String service;

    public HefConsumerHandler(String service) {
        this.service = service;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        request.setArgs(args);
        request.setMethod(method.getName());
        request.setService(this.service);

        RpcResponse response = fetchHttpRpcResponse(request, method.getReturnType());
        if (response.isStatus()) {
            return response.getData();
        }else {
            return null;
        }
    }

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
            .connectTimeout(1, TimeUnit.SECONDS) // 建立HTTP连接的超时时间
            .readTimeout(1, TimeUnit.SECONDS)  //
            .writeTimeout(1, TimeUnit.SECONDS)
            .build();

    private RpcResponse fetchHttpRpcResponse(RpcRequest request,Class<?> returnTpe) {
        try {
            Gson gson = new Gson();
            String jsonRequest = gson.toJson(request);
            Request okRequest = new Request.Builder()
                    .url("http://localhost:8080/")
                    .post(RequestBody.create(jsonRequest, MediaType.get("application/json; charset=utf-8")))
                    .build();
            Response okResponse = client.newCall(okRequest).execute();
            String dataStr = okResponse.body().string();
            TypeToken<?> parameterized = TypeToken.getParameterized(RpcResponse.class, returnTpe);
            RpcResponse result = gson.fromJson(dataStr, parameterized.getType());
            return result;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
