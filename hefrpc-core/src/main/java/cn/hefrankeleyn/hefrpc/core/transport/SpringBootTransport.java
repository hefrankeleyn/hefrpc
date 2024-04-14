package cn.hefrankeleyn.hefrpc.core.transport;

import cn.hefrankeleyn.hefrpc.core.api.RpcRequest;
import cn.hefrankeleyn.hefrpc.core.api.RpcResponse;
import cn.hefrankeleyn.hefrpc.core.provider.ProviderInvoker;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Date 2024/4/14
 * @Author lifei
 */
@RestController
public class SpringBootTransport {

    @Resource
    private ProviderInvoker providerInvoker;

    // 使用HTTP + JSON实现序列化
    @RequestMapping(value = "/hefrpc")
    public RpcResponse<Object> invoke(@RequestBody RpcRequest request) {
        return providerInvoker.invoke(request);
    }


}
