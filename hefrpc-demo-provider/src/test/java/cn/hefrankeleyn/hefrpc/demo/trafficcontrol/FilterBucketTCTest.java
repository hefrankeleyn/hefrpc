package cn.hefrankeleyn.hefrpc.demo.trafficcontrol;

import com.google.common.base.Strings;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Date 2024/4/27
 * @Author lifei
 */
public class FilterBucketTCTest {
    // 上次桶漏的时间
    private static long lastTime = System.currentTimeMillis();
    // 每秒流水速度，
    private static final int rate = 10;
    private static final int trafficControl = rate;
    // 桶的容量，同时
    private static final int capacity = 10;
    private static final AtomicInteger requestCount = new AtomicInteger(0);

    @Test
    public void test01() {
        int v01 = (int)Math.ceil(9.1d);
        System.out.println(v01);
    }

    public static synchronized boolean tryAcquire() {
        // 如果是空桶，直接漏下去
        if (requestCount.get()==0) {
            lastTime = System.currentTimeMillis();
            requestCount.incrementAndGet();
            return false;
        }
        long now = System.currentTimeMillis();
        // 执行漏水
        // 两次请求之间流了多少水
        int timeOff = (int)(now - lastTime)/1000;
        int waterLeaked = timeOff * rate;
        // 桶中剩余的水
        int waterLeft = Math.max(0, requestCount.get() - waterLeaked);
        requestCount.set(waterLeft);
        lastTime = now;
//        System.out.println(Strings.lenientFormat("漏水量：x%s, 剩余水量：%s", waterLeaked, requestCount.get()));
        if (requestCount.get() < capacity) {
            requestCount.incrementAndGet();
            return false;
        }else {
            return true;
        }
    }

    @Test
    public void testTC() {
        try {
            for (int j = 0; j < 5; j++) {
                Thread.sleep(1000 * 2);
                int x=0;
                for (int i = 0; i < 20; i++) {
                    x += 1;
                    if (!tryAcquire()) {
                        System.out.println(Strings.lenientFormat("%s: 执行成功,OK!", x));
                    } else {
                        System.out.println(Strings.lenientFormat("%s,: 被限流了!!!!", x));
                    }

                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
