package cn.hefrankeleyn.hefrpc.core.provider;

import cn.hefrankeleyn.hefrpc.core.annotation.HefProvider;
import cn.hefrankeleyn.hefrpc.core.api.RpcRequest;
import cn.hefrankeleyn.hefrpc.core.api.RpcResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.BeansException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Date 2024/3/7
 * @Author lifei
 */
public class ProviderBootstrap implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private Map<String, Object> skeletion = new HashMap<>();
    private static int objNum = 0;

    public RpcResponse invoke(RpcRequest request) {
        RpcResponse response= new RpcResponse();
        try {
            Object bean = skeletion.get(request.getService());
            String methodName = request.getMethod();
            Method[] objMethod = Object.class.getMethods();
            Set<String> objMethodSet = Arrays.stream(objMethod).map(Method::getName).collect(Collectors.toSet());
            if (objMethodSet.contains(methodName)) {
                response.setStatus(false);
                response.setData(null);
                return response;
            }
            Method method = getMethod(bean, methodName);

            Object data = method.invoke(bean, request.getArgs());
            response.setStatus(true);
            response.setData(data);
            return response;
        }catch (Exception e) {
            e.printStackTrace();
            response.setStatus(false);
            response.setData(null);
            return response;
        }
    }

    private Method getMethod(Object bean, String methodName) {
        Method[] methods = bean.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }


    /**
     * POSTConstruct 相当于 init-method
     */
    @PostConstruct
    public void buildProviders() {
        ++objNum;
        System.out.printf("装配第 %d 个对象\n", objNum);
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(HefProvider.class);
        System.out.println("beanSize" + beans.size());
        beans.values().forEach(
                (item)-> getInterface(item)
        );
    }

    /**
     * 默认只支持一个接口
     * @param o
     * @return
     */
    private void getInterface(Object o) {
        Class<?> clazz = o.getClass().getInterfaces()[0];
        skeletion.put(clazz.getCanonicalName(), o);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
