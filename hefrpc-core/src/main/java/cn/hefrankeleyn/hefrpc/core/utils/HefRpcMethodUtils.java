package cn.hefrankeleyn.hefrpc.core.utils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Date 2024/3/14
 * @Author lifei
 */
public class HefRpcMethodUtils {

    // 切分参数类型
    public static final String ARGUMENT_TYPE_SPLIT = "&";
    public static final String METHOD_CONNECT_ARGS_TYPE = "#";

    /**
     * 本地方法不代理
     * @param method
     * @return
     */
    public static boolean checkLocalMethod(String method) {
        Method[] methods = Object.class.getMethods();
        Set<String> localMethodSet = Arrays.stream(methods).map(Method::getName).collect(Collectors.toSet());
        System.out.println(localMethodSet);
        return localMethodSet.contains(method);
    }

    public static boolean checkLocalMethod(Method method) {
        return method.getDeclaringClass().equals(Object.class);
    }


    /**
     * 创建方法签名
     * @param method
     * @return
     */
    public static String createMethodSign(Method method) {
        String methodName = method.getName();
        String argStr = Arrays.stream(method.getParameterTypes()).map(Class::getCanonicalName)
                .collect(Collectors.joining(ARGUMENT_TYPE_SPLIT));
        return String.format("%s%s%s", methodName, METHOD_CONNECT_ARGS_TYPE, argStr);
    }


}
