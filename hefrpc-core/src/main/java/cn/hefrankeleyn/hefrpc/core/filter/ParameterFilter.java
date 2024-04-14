package cn.hefrankeleyn.hefrpc.core.filter;

import cn.hefrankeleyn.hefrpc.core.api.Filter;
import cn.hefrankeleyn.hefrpc.core.api.HefrpcContent;
import cn.hefrankeleyn.hefrpc.core.api.RpcRequest;
import cn.hefrankeleyn.hefrpc.core.api.RpcResponse;

import java.util.Map;

/**
 * @Date 2024/4/14
 * @Author lifei
 */
public class ParameterFilter implements Filter {
    @Override
    public Object preFilter(RpcRequest rpcRequest) {
        Map<String, String> contextParameters = HefrpcContent.contextParameters.get();
        if (!contextParameters.isEmpty()) {
            rpcRequest.getParams().putAll(contextParameters);
        }
        return null;
    }

    @Override
    public Object postFilter(RpcRequest request, RpcResponse rpcResponse, Object result) {
        return null;
    }
}
