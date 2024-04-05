package com.hhrpc.hefrpc.demo.consumer;

import cn.hefrankeleyn.hefrpc.core.api.HefRpcException;
import cn.hefrankeleyn.hefrpc.core.utils.MockUtils;
import cn.hefrankeleyn.hefrpc.demo.api.User;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Date 2024/3/13
 * @Author lifei
 */
public class BeanTest {

    @Test
    public void mockTest() {
        User user = new User();
        System.out.println(user);
        Object mock = MockUtils.mock(user.getClass(), null);
        System.out.println(mock);
    }

    @Test
    public void userTest() throws NoSuchMethodException {
        Gson gson = new Gson();
        Method method = BeanTest.class.getMethod("getIntArray");
        Class<?> returnType = method.getReturnType();
        Object[] o01 = new Object[]{1,3,4};
        String ostr01 = gson.toJson(o01);
        Object a01 = gson.fromJson(ostr01, returnType);
        System.out.println(Arrays.asList(a01));
        List<Integer> l01 = new ArrayList<>();
        Object[] array = l01.toArray();
    }

    public int[] getIntArray() {
        return null;
    }

    @Test
    public void exTest() {
        HefRpcException oneError = new HefRpcException("one error");
        String oneSTr = JSONObject.toJSONString(oneError);
        System.out.println(oneSTr);
        Object result = JSONObject.parseObject(oneSTr);
        System.out.println(result);
        System.out.println(result instanceof HefRpcException);
    }
}
