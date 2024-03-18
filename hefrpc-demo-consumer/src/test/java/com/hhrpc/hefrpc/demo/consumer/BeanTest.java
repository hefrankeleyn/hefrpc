package com.hhrpc.hefrpc.demo.consumer;

import cn.hefrankeleyn.hefrpc.demo.api.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
}
