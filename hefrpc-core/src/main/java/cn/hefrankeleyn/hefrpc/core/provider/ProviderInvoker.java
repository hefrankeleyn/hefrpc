package cn.hefrankeleyn.hefrpc.core.provider;

import cn.hefrankeleyn.hefrpc.core.api.HefRpcException;
import cn.hefrankeleyn.hefrpc.core.api.RpcRequest;
import cn.hefrankeleyn.hefrpc.core.api.RpcResponse;
import cn.hefrankeleyn.hefrpc.core.meta.ProviderMeta;
import cn.hefrankeleyn.hefrpc.core.utils.TypeUtils;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkState;

/**
 * @Date 2024/3/24
 * @Author lifei
 */
public class ProviderInvoker {
    private static final Logger log = LoggerFactory.getLogger(ProviderInvoker.class);

    private MultiValueMap<String, ProviderMeta> skeletion;

    public ProviderInvoker(ProviderBootstrap providerBootstrap){
        this.skeletion = providerBootstrap.getSkeletion();
    }

    public RpcResponse<Object> invoke(RpcRequest request) {
        RpcResponse<Object> response = new RpcResponse<>();
        response.setStatus(false);
        try {
            ProviderMeta providerMeta = findProviderMeta(request);
            log.debug(Strings.lenientFormat("开始执行：%s", providerMeta));
            checkState(Objects.nonNull(providerMeta), "没有查到对方的方法：%s", request);
            Method method = providerMeta.getMethod();
            Object[] args = TypeUtils.processArgs(request.getArgs(), method.getParameterTypes(), method.getGenericParameterTypes());
            Object data = method.invoke(providerMeta.getService(), args);
            response.setStatus(true);
            response.setData(data);
            return response;
        } catch (InvocationTargetException e) {
            // 被调用方法本身发生异常
            response.setEx(new HefRpcException(e.getTargetException()));
        } catch (IllegalAccessException e) {
            response.setEx(new HefRpcException(e));
        }
        return response;
    }

    private ProviderMeta findProviderMeta(RpcRequest request) {
        List<ProviderMeta> providerMetas = skeletion.get(request.getService());
        return providerMetas.stream().filter(providerMeta -> providerMeta.getMethodSign().equals(request.getMethodSign()))
                .findFirst()
                .orElse(null);
    }
}
