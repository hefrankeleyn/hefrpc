package cn.hefrankeleyn.hefrpc.demo.trafficcontrol;

import com.google.common.base.Strings;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Date 2024/4/29
 * @Author lifei
 */
public class SingleThreadTokenBucketTCTest {
    private static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private static final int trafficControl = 10;
    private static final long timeWindow = 1;

    private static long startTime = System.currentTimeMillis();

    private static final AtomicInteger requestCount = new AtomicInteger(trafficControl);

    static {
        scheduledExecutorService.scheduleWithFixedDelay(()->{
            requestCount.set(trafficControl);
        },0, timeWindow, TimeUnit.SECONDS);
    }

    public static synchronized boolean tryAcquire() {
        return requestCount.decrementAndGet() > 0;
    }

    @Test
    public void testTC() {
        try {
            // 正常每30s限流15次
            for (int j = 0; j < 5; j++) {
                Thread.sleep(1000*1);
                for (int i = 0; i < 20; i++) {
                    if (tryAcquire()) {
                        System.out.println(Strings.lenientFormat("%s: 执行成功,OK!", i));
                    } else {
                        System.out.println(Strings.lenientFormat("%s: 被限流了!!!!", i));
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
