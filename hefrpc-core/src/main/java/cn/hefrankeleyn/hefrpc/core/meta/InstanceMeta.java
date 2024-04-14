package cn.hefrankeleyn.hefrpc.core.meta;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

import java.util.Map;
import java.util.Objects;

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

    private Map<String, String> parameters = Maps.newHashMap();

    public InstanceMeta(){}

    public InstanceMeta(String schema, String host, Integer port, String context) {
        this.schema = schema;
        this.host = host;
        this.port = port;
        this.context = context;
    }

    public static InstanceMeta http(String schema, String host, Integer port, String context) {
        return new InstanceMeta(schema, host, port, context);
    }

    public InstanceMeta addParams(Map<String, String> params) {
        this.getParameters().putAll(params);
        return this;
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
        return Strings.lenientFormat("%s_%s_%s", host, port, context);
    }

    public String toUrl() {
        return Strings.lenientFormat("%s://%s:%s/%s", schema, host, port, context);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InstanceMeta that)) return false;
        return Objects.equals(getSchema(), that.getSchema()) && Objects.equals(getHost(), that.getHost()) && Objects.equals(getPort(), that.getPort()) && Objects.equals(getStatus(), that.getStatus()) && Objects.equals(getContext(), that.getContext()) && Objects.equals(getParameters(), that.getParameters());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSchema(), getHost(), getPort(), getStatus(), getContext(), getParameters());
    }

    public String toMetas() {
        return new Gson().toJson(parameters);
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
