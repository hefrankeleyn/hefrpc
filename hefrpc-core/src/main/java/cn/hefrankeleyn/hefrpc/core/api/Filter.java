package cn.hefrankeleyn.hefrpc.core.api;

import java.util.Objects;

/**
 * 过滤器
 * @Date 2024/3/20
 * @Author lifei
 */
public interface Filter {

    Object preFilter(RpcRequest rpcRequest);

    Object postFilter(RpcRequest request, RpcResponse rpcResponse, Object result);

    Filter DEFAULT = new Filter() {
        @Override
        public Object preFilter(RpcRequest rpcRequest) {
            return null;
        }

        @Override
        public Object postFilter(RpcRequest request, RpcResponse rpcResponse, Object result) {
            return result;
        }
    };
}
