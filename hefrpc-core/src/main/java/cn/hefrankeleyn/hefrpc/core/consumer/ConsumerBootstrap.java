package cn.hefrankeleyn.hefrpc.core.consumer;

import cn.hefrankeleyn.hefrpc.core.annotation.HefConsumer;
import cn.hefrankeleyn.hefrpc.core.api.HefrpcContent;
import cn.hefrankeleyn.hefrpc.core.api.LoadBalance;
import cn.hefrankeleyn.hefrpc.core.api.RegistryCenter;
import cn.hefrankeleyn.hefrpc.core.api.Router;
import cn.hefrankeleyn.hefrpc.core.handler.HefConsumerHandler;
import cn.hefrankeleyn.hefrpc.core.meta.InstanceMeta;
import cn.hefrankeleyn.hefrpc.core.utils.HefRpcMethodUtils;
import com.google.common.base.Strings;
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

    private ApplicationContext applicationContext;
    private Environment environment;
    private Map<String, Object> stub = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }



    public void scanningFields() {
        RegistryCenter registryCenter = applicationContext.getBean(RegistryCenter.class);
        Router router = applicationContext.getBean(Router.class);
        LoadBalance loadBalance = applicationContext.getBean(LoadBalance.class);
        HefrpcContent hefrpcContent = new HefrpcContent(loadBalance, router);
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
        List<InstanceMeta> instanceMetaList = registryCenter.findAll(serviceName);
        System.out.println("====> nodes map to providers");
        instanceMetaList.forEach(System.out::println);
        registryCenter.subscribe(serviceName, (event)->{
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
