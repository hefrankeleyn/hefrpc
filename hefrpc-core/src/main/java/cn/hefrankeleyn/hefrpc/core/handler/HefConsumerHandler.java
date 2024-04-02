package cn.hefrankeleyn.hefrpc.core.handler;

import cn.hefrankeleyn.hefrpc.core.api.*;
import cn.hefrankeleyn.hefrpc.core.consumer.HttpInvoker;
import cn.hefrankeleyn.hefrpc.core.consumer.http.OkHttpInvoker;
import cn.hefrankeleyn.hefrpc.core.meta.InstanceMeta;
import cn.hefrankeleyn.hefrpc.core.utils.HefRpcMethodUtils;
import cn.hefrankeleyn.hefrpc.core.utils.TypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
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

    private Long timeout;

    private HttpInvoker httpInvoker;

    public HefConsumerHandler(String service, List<InstanceMeta> instanceMetaList, HefrpcContent hefrpcContent) {
        this.service = service;
        this.instanceMetaList = instanceMetaList;
        this.hefrpcContent = hefrpcContent;
        timeout = Long.parseLong(hefrpcContent.getParameters().getOrDefault("app.retries", "1000"));
        httpInvoker = new OkHttpInvoker(timeout);
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
        int retries = Integer.parseInt(hefrpcContent.getParameters().get("app.retries"));
        while (retries-- > 0) {
            try {
                log.info("===> reties: " + retries);
                List<Filter> filterList = hefrpcContent.getFilterList();
                for (Filter filter : filterList) {
                    Object o = filter.preFilter(request);
                    if (Objects.nonNull(o)) {
                        log.info(filter.getClass().getName() + " ===> preFilter: " + o);
                        return o;
                    }
                }
                InstanceMeta instanceMeta = hefrpcContent.getLoadBalance().choose(hefrpcContent.getRouter().route(instanceMetaList));
                RpcResponse<?> rpcResponse = httpInvoker.post(request, instanceMeta.toUrl());
                Object result = castReturnResult(rpcResponse, method);
                for (Filter filter : filterList) {
                    Object o = filter.postFilter(request, rpcResponse, result);
                    if (Objects.nonNull(o)) {
                        return o;
                    }
                }
                return result;
            } catch (RuntimeException e) {
                if (!(e.getCause() instanceof SocketTimeoutException)) {
                    throw e;
                }
            }
        }
        return null;
    }

    private static Object castReturnResult(RpcResponse<?> rpcResponse, Method method) {
        if (!rpcResponse.isStatus()) {
            throw rpcResponse.getEx();
        } else {
            return TypeUtils.castFastJsonReturnObject(method, rpcResponse.getData());
        }
    }

    private RpcResponse<?> fetchHttpRpcResponse(RpcRequest request, Method method) {
        try {
            InstanceMeta instanceMeta = hefrpcContent.getLoadBalance().choose(hefrpcContent.getRouter().route(instanceMetaList));
            String url = instanceMeta.toUrl();
            log.debug("loadBalance.choose(urls) => " + url);
            RpcResponse<?> rpcResponse = httpInvoker.post(request, url);
            return TypeUtils.getRpcResponse(method, rpcResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
