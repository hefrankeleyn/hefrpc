package com.hhrpc.hefrpc.demo.consumer;

import cn.hefrankeleyn.hefrpc.core.annotation.HefConsumer;
import cn.hefrankeleyn.hefrpc.core.conf.ConsumerConf;
import cn.hefrankeleyn.hefrpc.demo.api.Order;
import cn.hefrankeleyn.hefrpc.demo.api.OrderService;
import cn.hefrankeleyn.hefrpc.demo.api.User;
import cn.hefrankeleyn.hefrpc.demo.api.UserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@Import(ConsumerConf.class)
@RestController
public class HefrpcDemoConsumerApplication {

    @HefConsumer
    private UserService userService;

    @HefConsumer
    private OrderService orderService;

    @RequestMapping(value = "/")
    public User findById(int id) {
        return this.userService.findById(id);
    }

    public static void main(String[] args) {
        SpringApplication.run(HefrpcDemoConsumerApplication.class, args);
    }

    @Bean
    public ApplicationRunner consumerRun() {
        return x -> {
//            testAll();
        };
    }

    private void testAll() {
        User user = this.userService.findById(100);
        System.out.println(user);
        Integer idNum01 = this.userService.findIdNum(190);
        System.out.println(idNum01);
        String usename = this.userService.findName("aaaa");
        System.out.println(usename);
        System.out.println(this.userService.toString());
        Integer idNum = this.userService.findIdNum(3.15d);
        System.out.println(idNum);
        User user01 = this.userService.findById(201, "xiaofang");
        System.out.println(user01);
        Long idNumLong = this.userService.findIdNum(123L);
        System.out.println("long: " + idNumLong);
        long reslong = this.orderService.findById(15l);
        System.out.println(reslong);
        System.out.println(this.orderService.findById(new Order(12, 97.234)));
        System.out.println(this.orderService.findById(1f));
        System.out.println(Arrays.toString(this.orderService.findIds()));
        System.out.println(Arrays.toString(this.orderService.findIntIds()));
        System.out.println(Arrays.toString(this.orderService.findIntIds(new int[]{9, 8, 7, 6, 5})));
        List<Integer> listIds = this.orderService.findListIds();
        System.out.println(listIds);

        List<Integer> listOids = this.orderService.findListIds(Arrays.asList(new Order(1, 1.1d), new Order(2, 2.2d)));
        System.out.println(listOids);
        Map<String, Order> map = new HashMap<>();
        map.put("aaa", new Order(111, 11.1d));
        map.put("bbb", new Order(222, 22.2d));
        Map<String, Order> resMap = this.orderService.findMap(map);
        System.out.println(resMap);
        User user03 = this.userService.findById(12);
        System.out.println(user03);
    }

}
