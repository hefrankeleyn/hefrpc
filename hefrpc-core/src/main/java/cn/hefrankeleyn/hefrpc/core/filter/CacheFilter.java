package cn.hefrankeleyn.hefrpc.core.filter;

import cn.hefrankeleyn.hefrpc.core.api.Filter;
import cn.hefrankeleyn.hefrpc.core.api.RpcRequest;
import cn.hefrankeleyn.hefrpc.core.api.RpcResponse;
import com.google.gson.Gson;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Date 2024/3/27
 * @Author lifei
 */
public class CacheFilter implements Filter {

    private final Map<String, Object> cache = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();
    @Override
    public Object preFilter(RpcRequest rpcRequest) {
        if (Objects.isNull(rpcRequest)) {
            return null;
        }
        return cache.get(gson.toJson(rpcRequest));
    }

    @Override
    public Object postFilter(RpcRequest request, RpcResponse rpcResponse, Object result) {
        if (Objects.isNull(request) || Objects.isNull(result)) {
            return null;
        }
        String key = gson.toJson(request);
        cache.putIfAbsent(key, result);
        return cache.get(key);
    }
}
