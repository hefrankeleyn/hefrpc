package cn.hefrankeleyn.hefrpc.core.conf;

import com.google.common.base.MoreObjects;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Date 2024/4/14
 * @Author lifei
 */
@Configuration
@ConfigurationProperties(prefix = "hefrpc.consumer")
public class ConsumerBusConf {
    private Integer retries = 2;
    private Long timeout = 1000L;
    private Integer grayRatio = 10;
    private Long halfOpenInitDelay = 10_000L;
    private Long halfOpenDelay = 60_000L;
    private Integer faultLimit = 10;

    public Integer getRetries() {
        return retries;
    }

    public Long getTimeout() {
        return timeout;
    }

    public Integer getGrayRatio() {
        return grayRatio;
    }

    public Long getHalfOpenInitDelay() {
        return halfOpenInitDelay;
    }

    public Long getHalfOpenDelay() {
        return halfOpenDelay;
    }

    public Integer getFaultLimit() {
        return faultLimit;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(ConsumerBusConf.class)
                .add("retries", retries)
                .add("timeout", timeout)
                .add("grayRatio", grayRatio)
                .add("halfOpenInitDelay", halfOpenInitDelay)
                .add("halfOpenDelay", halfOpenDelay)
                .add("faultLimit", faultLimit)
                .toString();
    }
}
