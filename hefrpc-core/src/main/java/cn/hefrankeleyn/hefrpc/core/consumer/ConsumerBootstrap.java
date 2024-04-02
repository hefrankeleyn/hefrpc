package cn.hefrankeleyn.hefrpc.core.consumer;

import cn.hefrankeleyn.hefrpc.core.annotation.HefConsumer;
import cn.hefrankeleyn.hefrpc.core.api.*;
import cn.hefrankeleyn.hefrpc.core.handler.HefConsumerHandler;
import cn.hefrankeleyn.hefrpc.core.meta.InstanceMeta;
import cn.hefrankeleyn.hefrpc.core.meta.ServiceMeta;
import cn.hefrankeleyn.hefrpc.core.utils.HefRpcMethodUtils;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Date 2024/3/11
 * @Author lifei
 */
public class ConsumerBootstrap implements ApplicationContextAware, EnvironmentAware {
    private static final Logger log = LoggerFactory.getLogger(ConsumerBootstrap.class);

    private ApplicationContext applicationContext;
    private Environment environment;
    private Map<String, Object> stub = new HashMap<>();

    private String app;
    private String namespace;
    private String env;
    private int retries;
    private long timeout;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }



    public void scanningFields() {
        RegistryCenter registryCenter = applicationContext.getBean(RegistryCenter.class);
        Router<InstanceMeta> router = applicationContext.getBean(Router.class);
        LoadBalance<InstanceMeta> loadBalance = applicationContext.getBean(LoadBalance.class);
        List<Filter> filters = Lists.newArrayList(applicationContext.getBeansOfType(Filter.class).values());
        HefrpcContent hefrpcContent = new HefrpcContent(loadBalance, router);
        hefrpcContent.setFilterList(filters);
        app = environment.getProperty("app.id");
        namespace = environment.getProperty("app.namespace");
        env = environment.getProperty("app.env");
        retries = Integer.parseInt(environment.getProperty("app.retries"));
        timeout = Long.parseLong(environment.getProperty("app.timeout"));
        hefrpcContent.getParameters().put("app.retries", String.valueOf(retries));
        hefrpcContent.getParameters().put("app.timeout", String.valueOf(timeout));
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);
            Field[] fields = bean.getClass().getDeclaredFields();
            if (Objects.isNull(fields) || fields.length==0) {
                continue;
            }
            List<Field> consumerFieldList = HefRpcMethodUtils.getAnnotationField(bean.getClass(), HefConsumer.class);

            if (Objects.isNull(consumerFieldList) || consumerFieldList.size()==0) {
                continue;
            }
            try {
                for (Field field : consumerFieldList) {
                    String serviceName = field.getType().getCanonicalName();
                    Object proxyObj;
                    if (stub.containsKey(serviceName)) {
                        proxyObj = stub.get(serviceName);
                    } else {
                        proxyObj = createFromRegistry(field.getType(), hefrpcContent, registryCenter);
                        stub.put(serviceName, proxyObj);
                    }
                    field.setAccessible(true);
                    field.set(bean, proxyObj);
                }
            }catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Object createFromRegistry(Class<?> type, HefrpcContent hefrpcContent, RegistryCenter registryCenter) {
        String serviceName = type.getCanonicalName();
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .name(serviceName)
                .app(app)
                .env(env)
                .namespace(namespace)
                .build();
        List<InstanceMeta> instanceMetaList = registryCenter.findAll(serviceMeta);
        log.info("====> nodes map to providers");
        instanceMetaList.forEach(System.out::println);
        registryCenter.subscribe(serviceMeta, (event)->{
            instanceMetaList.clear();
            instanceMetaList.addAll(event.getData());
        });
        return fetchProxyObj(type, instanceMetaList, hefrpcContent);
    }

    private List<String> mapToUrls(List<String> nodes) {
        return  nodes.stream().map(node->Strings.lenientFormat("http://%s/", node.replace("_", ":")))
                .collect(Collectors.toList());
    }

    private List<String> mapInstanceToUrls(List<InstanceMeta> nodes) {
        return  nodes.stream().map(node->Strings.lenientFormat("%s://%s:%s/",
                        node.getSchema(), node.getHost(), node.getPort()))
                .collect(Collectors.toList());
    }

    private Object fetchProxyObj(Class<?> service, List<InstanceMeta> instanceMetaList, HefrpcContent hefrpcContent) {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{service},
                new HefConsumerHandler(service.getCanonicalName(), instanceMetaList, hefrpcContent));
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}
