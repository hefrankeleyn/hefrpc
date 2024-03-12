package cn.hefrankeleyn.hefrpc.core.conf;

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
}
