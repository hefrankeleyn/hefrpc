package cn.hefrankeleyn.hefrpc.core.handler;

import cn.hefrankeleyn.hefrpc.core.api.HefrpcContent;
import cn.hefrankeleyn.hefrpc.core.api.RpcRequest;
import cn.hefrankeleyn.hefrpc.core.api.RpcResponse;
import cn.hefrankeleyn.hefrpc.core.consumer.HttpInvoker;
import cn.hefrankeleyn.hefrpc.core.consumer.http.OkHttpInvoker;
import cn.hefrankeleyn.hefrpc.core.utils.HefRpcMethodUtils;
import cn.hefrankeleyn.hefrpc.core.utils.TypeUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @Date 2024/3/11
 * @Author lifei
 */
public class HefConsumerHandler implements InvocationHandler {

    private String service;
    private List<String> providers;
    private HefrpcContent hefrpcContent;

    private HttpInvoker httpInvoker = new OkHttpInvoker();

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

    private RpcResponse fetchHttpRpcResponse(RpcRequest request, Method method) {
        try {
            String url = (String) hefrpcContent.getLoadBalance().choose(hefrpcContent.getRouter().route(providers));
            System.out.println("loadBalance.choose(urls) => " + url);
            RpcResponse rpcResponse = httpInvoker.post(request, url);
            RpcResponse result = TypeUtils.getRpcResponse(method, rpcResponse);
            return result;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
