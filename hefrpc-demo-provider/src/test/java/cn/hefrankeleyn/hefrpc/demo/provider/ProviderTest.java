package cn.hefrankeleyn.hefrpc.demo.provider;


import cn.hefrankeleyn.hefrpc.core.utils.HefRpcMethodUtils;
import cn.hefrankeleyn.hefrpc.demo.api.UserService;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 * @Date 2024/3/15
 * @Author lifei
 */
public class ProviderTest {

    @Test
    public void methodSignTest() {
        Class<UserService> userServiceClass = UserService.class;
        Method[] methods = userServiceClass.getMethods();
        for (Method method : methods) {
            String methodSign = HefRpcMethodUtils.createMethodSign(method);
            System.out.println(methodSign);
        }
    }
}
