package com.hhrpc.hefrpc.demo.consumer;

import cn.hefrankeleyn.hefrpc.core.annotation.HefConsumer;
import cn.hefrankeleyn.hefrpc.core.conf.ConsumerConf;
import cn.hefrankeleyn.hefrpc.demo.api.User;
import cn.hefrankeleyn.hefrpc.demo.api.UserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ConsumerConf.class)
public class HefrpcDemoConsumerApplication {

    @HefConsumer
    private UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(HefrpcDemoConsumerApplication.class, args);
    }

    @Bean
    public ApplicationRunner consumerRun() {
        return x -> {
            User user = this.userService.findById(100);
            System.out.println(user);
            Integer idNum = this.userService.findIdNum(190);
            System.out.println(idNum);
            String usename = this.userService.findName("aaaa");
            System.out.println(usename);
            System.out.println(this.userService.toString());
        };
    }

}
