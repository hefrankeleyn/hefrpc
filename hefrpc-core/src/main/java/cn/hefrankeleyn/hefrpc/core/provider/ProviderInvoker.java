package cn.hefrankeleyn.hefrpc.core.provider;

import cn.hefrankeleyn.hefrpc.core.api.RpcRequest;
import cn.hefrankeleyn.hefrpc.core.api.RpcResponse;
import cn.hefrankeleyn.hefrpc.core.meta.ProviderMeta;
import cn.hefrankeleyn.hefrpc.core.utils.TypeUtils;
import com.google.common.base.Strings;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkState;

/**
 * @Date 2024/3/24
 * @Author lifei
 */
public class ProviderInvoker {

    private MultiValueMap<String, ProviderMeta> skeletion;

    public ProviderInvoker(ProviderBootstrap providerBootstrap){
        this.skeletion = providerBootstrap.getSkeletion();
    }

    public RpcResponse<Object> invoke(RpcRequest request) {
        RpcResponse<Object> response = new RpcResponse<>();
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
        return providerMetas.stream().filter(providerMeta -> providerMeta.getMethodSign().equals(request.getMethodSign()))
                .findFirst()
                .orElse(null);
    }
}
