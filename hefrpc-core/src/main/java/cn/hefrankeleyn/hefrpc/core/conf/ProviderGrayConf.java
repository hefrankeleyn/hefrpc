package cn.hefrankeleyn.hefrpc.core.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Date 2024/4/9
 * @Author lifei
 */
@Configuration
@ConfigurationProperties(prefix = "app")
public class ProviderGrayConf {
    private Map<String, String> metas = new HashMap<>();

    public Map<String, String> getMetas() {
        return metas;
    }
}
