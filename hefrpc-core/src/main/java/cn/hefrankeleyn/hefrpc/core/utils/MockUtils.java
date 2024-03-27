package cn.hefrankeleyn.hefrpc.core.utils;

import cn.hefrankeleyn.hefrpc.core.api.RpcRequest;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * @Date 2024/3/27
 * @Author lifei
 */
public class MockUtils {
    public static Object mock(RpcRequest rpcRequest) {
        try {
            Class<?> aClass = Class.forName(rpcRequest.getService());
            Method method = Arrays.stream(aClass.getMethods()).filter(o -> HefRpcMethodUtils.createMethodSign(o).equals(rpcRequest.getMethodSign()))
                    .findAny().orElse(null);
            Class<?> returnType = method.getReturnType();
            return mock(returnType, method.getGenericReturnType());
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static Object mock(Class<?> valType, Type genericValType) {
        try {
            if (Integer.class.equals(valType) || Integer.TYPE.equals(valType)) {
                return 1;
            } else if (Long.class.equals(valType) || Long.TYPE.equals(valType)) {
                return 11L;
            } else if (Short.class.equals(valType) || Short.TYPE.equals(valType)) {
                return Short.valueOf("2");
            } else if (Character.class.equals(valType) || Character.TYPE.equals(valType)) {
                return '0';
            } else if (Float.class.equals(valType) || Float.TYPE.equals(valType)) {
                return 0.1f;
            } else if (Double.class.equals(valType) || Double.TYPE.equals(valType)) {
                return 1.1d;
            } else if (Boolean.class.equals(valType) || Boolean.TYPE.equals(valType)) {
                return true;
            }
            if (String.class.equals(valType)) {
                return "this_is_a_mock_string";
            }
            return mockPojo(valType);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Object mockPojo(Class<?> valType) {
        try {
            Constructor<?> constructor = valType.getConstructor();
            Object result = constructor.newInstance();
            Field[] fields = valType.getDeclaredFields();
            for (Field field : fields) {
                Object val = mock(field.getType(), field.getGenericType());
                field.setAccessible(true);
                field.set(result, val);
            }
            return result;
        }catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
