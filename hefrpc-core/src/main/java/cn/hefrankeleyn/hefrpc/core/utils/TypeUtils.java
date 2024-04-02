package cn.hefrankeleyn.hefrpc.core.utils;

import cn.hefrankeleyn.hefrpc.core.api.RpcResponse;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

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

    public static Object castFastJsonReturnObject(Method method, Object object) {
        if (Objects.isNull(object)) {
            return null;
        }
        Class<?> returnType = method.getReturnType();
        Type genericReturnType = method.getGenericReturnType();
        return castFastJsonObj(object, returnType, genericReturnType);
    }

    private static Object castFastJsonObj(Object origin, Class<?> valType, Type genericType) {
        if (Objects.isNull(origin)) {
            return null;
        }
        if (Integer.class.equals(valType) || Integer.TYPE.equals(valType)) {
            return Integer.valueOf(origin.toString());
        } else if (Short.class.equals(valType) || Short.TYPE.equals(valType)) {
            return Short.valueOf(origin.toString());
        } else if (Character.class.equals(valType) || Character.TYPE.equals(valType)) {
            return Character.valueOf(origin.toString().charAt(0));
        } else if (Long.class.equals(valType) || Long.TYPE.equals(valType)) {
            return Long.valueOf(origin.toString());
        } else if (Float.class.equals(valType) || Float.TYPE.equals(valType)) {
            return Float.valueOf(origin.toString());
        } else if (Double.class.equals(valType) || Double.TYPE.equals(valType)) {
            return Double.valueOf(origin.toString());
        } else if (Boolean.class.equals(valType) || Boolean.TYPE.equals(valType)) {
            return Boolean.valueOf(origin.toString());
        }

        if (origin instanceof JSONObject jsonObject) {
            if (Map.class.isAssignableFrom(valType)) {
                Map resMap = new HashMap();
                if (genericType instanceof ParameterizedType parameterizedType) {
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    Class<?> keyClass = (Class<?>) actualTypeArguments[0];
                    Class<?> valueClass = (Class<?>) actualTypeArguments[1];
                    for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                        Object mapKey = castFastJsonObj(entry.getKey(), keyClass, keyClass.getGenericSuperclass());
                        Object mapValue = castFastJsonObj(entry.getValue(), valueClass, valueClass.getGenericSuperclass());
                        resMap.put(mapKey, mapValue);
                    }
                } else {
                    resMap = jsonObject.toJavaObject(Map.class);
                }
                return resMap;
            } else {
                return jsonObject.toJavaObject(valType);
            }
        } else if (origin instanceof JSONArray jsonArray) {
            Object[] array = jsonArray.toArray();
            if (valType.isArray()) {
                Class<?> componentType = valType.getComponentType();
                Object arrRes = Array.newInstance(componentType, array.length);
                for (int i = 0; i < array.length; i++) {
                    Array.set(arrRes, i, castFastJsonObj(array[i], componentType, componentType.getGenericSuperclass()));
                }
                return arrRes;
            } else if (List.class.isAssignableFrom(valType)) {
                List listRes = new ArrayList(array.length);
                if (genericType instanceof ParameterizedType parameterizedType) {
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    Class<?> itemType = (Class<?>)actualTypeArguments[0];
                    for (Object oneVal : array) {
                        listRes.add(castFastJsonObj(oneVal, itemType, itemType.getGenericSuperclass()));
                    }
                } else {
                    for (Object oneVal : array) {
                        listRes.add(oneVal);
                    }
                }
                return listRes;
            } else {
                return null;
            }
        }
        if (valType.isAssignableFrom(origin.getClass())) {
            return origin;
        }
        return JSONObject.parseObject(origin.toString(), valType);
    }

    public static RpcResponse<?> getRpcResponse(Method method, RpcResponse<?> rpcResponse) throws IOException {
        Gson gson = new Gson();
        String dataStr = gson.toJson(rpcResponse);
        Class<?> realType = TypeUtils.cast(method.getReturnType());
        TypeToken<?> parameterized = TypeToken.getParameterized(RpcResponse.class, realType);
        // 处理返回值是List的情况
        Type genericReturnType = method.getGenericReturnType();
        if (List.class.isAssignableFrom(realType)) {
            if (genericReturnType instanceof ParameterizedType parameterizedType) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments.length>0) {
                    Type actualTypeArgument = actualTypeArguments[0];
                    TypeToken<?> listType = TypeToken.getParameterized(List.class, actualTypeArgument);
                    parameterized = TypeToken.getParameterized(RpcResponse.class, listType.getType());
                }
            }
        }
        if (Map.class.isAssignableFrom(realType)) {
            if (genericReturnType instanceof ParameterizedType parameterizedType) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments!=null && actualTypeArguments.length>0) {
                    TypeToken<?> listType = TypeToken.getParameterized(Map.class, actualTypeArguments);
                    parameterized = TypeToken.getParameterized(RpcResponse.class, listType.getType());
                }
            }
        }
        return gson.fromJson(dataStr, parameterized.getType());
    }
}
