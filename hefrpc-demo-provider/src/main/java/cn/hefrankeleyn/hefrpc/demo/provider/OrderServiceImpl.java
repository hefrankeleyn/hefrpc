package cn.hefrankeleyn.hefrpc.demo.provider;

import cn.hefrankeleyn.hefrpc.core.annotation.HefProvider;
import cn.hefrankeleyn.hefrpc.demo.api.Order;
import cn.hefrankeleyn.hefrpc.demo.api.OrderService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

/**
 * @Date 2024/3/8
 * @Author lifei
 */
@Service
@HefProvider
public class OrderServiceImpl implements OrderService {

    public OrderServiceImpl() {
        try {
            System.out.println("初始化order ....");
            Thread.sleep(1L);
            System.out.println("初始化order ....完成");
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Order findById(Integer oid) {
        return new Order(oid, 26.2d);
    }

    @PostConstruct
    public void beanPostConstruct() {
        System.out.println("当前的Bean是：" + OrderServiceImpl.class.getName());
    }
}
