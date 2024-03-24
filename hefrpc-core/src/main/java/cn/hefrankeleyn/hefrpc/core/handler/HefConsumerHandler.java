package cn.hefrankeleyn.hefrpc.core.handler;

import cn.hefrankeleyn.hefrpc.core.api.HefrpcContent;
import cn.hefrankeleyn.hefrpc.core.api.RpcRequest;
import cn.hefrankeleyn.hefrpc.core.api.RpcResponse;
import cn.hefrankeleyn.hefrpc.core.utils.HefRpcMethodUtils;
import cn.hefrankeleyn.hefrpc.core.utils.TypeUtils;
import com.google.gson.Gson;
import okhttp3.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Date 2024/3/11
 * @Author lifei
 */
public class HefConsumerHandler implements InvocationHandler {

    private String service;
    private List<String> providers;
    private HefrpcContent hefrpcContent;

    public HefConsumerHandler(String service, List<String> providers, HefrpcContent hefrpcContent) {
        this.service = service;
        this.providers = providers;
        this.hefrpcContent = hefrpcContent;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();
        request.setArgs(args);
        String methodSign = HefRpcMethodUtils.createMethodSign(method);
        if (HefRpcMethodUtils.checkLocalMethod(method)) {
            return null;
        }
        request.setMethodSign(methodSign);
        request.setService(this.service);
        RpcResponse response = fetchHttpRpcResponse(request, method);
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

    private RpcResponse fetchHttpRpcResponse(RpcRequest request, Method method) {
        try {
            Gson gson = new Gson();
            String jsonRequest = gson.toJson(request);
            String url = (String) hefrpcContent.getLoadBalance().choose(hefrpcContent.getRouter().route(providers));
            System.out.println("loadBalance.choose(urls) => " + url);
            Request okRequest = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(jsonRequest, MediaType.get("application/json; charset=utf-8")))
                    .build();
            Response okResponse = client.newCall(okRequest).execute();
            RpcResponse result = TypeUtils.getRpcResponse(method, okResponse);
            return result;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
