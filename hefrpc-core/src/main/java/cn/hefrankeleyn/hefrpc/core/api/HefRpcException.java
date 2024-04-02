package cn.hefrankeleyn.hefrpc.core.api;

import com.google.common.base.Strings;

/**
 * @Date 2024/4/3
 * @Author lifei
 */
public class HefRpcException extends RuntimeException{

    private String errCode;

    public HefRpcException(){}

    public HefRpcException(String message) {
        super(message);
    }
    public HefRpcException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public HefRpcException(Throwable throwable) {
        super(throwable);
    }
    public HefRpcException(Throwable throwable, String errCode) {
        super(throwable);
        this.errCode = errCode;
    }

    // X==> 技术类异常
    // Y==> 业务类异常
    // Z==> 搞不清楚
    public static final String SOCKET_TIMEOUT_EX = Strings.lenientFormat("X%s-%%s", "001","http_invoke_timeout");
    public static final String NO_SUCH_METHOD_EX = Strings.lenientFormat("X%s-%%s", "002","method_not_exists");
    public static final String UNKNOWN_EX = Strings.lenientFormat("Z%s-%%s", "001","unknown");

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }
}
