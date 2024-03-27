package cn.hefrankeleyn.hefrpc.core.conf;

import cn.hefrankeleyn.hefrpc.core.api.Filter;
import cn.hefrankeleyn.hefrpc.core.api.LoadBalance;
import cn.hefrankeleyn.hefrpc.core.api.RegistryCenter;
import cn.hefrankeleyn.hefrpc.core.api.Router;
import cn.hefrankeleyn.hefrpc.core.cluster.RoundRibonLoadBalance;
import cn.hefrankeleyn.hefrpc.core.consumer.ConsumerBootstrap;
import cn.hefrankeleyn.hefrpc.core.consumer.HttpInvoker;
import cn.hefrankeleyn.hefrpc.core.consumer.http.OkHttpInvoker;
import cn.hefrankeleyn.hefrpc.core.filter.CacheFilter;
import cn.hefrankeleyn.hefrpc.core.filter.MockFilter;
import cn.hefrankeleyn.hefrpc.core.meta.InstanceMeta;
import cn.hefrankeleyn.hefrpc.core.registry.zk.ZkRegistryCenter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

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
    public LoadBalance<InstanceMeta> loadBalance(){
        return new RoundRibonLoadBalance<>();
    }

    @Bean
    public Router<InstanceMeta> router() {
        return Router.DEFAULT;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter registryCenter() {
//        List<String> providers = Arrays.asList(servers.split(","));
//        return new RegistryCenter.StaticRegistryCenter(providers);
        return new ZkRegistryCenter();
    }

    @Bean
    public HttpInvoker httpInvoker() {
        return new OkHttpInvoker();
    }

    @Bean
    public Filter filter01() {
        return Filter.DEFAULT;
    }

    @Bean
    public Filter filter02() {
//        return new CacheFilter();
        return new MockFilter();
    }

    
}
