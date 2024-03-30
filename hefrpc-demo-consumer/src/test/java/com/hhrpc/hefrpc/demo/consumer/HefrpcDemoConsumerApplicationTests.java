package com.hhrpc.hefrpc.demo.consumer;

import cn.hefrankeleyn.hefrpc.demo.api.User;
import cn.hefrankeleyn.hefrpc.demo.provider.HefrpcDemoProviderApplication;
import jakarta.annotation.Resource;
import org.apache.curator.test.TestingServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class HefrpcDemoConsumerApplicationTests {

    private static TestingServer testingServer;

    @Resource
    private HefrpcDemoConsumerApplication hefrpcDemoConsumerApplication;

    private static ApplicationContext providerContext = null;
    @BeforeAll
    public static void init() {
        try {
            testingServer = new TestingServer(2182);
            providerContext = SpringApplication.run(HefrpcDemoProviderApplication.class, new String[]{
                    "--server.port=8084", "--hefrpc.zkservers=localhost:2182"
            });
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void contextLoads() {
        User user = hefrpcDemoConsumerApplication.findById(99);
        System.out.println(user);
    }

    @AfterAll
    public static void stop() {
        try {
            SpringApplication.exit(providerContext, ()->1);
            testingServer.stop();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
