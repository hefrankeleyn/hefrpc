package com.hhrpc.hefrpc.demo.consumer;

import cn.hefrankeleyn.hefrpc.core.annotation.HefConsumer;
import cn.hefrankeleyn.hefrpc.core.api.HefRpcException;
import cn.hefrankeleyn.hefrpc.core.api.HefrpcContent;
import cn.hefrankeleyn.hefrpc.core.api.Router;
import cn.hefrankeleyn.hefrpc.core.cluster.GrayRouter;
import cn.hefrankeleyn.hefrpc.core.conf.ConsumerConf;
import cn.hefrankeleyn.hefrpc.core.meta.InstanceMeta;
import cn.hefrankeleyn.hefrpc.demo.api.Order;
import cn.hefrankeleyn.hefrpc.demo.api.OrderService;
import cn.hefrankeleyn.hefrpc.demo.api.User;
import cn.hefrankeleyn.hefrpc.demo.api.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public User findById(@RequestParam("id") int id) {
        return this.userService.findById(id);
    }

    @RequestMapping(value = "/findEx")
    public User findEx(@RequestParam("flag") boolean flag) {
        return userService.ex(flag);
    }

    @RequestMapping(value = "/findTimeOut")
    public User findTimeOut(@RequestParam("timeout") int timeout) {
        return userService.findTimeOut(timeout);
    }

    @Resource
    private Router<InstanceMeta> router;

    @RequestMapping(value = "/updateGrayRatio")
    public Integer updateGrayRatio(@RequestParam("grayRatio") Integer grayRatio) {
        ((GrayRouter)router).setGrayRatio(grayRatio);
        return grayRatio;
    }

    public static void main(String[] args) {
        SpringApplication.run(HefrpcDemoConsumerApplication.class, args);
    }

    @Bean
    public ApplicationRunner consumerRun() {
        return x -> {
            testAll();
        };
    }

    private void testAll() {
        System.out.println("===> 用例01: 返回User对象： ");
        User user = this.userService.findById(100);
        System.out.println(user);
        System.out.println("====> 用例02: 返回Integer类型： ");
        Integer idNum01 = this.userService.findIdNum(190);
        System.out.println(idNum01);
        System.out.println("====> 用例03: 返回String类型： ");
        String usename = this.userService.findName("aaaa");
        System.out.println(usename);
        System.out.println("====> 用例04: 调用Object方法： ");
        System.out.println(this.userService.toString());
        System.out.println("===> 用例05: 参数为double， 返回Integer： ");
        Integer idNum = this.userService.findIdNum(3.15d);
        System.out.println(idNum);
        System.out.println("====> 用例06: 参数为两个，返回User： ");
        User user01 = this.userService.findById(201, "xiaofang");
        System.out.println(user01);
        System.out.println("====> 用例07: 参数为Long， 返回值为Long类型： ");
        Long idNumLong = this.userService.findIdNum(123L);
        System.out.println("long: " + idNumLong);
        System.out.println("====> 用例08: 参数为Long，返回Long");
        long reslong = this.orderService.findById(15l);
        System.out.println(reslong);
        System.out.println("===> 用例09: 参数为Order， 返回Long： ");
        System.out.println(this.orderService.findById(new Order(12, 97.234)));
        System.out.println("====> 用例10: 参数为float， 返回值为long： ");
        System.out.println(this.orderService.findById(1f));
        System.out.println("===> 用例11: 返回值为Array： ");
        System.out.println(Arrays.toString(this.orderService.findIds()));
        System.out.println("=====> 用例12: 返回值为int类型的数组： ");
        System.out.println(Arrays.toString(this.orderService.findIntIds()));
        System.out.println("===> 用例13: 参数和返回值都是int类型的数组");
        System.out.println(Arrays.toString(this.orderService.findIntIds(new int[]{9, 8, 7, 6, 5})));
        List<Integer> listIds = this.orderService.findListIds();
        System.out.println(listIds);
        System.out.println("===> 用例14: 返回值为List类型： ");
        List<Integer> listOids = this.orderService.findListIds(Arrays.asList(new Order(1, 1.1d), new Order(2, 2.2d)));
        System.out.println(listOids);
        System.out.println("===> 用例15: 返回值是Map： ");
        Map<String, Order> map = new HashMap<>();
        map.put("aaa", new Order(111, 11.1d));
        map.put("bbb", new Order(222, 22.2d));
        Map<String, Order> resMap = this.orderService.findMap(map);
        System.out.println(resMap);
        User user03 = this.userService.findById(12);
        System.out.println(user03);
        System.out.println("====> 一个返回异常的case： ");
        try {
            User exUser = userService.ex(true);
            System.out.println(exUser);
        }catch (Exception e) {
            if (e instanceof HefRpcException rpcex) {
                System.out.println("=====> This is a rpc exception: " + rpcex.getMessage());
            } else {
                System.out.println("====> This is other exception...");
            }
        }

        System.out.println("===> 用例16: 测试通过RpcContext 跨越consumer和provider传递参数：");
        Map<String, String> contextParameters01 = HefrpcContent.contextParameters.get();
        contextParameters01.put("tokenId", "001");
        contextParameters01.put("username", "aaa");
        String tokenId = userService.cacheParameter("tokenId");
        System.out.println(tokenId);
        HefrpcContent.contextParameters.get().clear();
    }

}
