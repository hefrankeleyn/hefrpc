package cn.hefrankeleyn.hefrpc.core.meta;

import com.google.common.base.Strings;

import java.util.Map;

/**
 * @Date 2024/3/24
 * @Author lifei
 */
public class InstanceMeta {

    // http、https、ftp
    private String schema;
    private String host;
    private Integer port;
    // online or offline
    private Boolean status;

    private String context;

    private Map<String, String> parameters;

    public InstanceMeta(){}

    public InstanceMeta(String schema, String host, Integer port) {
        this.schema = schema;
        this.host = host;
        this.port = port;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String toPath() {
        return Strings.lenientFormat("%s_%s", host, port);
    }

    public String toUrl() {
        return Strings.lenientFormat("%s://%s:%s/", schema, host, port);
    }

    @Override
    public String toString() {
        return "InstanceMeta{" +
                "schema='" + schema + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", status=" + status +
                ", context='" + context + '\'' +
                ", parameters=" + parameters +
                '}';
    }


}
