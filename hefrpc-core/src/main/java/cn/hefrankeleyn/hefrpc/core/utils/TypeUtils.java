package cn.hefrankeleyn.hefrpc.core.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.val;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Date 2024/3/18
 * @Author lifei
 */
public class TypeUtils {

    public static Class<?> cast(Class<?> type) {
        if (type.equals(Long.TYPE)) {
            return Long.class;
        } else if (type.equals(Integer.TYPE)) {
            return Integer.class;
        } else if (type.equals(Short.TYPE)) {
            return Short.class;
        } else if (type.equals(Character.TYPE)) {
            return Character.class;
        } else if (type.equals(Boolean.TYPE)) {
            return Boolean.class;
        } else if (type.equals(Float.TYPE)) {
            return Float.class;
        } else if (type.equals(Double.TYPE)) {
            return Double.class;
        } else {
            return type;
        }
    }

    public static Object cast(Object arg, Class<?> type, Type genericParameterType) {
        try {
            if (Objects.isNull(arg)) {
                return null;
            }
            Class<?> aClass = arg.getClass();
            if (aClass.isAssignableFrom(type)) {
                return arg;
            }
            if (type.equals(Integer.class) || type.equals(Integer.TYPE)) {
                return Integer.valueOf(arg.toString());
            } else if (type.equals(Short.class) || type.equals(Short.TYPE)) {
                return Short.valueOf(arg.toString());
            } else if (type.equals(Character.class) || type.equals(Character.TYPE)) {
                return Character.valueOf(arg.toString().charAt(0));
            } else if (type.equals(Long.class) || type.equals(Long.TYPE)) {
                return Long.valueOf(arg.toString());
            } else if (type.equals(Double.class) || type.equals(Double.TYPE)) {
                return Double.valueOf(arg.toString());
            } else if (type.equals(Float.class) || type.equals(Float.TYPE)) {
                return Float.valueOf(arg.toString());
            } else if (type.equals(Boolean.class) || type.equals(Boolean.TYPE)) {
                return Boolean.valueOf(arg.toString());
            }
            Object result = arg;
            // 第一种方案：使用Gson进行反序列化
            Gson gson = new Gson();
            String valStr = gson.toJson(arg);
            if (List.class.isAssignableFrom(type)) {
                if (genericParameterType instanceof ParameterizedType parameterizedType) {
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    if (Objects.nonNull(actualTypeArguments) && actualTypeArguments.length>0) {
                        Type actualTypeArgument = actualTypeArguments[0];
                        TypeToken<?> parameterized = TypeToken.getParameterized(List.class, actualTypeArgument);
                        result = gson.fromJson(valStr, parameterized.getType());
                        return result;
                    }
                }
            }
            if (Map.class.isAssignableFrom(type)) {
                if (genericParameterType instanceof ParameterizedType parameterizedType) {
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    TypeToken<?> parameterized = TypeToken.getParameterized(Map.class, actualTypeArguments);
                    result = gson.fromJson(valStr, parameterized.getType());
                    return result;
                }
            }
            result = gson.fromJson(valStr, type);
            // // 第二种方案
//            if (arg instanceof HashMap map) {
//                Object result = type.getConstructor().newInstance();
//                for (Object key : map.keySet()) {
//                    if (Objects.isNull(map.get(key))) {
//                        continue;
//                    }
//                    Object currentVal = map.get(key);
//                    Field declaredField = type.getDeclaredField(key.toString());
//                    declaredField.setAccessible(true);
//                    declaredField.set(result, currentVal);
//                }
//                return result;
//            }
//            if (type.isArray()) {
//                if (arg instanceof List<?> list) {
//                    arg = list.toArray();
//                }
//                int len = Array.getLength(arg);
//                Object result = Array.newInstance(type.getComponentType(), len);
//                for (int i = 0; i < len; i++) {
//                    Array.set(result, i, cast(Array.get(arg, i), type.getComponentType()));
//                }
//                return result;
//            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 转化参数
     *
     * @param args
     * @param parameterTypes
     * @return
     */
    public static Object[] processArgs(Object[] args, Class<?>[] parameterTypes, Type[] genericParameterTypes) {
        if (args == null || args.length == 0) {
            return args;
        }
        Object[] result = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            result[i] = cast(args[i], parameterTypes[i], genericParameterTypes[i]);
        }
        return result;
    }
}
