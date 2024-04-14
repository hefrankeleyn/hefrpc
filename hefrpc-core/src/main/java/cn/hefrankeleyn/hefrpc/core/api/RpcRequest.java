package cn.hefrankeleyn.hefrpc.core.api;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Map;

/**
 * 模拟请求的描述
 * @Date 2024/3/7
 * @Author lifei
 */
public class RpcRequest {

    /** 接口，例如：cn.hefrankeleyn.hefrpc.demo.api.UserService */
    private String service;

    /** 方法，例如：findById */
    private String methodSign;

    /** 参数： 例如： 100 */
    private Object[] args;

    private Map<String, String> params = Maps.newHashMap();

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getMethodSign() {
        return methodSign;
    }

    public void setMethodSign(String methodSign) {
        this.methodSign = methodSign;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(RpcRequest.class)
                .add("service", service)
                .add("methodSign", methodSign)
                .add("args", Arrays.toString(args))
                .add("params", params)
                .toString();
    }
}
