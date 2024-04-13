package cn.hefrankeleyn.hefrpc.core.handler;

import cn.hefrankeleyn.hefrpc.core.api.*;
import cn.hefrankeleyn.hefrpc.core.consumer.HttpInvoker;
import cn.hefrankeleyn.hefrpc.core.consumer.http.OkHttpInvoker;
import cn.hefrankeleyn.hefrpc.core.governance.SlidingTimeWindow;
import cn.hefrankeleyn.hefrpc.core.meta.InstanceMeta;
import cn.hefrankeleyn.hefrpc.core.utils.HefRpcMethodUtils;
import cn.hefrankeleyn.hefrpc.core.utils.TypeUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Date 2024/3/11
 * @Author lifei
 */
public class HefConsumerHandler implements InvocationHandler {
    private static final Logger log = LoggerFactory.getLogger(HefConsumerHandler.class);

    private final String service;
    private List<InstanceMeta> providers = Lists.newArrayList();
    private final HefrpcContent hefrpcContent;
    private final Map<String, SlidingTimeWindow> slidingTimeWindowMap = Maps.newHashMap();

    private final List<InstanceMeta> isolatedProviders = Lists.newArrayList();
    private final List<InstanceMeta> halfOpenProviders = Lists.newArrayList();

    private final Long timeout;

    private final HttpInvoker httpInvoker;
    private final ScheduledExecutorService executorService;

    public HefConsumerHandler(String service, List<InstanceMeta> providers, HefrpcContent hefrpcContent) {
        this.service = service;
        this.providers = providers;
        this.hefrpcContent = hefrpcContent;
        timeout = Long.parseLong(hefrpcContent.getParameters().getOrDefault("app.timeout", "1000"));
        httpInvoker = new OkHttpInvoker(timeout);
        this.executorService = Executors.newScheduledThreadPool(1);
        this.executorService.scheduleWithFixedDelay(this::halfOpen, 10, 30, TimeUnit.SECONDS);
    }

    public void halfOpen() {
        log.debug("===> halfOpenProviders: {}， isolatedProviders: {}, providers: {}", halfOpenProviders, isolatedProviders, providers);
        this.halfOpenProviders.clear();
        this.halfOpenProviders.addAll(isolatedProviders);
        log.debug("===> halfOpenProviders: {}， isolatedProviders: {}, providers: {}", halfOpenProviders, isolatedProviders, providers);
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
                RpcResponse<?> rpcResponse = null;
                Object result = null;
                InstanceMeta instanceMeta= null;
                synchronized (halfOpenProviders) {
                    if (halfOpenProviders.isEmpty()) {
                        instanceMeta = hefrpcContent.getLoadBalance().choose(hefrpcContent.getRouter().route(providers));
                        log.debug("===> loadBalance.choose: {}", instanceMeta);
                    } else {
                        instanceMeta = halfOpenProviders.remove(0);
                        log.debug("===> check alive instance : {}", instanceMeta);
                    }
                }
                String url = instanceMeta.toUrl();
                try {
                    log.debug("==> current url: {}", url);
                    rpcResponse = httpInvoker.post(request, url);
                    result = castReturnResult(rpcResponse, method);
                }catch (Exception e) {
                    // 故障的规则判断和隔离
                    // 创建一个环，记录 多长时间间隔内发生异常的次数
                    // 每次异常记录异常，记录30m的异常次数
                    slidingTimeWindowMap.putIfAbsent(url, new SlidingTimeWindow());
                    SlidingTimeWindow slidingTimeWindow = slidingTimeWindowMap.get(url);
                    slidingTimeWindow.record(System.currentTimeMillis());
                    log.info("instance {} in window with {}", url, slidingTimeWindow.getSum());
                    // 30秒内发生10次，就进行故障隔离
                    if (slidingTimeWindow.getSum() >= 10) {
                        isolate(instanceMeta);
                    }
                    throw e;
                }

                synchronized (providers) {
                    if (!providers.contains(instanceMeta)) {
                        isolatedProviders.remove(instanceMeta);
                        providers.add(instanceMeta);
                        log.debug("===> instance: {}, providers: {}, halfOpenProviders : {}", instanceMeta, providers, halfOpenProviders);
                    }
                }

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

    private void isolate(InstanceMeta instanceMeta) {
        log.debug("===> isolate instance : " + instanceMeta);
        providers.remove(instanceMeta);
        log.debug("===> providers: {}", providers);
        isolatedProviders.add(instanceMeta);
        log.debug("===> isolatedProviders: {}", isolatedProviders);
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
            InstanceMeta instanceMeta = hefrpcContent.getLoadBalance().choose(hefrpcContent.getRouter().route(providers));
            String url = instanceMeta.toUrl();
            log.debug("loadBalance.choose(urls) => " + url);
            RpcResponse<?> rpcResponse = httpInvoker.post(request, url);
            return TypeUtils.getRpcResponse(method, rpcResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
