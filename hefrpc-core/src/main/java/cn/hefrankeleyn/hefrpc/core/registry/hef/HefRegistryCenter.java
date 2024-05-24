package cn.hefrankeleyn.hefrpc.core.registry.hef;

import cn.hefrankeleyn.hefrpc.core.api.RegistryCenter;
import cn.hefrankeleyn.hefrpc.core.consumer.HttpInvoker;
import cn.hefrankeleyn.hefrpc.core.meta.InstanceMeta;
import cn.hefrankeleyn.hefrpc.core.meta.ServiceMeta;
import cn.hefrankeleyn.hefrpc.core.registry.ChangedListener;
import cn.hefrankeleyn.hefrpc.core.registry.Event;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Date 2024/5/23
 * @Author lifei
 */
public class HefRegistryCenter implements RegistryCenter {

    private static final Logger log = LoggerFactory.getLogger(HefRegistryCenter.class);

    @Value("${hefregistry.servers}")
    private String registryServer;

    private Map<String, Long> VERSIONS = Maps.newHashMap();
    private MultiValueMap<InstanceMeta, ServiceMeta> INSTANCES = new LinkedMultiValueMap<>();

    private ScheduledExecutorService consumerExecutor;
    private ScheduledExecutorService providerExecutor;


    @Override
    public void start() {
        log.debug("====> [HefRegistry]: start with servers: {}", registryServer);
        consumerExecutor = Executors.newSingleThreadScheduledExecutor();
        providerExecutor = Executors.newSingleThreadScheduledExecutor();
        providerExecutor.scheduleWithFixedDelay(()->{
            INSTANCES.keySet().parallelStream().forEach(instance->{
                String services = INSTANCES.get(instance).stream().map(ServiceMeta::toPath).collect(Collectors.joining(","));
                String requestBody = new Gson().toJson(instance);
                Long timestamp = HttpInvoker.httpPost(Strings.lenientFormat("%s/renews?services=%s", registryServer, services), requestBody, Long.class);
                log.debug("===> [HefRegistry] timestamp {},  check alive for instance {}, services: {}", timestamp, instance, services);
            });
        }, 5, 5, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        log.debug("====> [HefRegistry]: stop with servers: {}", registryServer);
        gracefulShutdown(consumerExecutor);
    }

    /**
     * 优雅的关闭
     * @param executor
     */
    private void gracefulShutdown(ScheduledExecutorService executor) {
        executor.shutdown();
        try {
            executor.awaitTermination(1000, TimeUnit.MILLISECONDS);
            if (!executor.isTerminated()) {
                executor.shutdownNow();
            }
        }catch (Exception e) {
            // ignore
        }
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        log.debug("====> [HefRegistry]: registr instance {} for service {}", instance, service);
        String url = Strings.lenientFormat("%s/register?service=%s", registryServer, service.toPath());
        InstanceMeta instanceMeta = HttpInvoker.httpPost(url, new Gson().toJson(instance), InstanceMeta.class);
        log.debug("====> [hefRegistry]: register url : {}", url);
        if (Objects.nonNull(instanceMeta)) {
            INSTANCES.add(instanceMeta, service);
        }
        log.debug("===> [HefRegistry]: registr instance {}", instanceMeta);
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        log.debug("====> [HefRegistry]: unregister instance {} for service {}", instance, service);
        InstanceMeta instanceMeta = HttpInvoker.httpPost(Strings.lenientFormat("%s/unregister?service=%s", registryServer, service.toPath()),
                new Gson().toJson(instance), InstanceMeta.class);
        INSTANCES.remove(instanceMeta, service);
        log.debug("===> [HefRegistry]: unregister instance {}", instanceMeta);
    }

    @Override
    public List<InstanceMeta> findAll(ServiceMeta service) {
        log.debug("====> [HefRegistry]: findAll instances for service {}", service);
        List<InstanceMeta> instanceList = HttpInvoker.httpGet(Strings.lenientFormat("%s/findAllInstances?service=%s", registryServer, service.toPath()),
                new TypeToken<List<InstanceMeta>>() {});
        log.debug("===> [HefRegistry]: findAll instance: {}", instanceList);
        return instanceList;
    }



    @Override
    public void subscribe(ServiceMeta service, ChangedListener changedListener) {
        consumerExecutor.scheduleWithFixedDelay(()->{
            Long oldVersion = VERSIONS.getOrDefault((Object) service.toPath(), -1L);
            Long newVersion = HttpInvoker.httpGet(Strings.lenientFormat("%s/version?service=%s", registryServer, service.toPath()), Long.class);
            log.debug("===> [HefRegistry] new version: {}, old version: {}", newVersion, oldVersion);
            if (Optional.ofNullable(newVersion).orElse(-1L) > oldVersion) {
                List<InstanceMeta> instanceList = findAll(service);
                changedListener.fire(new Event(instanceList));
                VERSIONS.put(service.toPath(), newVersion);
            }

        }, 0, 1000, TimeUnit.MILLISECONDS);
    }
}
