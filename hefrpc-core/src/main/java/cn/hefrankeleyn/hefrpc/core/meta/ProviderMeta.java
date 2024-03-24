package cn.hefrankeleyn.hefrpc.core.meta;

import java.lang.reflect.Method;

/**
 * @Date 2024/3/14
 * @Author lifei
 */
public class ProviderMeta {
    private Method method;
    private String methodSign;
    private Object service;


    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object getService() {
        return service;
    }

    public void setService(Object service) {
        this.service = service;
    }

    public String getMethodSign() {
        return methodSign;
    }

    public void setMethodSign(String methodSign) {
        this.methodSign = methodSign;
    }

    @Override
    public String toString() {
        return "ProviderMeta{" +
                "method='" + method + '\'' +
                ",service=" + service +
                ",methodSign='" + methodSign + '\'' +
                '}';
    }
}
