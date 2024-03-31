package cn.hefrankeleyn.hefrpc.core.handler;

import cn.hefrankeleyn.hefrpc.core.api.Filter;
import cn.hefrankeleyn.hefrpc.core.api.HefrpcContent;
import cn.hefrankeleyn.hefrpc.core.api.RpcRequest;
import cn.hefrankeleyn.hefrpc.core.api.RpcResponse;
import cn.hefrankeleyn.hefrpc.core.consumer.HttpInvoker;
import cn.hefrankeleyn.hefrpc.core.consumer.http.OkHttpInvoker;
import cn.hefrankeleyn.hefrpc.core.meta.InstanceMeta;
import cn.hefrankeleyn.hefrpc.core.utils.HefRpcMethodUtils;
import cn.hefrankeleyn.hefrpc.core.utils.TypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * @Date 2024/3/11
 * @Author lifei
 */
public class HefConsumerHandler implements InvocationHandler {
    private static final Logger log = LoggerFactory.getLogger(HefConsumerHandler.class);

    private String service;
    private List<InstanceMeta> instanceMetaList;
    private HefrpcContent hefrpcContent;

    private HttpInvoker httpInvoker = new OkHttpInvoker();

    public HefConsumerHandler(String service, List<InstanceMeta> instanceMetaList, HefrpcContent hefrpcContent) {
        this.service = service;
        this.instanceMetaList = instanceMetaList;
        this.hefrpcContent = hefrpcContent;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (HefRpcMethodUtils.checkLocalMethod(method)) {
            return null;
        }
        RpcRequest request = new RpcRequest();
        request.setArgs(args);
        request.setMethodSign(HefRpcMethodUtils.createMethodSign(method));
        request.setService(this.service);
        List<Filter> filterList = hefrpcContent.getFilterList();
        for (Filter filter : filterList) {
            Object o = filter.preFilter(request);
            if (Objects.nonNull(o)) {
                log.info(filter.getClass().getName() + " ===> preFilter: " + o);
                return o;
            }
        }
        RpcResponse<?> response = fetchHttpRpcResponse(request, method);
        if (!response.isStatus()) {
            throw new RuntimeException(response.getEx());
        }
        Object result = response.getData();
        for (Filter filter : filterList) {
            Object o = filter.postFilter(request, response, result);
            if (Objects.nonNull(o)) {
                return o;
            }
        }
        return result;
    }

    private RpcResponse<?> fetchHttpRpcResponse(RpcRequest request, Method method) {
        try {
            InstanceMeta instanceMeta = hefrpcContent.getLoadBalance().choose(hefrpcContent.getRouter().route(instanceMetaList));
            String url = instanceMeta.toUrl();
            log.debug("loadBalance.choose(urls) => " + url);
            RpcResponse<?> rpcResponse = httpInvoker.post(request, url);
            return TypeUtils.getRpcResponse(method, rpcResponse);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
