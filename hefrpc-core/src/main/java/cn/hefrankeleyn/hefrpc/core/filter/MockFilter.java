package cn.hefrankeleyn.hefrpc.core.filter;

import cn.hefrankeleyn.hefrpc.core.api.Filter;
import cn.hefrankeleyn.hefrpc.core.api.RpcRequest;
import cn.hefrankeleyn.hefrpc.core.api.RpcResponse;
import cn.hefrankeleyn.hefrpc.core.utils.MockUtils;

/**
 * @Date 2024/3/27
 * @Author lifei
 */
public class MockFilter implements Filter {
    @Override
    public Object preFilter(RpcRequest rpcRequest) {
        return MockUtils.mock(rpcRequest);
    }

    @Override
    public Object postFilter(RpcRequest request, RpcResponse rpcResponse, Object result) {
        return null;
    }
}
