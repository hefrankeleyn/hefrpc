package cn.hefrankeleyn.hefrpc.core.conf;

import com.google.common.base.MoreObjects;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Date 2024/4/14
 * @Author lifei
 */
@Configuration
@ConfigurationProperties(prefix = "hefrpc.app")
public class AppConfigProperties {
    private String id = "app1";
    private String namespace = "public";
    private String env = "dev";

    public String getId() {
        return id;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getEnv() {
        return env;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(AppConfigProperties.class)
                .add("id", id)
                .add("namespace", namespace)
                .add("env", env)
                .toString();
    }
}
