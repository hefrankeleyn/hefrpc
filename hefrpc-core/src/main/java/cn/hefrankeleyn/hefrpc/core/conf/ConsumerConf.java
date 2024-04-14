package cn.hefrankeleyn.hefrpc.core.conf;

import cn.hefrankeleyn.hefrpc.core.api.*;
import cn.hefrankeleyn.hefrpc.core.cluster.GrayRouter;
import cn.hefrankeleyn.hefrpc.core.cluster.RoundRibonLoadBalance;
import cn.hefrankeleyn.hefrpc.core.consumer.ConsumerBootstrap;
import cn.hefrankeleyn.hefrpc.core.consumer.HttpInvoker;
import cn.hefrankeleyn.hefrpc.core.consumer.http.OkHttpInvoker;
import cn.hefrankeleyn.hefrpc.core.filter.CacheFilter;
import cn.hefrankeleyn.hefrpc.core.filter.MockFilter;
import cn.hefrankeleyn.hefrpc.core.filter.ParameterFilter;
import cn.hefrankeleyn.hefrpc.core.meta.InstanceMeta;
import cn.hefrankeleyn.hefrpc.core.registry.zk.ZkRegistryCenter;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * @Date 2024/3/11
 * @Author lifei
 */
@Configuration
@Import({ConsumerBusConf.class, AppConfigProperties.class})
public class ConsumerConf {

    @Value("${hefrpc.provicer}")
    private String servers;

    @Value("${hefrpc.zk.servers:localhost:2181}")
    private String zkServers;

    @Value("${hefrpc.zk.root:hefrpc}")
    private String zkRoot;

    @Resource
    private ConsumerBusConf consumerBusConf;

    @Resource
    private AppConfigProperties appConfigProperties;

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
        return new GrayRouter(consumerBusConf.getGrayRatio());
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter registryCenter() {
//        List<String> providers = Arrays.asList(servers.split(","));
//        return new RegistryCenter.StaticRegistryCenter(providers);
        return new ZkRegistryCenter(zkServers, zkRoot);
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
    public Filter parameterFilter() {
        return new ParameterFilter();
    }

//    @Bean
//    public Filter filter02() {
//        return new CacheFilter();
////        return new MockFilter();
//    }

    @Bean
    public HefrpcContent hefrpcContent(List<Filter> filterList,
                                       Router<InstanceMeta> router,
                                       LoadBalance<InstanceMeta> loadBalance) {
        HefrpcContent hefrpcContent = new HefrpcContent();
        hefrpcContent.setFilterList(filterList);
        hefrpcContent.setRouter(router);
        hefrpcContent.setLoadBalance(loadBalance);
        hefrpcContent.getParameters().put("app.id", String.valueOf(appConfigProperties.getId()));
        hefrpcContent.getParameters().put("app.namespace", String.valueOf(appConfigProperties.getNamespace()));
        hefrpcContent.getParameters().put("app.env", String.valueOf(appConfigProperties.getEnv()));
        hefrpcContent.getParameters().put("consumer.retries", String.valueOf(consumerBusConf.getRetries()));
        hefrpcContent.getParameters().put("consumer.retries", String.valueOf(consumerBusConf.getRetries()));
        hefrpcContent.getParameters().put("consumer.retries", String.valueOf(consumerBusConf.getRetries()));
        hefrpcContent.getParameters().put("consumer.timeout", String.valueOf(consumerBusConf.getTimeout()));
        hefrpcContent.getParameters().put("consumer.halfOpenInitDelay", String.valueOf(consumerBusConf.getHalfOpenInitDelay()));
        hefrpcContent.getParameters().put("consumer.halfOpenDelay", String.valueOf(consumerBusConf.getHalfOpenDelay()));
        hefrpcContent.getParameters().put("consumer.faultLimit", String.valueOf(consumerBusConf.getFaultLimit()));
        return hefrpcContent;
    }

    
}
