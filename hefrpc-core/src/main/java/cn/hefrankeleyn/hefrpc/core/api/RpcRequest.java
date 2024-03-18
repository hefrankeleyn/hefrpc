package cn.hefrankeleyn.hefrpc.core.api;

import java.util.Arrays;

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

    @Override
    public String toString() {
        return "RpcRequest{" +
                "service='" + service + '\'' +
                ", method='" + methodSign + '\'' +
                ", args=" + Arrays.toString(args) +
                '}';
    }
}
