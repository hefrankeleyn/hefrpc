package cn.hefrankeleyn.hefrpc.core;

import cn.hefrankeleyn.hefrpc.core.utils.HefRpcMethodUtils;
import org.junit.Test;

import java.lang.reflect.Method;

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
