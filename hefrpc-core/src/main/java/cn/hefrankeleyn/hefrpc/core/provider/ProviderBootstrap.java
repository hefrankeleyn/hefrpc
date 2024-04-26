package cn.hefrankeleyn.hefrpc.core.provider;

import cn.hefrankeleyn.hefrpc.core.annotation.HefProvider;
import cn.hefrankeleyn.hefrpc.core.api.RegistryCenter;
import cn.hefrankeleyn.hefrpc.core.conf.AppConfigProperties;
import cn.hefrankeleyn.hefrpc.core.conf.ProviderBusConf;
import cn.hefrankeleyn.hefrpc.core.meta.InstanceMeta;
import cn.hefrankeleyn.hefrpc.core.meta.ProviderMeta;
import cn.hefrankeleyn.hefrpc.core.meta.ServiceMeta;
import cn.hefrankeleyn.hefrpc.core.utils.HefRpcMethodUtils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.*;

/**
 * @Date 2024/3/7
 * @Author lifei
 */
public class ProviderBootstrap implements ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(ProviderBootstrap.class);

    private ApplicationContext applicationContext;

    private InstanceMeta instance;

    private RegistryCenter registryCenter;

    // 1. 缓存，加快访问速度； 2.
    private MultiValueMap<String, ProviderMeta> skeletion = new LinkedMultiValueMap<>();

    private final Integer port;
    private final AppConfigProperties appConfigProperties;
    private final ProviderBusConf providerBusConf;

    public ProviderBootstrap(Integer port, AppConfigProperties appConfigProperties, ProviderBusConf providerBusConf) {
        this.port = port;
        this.appConfigProperties = appConfigProperties;
        this.providerBusConf = providerBusConf;
    }

    /**
     * POSTConstruct 相当于 init-method
     */
    @PostConstruct
    public void init() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(HefProvider.class);
        registryCenter = applicationContext.getBean(RegistryCenter.class);
        beans.values().forEach(
                (item) -> getInterface(item)
        );
    }

    public void start() {
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            instance = InstanceMeta.http("http", hostAddress, port, "hefrpc").addParams(providerBusConf.getMetas());
//            registryCenter.start();
            skeletion.keySet().forEach(this::registerService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void stop() {
        skeletion.keySet().forEach(this::unregisterService);
        registryCenter.stop();
    }


    private void registerService(String serviceName) {
        registryCenter.register(createServiceMeta(serviceName), instance);
    }

    private void unregisterService(String serviceName) {
        registryCenter.unregister(createServiceMeta(serviceName), instance);
    }

    private ServiceMeta createServiceMeta(String serviceName) {
        return ServiceMeta.builder()
                .name(serviceName)
                .app(appConfigProperties.getId())
                .env(appConfigProperties.getEnv())
                .namespace(appConfigProperties.getNamespace())
                .build();
    }

    /**
     * 默认只支持一个接口
     *
     * @param o
     * @return
     */
    private void getInterface(Object o) {
        for (Method method : o.getClass().getMethods()) {
            if (HefRpcMethodUtils.checkLocalMethod(method)) {
                continue;
            }
            ProviderMeta providerMeta = new ProviderMeta();
//            Class<?> clazz = o.getClass().getInterfaces()[0];
            for (Class<?> clazz : o.getClass().getInterfaces()) {
                String className = clazz.getCanonicalName();
                providerMeta.setMethodSign(HefRpcMethodUtils.createMethodSign(method));
                providerMeta.setMethod(method);
                providerMeta.setService(o);
                log.info(providerMeta.toString());
                skeletion.add(className, providerMeta);
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public MultiValueMap<String, ProviderMeta> getSkeletion() {
        return skeletion;
    }

    public ProviderBusConf getProviderBusConf() {
        return providerBusConf;
    }
}
