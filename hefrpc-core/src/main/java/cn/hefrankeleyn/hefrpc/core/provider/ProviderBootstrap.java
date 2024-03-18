package cn.hefrankeleyn.hefrpc.core.provider;

import cn.hefrankeleyn.hefrpc.core.annotation.HefProvider;
import cn.hefrankeleyn.hefrpc.core.api.RpcRequest;
import cn.hefrankeleyn.hefrpc.core.api.RpcResponse;
import cn.hefrankeleyn.hefrpc.core.utils.HefRpcMethodUtils;
import static com.google.common.base.Preconditions.*;

import cn.hefrankeleyn.hefrpc.core.utils.TypeUtils;
import com.google.common.base.Strings;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.BeansException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @Date 2024/3/7
 * @Author lifei
 */
public class ProviderBootstrap implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    // 1. 缓存，加快访问速度； 2.
    private MultiValueMap<String, ProviderMeta> skeletion = new LinkedMultiValueMap<>();

    public RpcResponse invoke(RpcRequest request) {
        RpcResponse response= new RpcResponse();
        try {
            ProviderMeta providerMeta = findProviderMeta(request);
            System.out.println(Strings.lenientFormat("开始执行：%s", providerMeta));
            checkState(Objects.nonNull(providerMeta), "没有查到对方的方法：%s", request);
            Method method = providerMeta.getMethod();
            Object[] args = TypeUtils.processArgs(request.getArgs(), method.getParameterTypes());
            Object data = method.invoke(providerMeta.getService(), args);
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
    public void buildProviders() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(HefProvider.class);
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
}
