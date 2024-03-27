package cn.hefrankeleyn.hefrpc.core;

import cn.hefrankeleyn.hefrpc.core.utils.HefRpcMethodUtils;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Date 2024/3/13
 * @Author lifei
 */
public class ParamTypeTest {

    @Test
    public void test01() {
//        Class<Integer> integerClass = int.class;
        Class<Integer> integerClass = Integer.class;
        Integer.valueOf(1);
        System.out.println(integerClass.getName());
    }

    @Test
    public void mapPutTest() {
        Map<String, String> oneMap = new ConcurrentHashMap<>();
//        oneMap.putIfAbsent("aa", null);
        oneMap.putIfAbsent("aa", "001");
        System.out.println(oneMap.get("aa"));
        oneMap.putIfAbsent("aa", "123");
        System.out.println(oneMap.get("aa"));
    }

    @Test
    public void createMethodSignTest() {
        Method[] methods = ParamTypeTest.class.getMethods();
        for (Method method : methods) {
            String methodSign = HefRpcMethodUtils.createMethodSign(method);
            System.out.println(methodSign);
        }
    }

    public void printMethodSignList(Class<?> clazz, String param2) {
        System.out.println("OK!");
    }

    @Test
    public void objectMethodTest() {
        boolean res = HefRpcMethodUtils.checkLocalMethod("toString");
        System.out.println(res);
    }
}
