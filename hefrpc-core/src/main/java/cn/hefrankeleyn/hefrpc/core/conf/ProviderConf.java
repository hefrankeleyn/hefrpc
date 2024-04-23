package cn.hefrankeleyn.hefrpc.core.conf;

import cn.hefrankeleyn.hefrpc.core.api.RegistryCenter;
import cn.hefrankeleyn.hefrpc.core.provider.ProviderBootstrap;
import cn.hefrankeleyn.hefrpc.core.provider.ProviderInvoker;
import cn.hefrankeleyn.hefrpc.core.registry.zk.ZkRegistryCenter;
import cn.hefrankeleyn.hefrpc.core.transport.SpringBootTransport;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.annotation.Order;

/**
 * @Date 2024/3/7
 * @Author lifei
 */
@Configuration
@Import({ProviderGrayConf.class, SpringBootTransport.class, AppConfigProperties.class})
public class ProviderConf {

    @Value("${server.port:8080}")
    private Integer port;

    @Value("${hefrpc.zk.servers:localhost:2181}")
    private String zkServers;

    @Value("${hefrpc.zk.root:hefrpc}")
    private String zkRoot;

    @Resource
    private AppConfigProperties appConfigProperties;

    @Resource
    private ProviderGrayConf providerGrayConf;

    private static final Logger log = LoggerFactory.getLogger(ProviderConf.class);

    @Bean
    public ProviderBootstrap providerBootstrap() {
        return new ProviderBootstrap(port, appConfigProperties, providerGrayConf);
    }

    @Bean
    public ProviderInvoker providerInvoker(ProviderBootstrap providerBootstrap) {
        return new ProviderInvoker(providerBootstrap);
    }


    // 这里start和stop是有bug的
    @Bean//(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnMissingBean
    public RegistryCenter registryCenter() {
        return new ZkRegistryCenter(zkServers, zkRoot);
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
