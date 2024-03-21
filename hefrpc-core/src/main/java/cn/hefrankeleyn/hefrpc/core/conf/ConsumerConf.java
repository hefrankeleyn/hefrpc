package cn.hefrankeleyn.hefrpc.core.conf;

import cn.hefrankeleyn.hefrpc.core.api.LoadBalance;
import cn.hefrankeleyn.hefrpc.core.api.RegistryCenter;
import cn.hefrankeleyn.hefrpc.core.api.Router;
import cn.hefrankeleyn.hefrpc.core.cluster.RandomLoadBalance;
import cn.hefrankeleyn.hefrpc.core.cluster.RoundRibonLoadBalance;
import cn.hefrankeleyn.hefrpc.core.consumer.ConsumerBootstrap;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;

/**
 * @Date 2024/3/11
 * @Author lifei
 */
@Configuration
public class ConsumerConf {

    @Value("${hefrpc.provicer}")
    private String servers;

    @Bean
    public ConsumerBootstrap consumerBootstrap() {
        return new ConsumerBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner runner(ConsumerBootstrap consumerBootstrap) {
        return x -> consumerBootstrap.scanningFields();
    }

    @Bean
    public LoadBalance loadBalance(){
        return new RoundRibonLoadBalance();
    }

    @Bean
    public Router router() {
        return Router.DEFAULT;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter registryCenter() {
        List<String> providers = Arrays.asList(servers.split(","));
        return new RegistryCenter.StaticRegistryCenter(providers);
    }
    
}
