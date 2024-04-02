package cn.hefrankeleyn.hefrpc.core.api;

/**
 * @Date 2024/3/7
 * @Author lifei
 */
public class RpcResponse<T> {

    /** 结果的状态 */
    private boolean status;

    /** 结果 */
    private T data;

    private HefRpcException ex;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public HefRpcException getEx() {
        return ex;
    }

    public void setEx(HefRpcException ex) {
        this.ex = ex;
    }

    @Override
    public String toString() {
        return "RpcResponse{" +
                "status=" + status +
                ", data=" + data +
                ", ex=" + ex +
                '}';
    }
}
