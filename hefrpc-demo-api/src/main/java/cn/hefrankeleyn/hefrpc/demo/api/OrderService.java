package cn.hefrankeleyn.hefrpc.demo.api;

import java.util.List;
import java.util.Map;

public interface OrderService {

    Order findById(Integer oid);

    long findById(long oid);

    long findById(Order order);
    long findById(float oid);

    long[] findIds();

    int[] findIntIds();
    int[] findIntIds(int[] ids);

    List<Integer> findListIds();

    List<Integer> findListIds(List<Order> orderList);

    Map<String, Order> findMap(Map<String, Order> map);
}
