package cn.hefrankeleyn.hefrpc.demo.trafficcontrol;

import com.google.common.base.Strings;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Date 2024/4/27
 * @Author lifei
 */
public class FixTimeWindowTCTest {

    // 限流阈值
    private final static int trafficControl = 10;
    // 限流的时间窗口
    private final static long fixTimeWindow = 1000 * 20;
    // 开始时间
    private static long startTime = System.currentTimeMillis();

    private static final AtomicInteger requestCount = new AtomicInteger(0);

    // 判断是否限流
    public static synchronized boolean tryAcquire() {
        if (System.currentTimeMillis() - startTime > fixTimeWindow) {
            requestCount.set(0);
            startTime = System.currentTimeMillis();
        }
        return requestCount.incrementAndGet() > trafficControl;
    }

    @Test
    public void testTC() {
        try {
            // 加上这行代码就会变了
            Thread.sleep(1000*10);
            // 正常每30s限流15次
            for (int i = 0, j = i; i < 100; i++) {

                Thread.sleep(1000);
                if (!tryAcquire()) {
                    j += 1;
                    int successTimes = j % trafficControl == 0 ? trafficControl : j % trafficControl;
                    System.out.println(Strings.lenientFormat("%s, %s: 执行成功,OK!", i + 1, successTimes));
                } else {
                    System.out.println(Strings.lenientFormat("%s, %s: 被限流了!!!!", i + 1, (i + 1) % trafficControl));
                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
