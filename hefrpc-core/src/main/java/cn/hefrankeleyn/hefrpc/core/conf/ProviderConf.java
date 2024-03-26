package cn.hefrankeleyn.hefrpc.core.conf;

import cn.hefrankeleyn.hefrpc.core.api.RegistryCenter;
import cn.hefrankeleyn.hefrpc.core.provider.ProviderBootstrap;
import cn.hefrankeleyn.hefrpc.core.provider.ProviderInvoker;
import cn.hefrankeleyn.hefrpc.core.registry.zk.ZkRegistryCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @Date 2024/3/7
 * @Author lifei
 */
@Configuration
public class ProviderConf {

    private static final Logger log = LoggerFactory.getLogger(ProviderConf.class);

    @Bean
    public ProviderBootstrap providerBootstrap() {
        return new ProviderBootstrap();
    }

    @Bean
    public ProviderInvoker providerInvoker(ProviderBootstrap providerBootstrap) {
        return new ProviderInvoker(providerBootstrap);
    }


    // 这里start和stop是有bug的
    @Bean//(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter registryCenter() {
        return new ZkRegistryCenter();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner providerStart(ProviderBootstrap providerBootstrap) {
        return x->{
            log.info("providerBootstrap start....");
            providerBootstrap.start();
            log.info("providerBootstap started");
        };
    }

}
