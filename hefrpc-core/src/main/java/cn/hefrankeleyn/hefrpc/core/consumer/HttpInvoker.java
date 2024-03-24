package cn.hefrankeleyn.hefrpc.core.consumer;

import cn.hefrankeleyn.hefrpc.core.api.RpcRequest;
import cn.hefrankeleyn.hefrpc.core.api.RpcResponse;

/**
 * @Date 2024/3/24
 * @Author lifei
 */
public interface HttpInvoker {

    RpcResponse<?> post(RpcRequest request, String url);
}
