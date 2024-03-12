package cn.hefrankeleyn.hefrpc.core.consumer;

import cn.hefrankeleyn.hefrpc.core.annotation.HefConsumer;
import cn.hefrankeleyn.hefrpc.core.handler.HefConsumerHandler;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * @Date 2024/3/11
 * @Author lifei
 */
public class ConsumerBootstrap implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private Map<String, Object> stub = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private List<Field> getConsumerField(Class<?> beanClass) {
        List<Field> result = new ArrayList<>();
        while (Objects.nonNull(beanClass)) {
            Field[] fields = beanClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(HefConsumer.class)) {
                    result.add(field);
                }
            }
            beanClass = beanClass.getSuperclass();
        }
        return result;
    }



    public void scanningFields() {
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);
            Field[] fields = bean.getClass().getDeclaredFields();
            if (Objects.isNull(fields) || fields.length==0) {
                continue;
            }
            List<Field> consumerFieldList = getConsumerField(bean.getClass());

            if (Objects.isNull(consumerFieldList) || consumerFieldList.size()==0) {
                continue;
            }
            try {
                for (Field field : consumerFieldList) {
                    String serviceName = field.getType().getCanonicalName();
                    Object proxyObj = null;
                    if (stub.containsKey(serviceName)) {
                        proxyObj = stub.get(serviceName);
                    } else {
                        proxyObj = fetchProxyObj(field.getType());
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

    private Object fetchProxyObj(Class<?> service) {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{service},
                new HefConsumerHandler(service.getCanonicalName()));
    }
}
