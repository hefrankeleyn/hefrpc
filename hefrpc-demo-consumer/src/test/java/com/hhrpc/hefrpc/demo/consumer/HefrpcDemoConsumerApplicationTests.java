package com.hhrpc.hefrpc.demo.consumer;

import cn.hefrankeleyn.hefrpc.demo.api.User;
import cn.hefrankeleyn.hefrpc.demo.provider.HefrpcDemoProviderApplication;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootTest
class HefrpcDemoConsumerApplicationTests {

    @Resource
    private HefrpcDemoConsumerApplication hefrpcDemoConsumerApplication;

    private static ApplicationContext providerContext = null;
    @BeforeAll
    public static void init() {
        providerContext = SpringApplication.run(HefrpcDemoProviderApplication.class, new String[]{
                "--server.port=8084"
        });
    }

    @Test
    void contextLoads() {
        User user = hefrpcDemoConsumerApplication.findById(99);
        System.out.println(user);
    }

    @AfterAll
    public static void stop() {
        SpringApplication.exit(providerContext, ()->1);
    }

}
