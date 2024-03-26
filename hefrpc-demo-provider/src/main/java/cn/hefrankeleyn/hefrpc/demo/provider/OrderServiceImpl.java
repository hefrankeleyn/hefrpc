package cn.hefrankeleyn.hefrpc.demo.provider;

import cn.hefrankeleyn.hefrpc.core.annotation.HefProvider;
import cn.hefrankeleyn.hefrpc.demo.api.Order;
import cn.hefrankeleyn.hefrpc.demo.api.OrderService;
import com.google.common.collect.Lists;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Date 2024/3/8
 * @Author lifei
 */
@Service
@HefProvider
public class OrderServiceImpl implements OrderService {

    public OrderServiceImpl() {}
    @Override
    public Order findById(Integer oid) {
        return new Order(oid, 26.2d);
    }

    @Override
    public long findById(long oid) {
        return oid + 1;
    }

    @Override
    public long findById(Order order) {
        return order.getOid()  + 123L;
    }

    @Override
    public long findById(float oid) {
        return 2L;
    }

    @Override
    public long[] findIds() {
        return new long[]{1l, 2l, 3l};
    }

    @Override
    public int[] findIntIds() {
        return new int[]{4,5,6};
    }

    @Override
    public int[] findIntIds(int[] ids) {
        return ids;
    }

    @Override
    public List<Integer> findListIds() {
        return Arrays.asList(1, 2,3, 4, 5, 6, 7);
    }

    @Override
    public List<Integer> findListIds(List<Order> orderList) {
        if (Objects.isNull(orderList) || orderList.size()==0) {
            return new ArrayList<>();
        }
        return orderList.stream().map(Order::getOid).toList();
    }

    @Override
    public Map<String, Order> findMap(Map<String, Order> map) {
        return map;
    }

}
