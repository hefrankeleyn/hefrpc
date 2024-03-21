package cn.hefrankeleyn.hefrpc.core.provider;

import cn.hefrankeleyn.hefrpc.core.annotation.HefProvider;
import cn.hefrankeleyn.hefrpc.core.api.RegistryCenter;
import cn.hefrankeleyn.hefrpc.core.api.RpcRequest;
import cn.hefrankeleyn.hefrpc.core.api.RpcResponse;
import cn.hefrankeleyn.hefrpc.core.utils.HefRpcMethodUtils;

import static com.google.common.base.Preconditions.*;

import cn.hefrankeleyn.hefrpc.core.utils.TypeUtils;
import com.google.common.base.Strings;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.BeansException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.*;

/**
 * @Date 2024/3/7
 * @Author lifei
 */
public class ProviderBootstrap implements ApplicationContextAware, EnvironmentAware {

    private ApplicationContext applicationContext;
    private Environment environment;

    private String instance;

    // 1. 缓存，加快访问速度； 2.
    private MultiValueMap<String, ProviderMeta> skeletion = new LinkedMultiValueMap<>();

    public RpcResponse invoke(RpcRequest request) {
        RpcResponse response = new RpcResponse();
        try {
            ProviderMeta providerMeta = findProviderMeta(request);
            System.out.println(Strings.lenientFormat("开始执行：%s", providerMeta));
            checkState(Objects.nonNull(providerMeta), "没有查到对方的方法：%s", request);
            Method method = providerMeta.getMethod();
            Object[] args = TypeUtils.processArgs(request.getArgs(), method.getParameterTypes(), method.getGenericParameterTypes());
            Object data = method.invoke(providerMeta.getService(), args);
            response.setStatus(true);
            response.setData(data);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(false);
            response.setData(null);
            return response;
        }
    }

    private ProviderMeta findProviderMeta(RpcRequest request) {
        List<ProviderMeta> providerMetas = skeletion.get(request.getService());
        ProviderMeta result = providerMetas.stream().filter(providerMeta -> providerMeta.getMethodSign().equals(request.getMethodSign()))
                .findFirst()
                .orElse(null);
        return result;
    }

    private boolean matchType(Class<?> parameterType, String argTypeName) {
        return parameterType.getCanonicalName().equals(argTypeName);
    }

    private Method getMethod(Object bean, RpcRequest request) {
        Method[] methods = bean.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().equals(request.getMethodSign())) {
                return method;
            }
        }
        return null;
    }


    /**
     * POSTConstruct 相当于 init-method
     */
    @PostConstruct
    public void init() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(HefProvider.class);
        beans.values().forEach(
                (item) -> getInterface(item)
        );
    }

    public void start() {
        try {
            String port = environment.getProperty("server.port");
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            instance = Strings.lenientFormat("%s_%s", hostAddress, port);
            RegistryCenter registryCenter = applicationContext.getBean(RegistryCenter.class);
            registryCenter.start();
            skeletion.keySet().forEach(this::registerService);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void stop() {
        skeletion.keySet().forEach(this::unregisterService);
        RegistryCenter registryCenter = applicationContext.getBean(RegistryCenter.class);
        registryCenter.stop();
    }


    private void registerService(String serviceName) {
        RegistryCenter registryCenter = applicationContext.getBean(RegistryCenter.class);
        registryCenter.register(serviceName, instance);
    }

    private void unregisterService(String serviceName) {
        RegistryCenter registryCenter = applicationContext.getBean(RegistryCenter.class);
        registryCenter.unregister(serviceName, instance);
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
                System.out.println(providerMeta);
                skeletion.add(className, providerMeta);
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
