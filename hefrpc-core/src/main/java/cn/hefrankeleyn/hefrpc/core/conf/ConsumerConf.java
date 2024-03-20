package cn.hefrankeleyn.hefrpc.core.conf;

import cn.hefrankeleyn.hefrpc.core.api.LoadBalance;
import cn.hefrankeleyn.hefrpc.core.api.Router;
import cn.hefrankeleyn.hefrpc.core.cluster.RandomLoadBalance;
import cn.hefrankeleyn.hefrpc.core.cluster.RoundRibonLoadBalance;
import cn.hefrankeleyn.hefrpc.core.consumer.ConsumerBootstrap;
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
    
}
