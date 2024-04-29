package cn.hefrankeleyn.hefrpc.demo.trafficcontrol;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.junit.Test;

import java.util.Set;
import java.util.TreeMap;

/**
 * @Date 2024/4/27
 * @Author lifei
 */
public class SlidingLogTCTest {

    private static final int trafficControl = 10;
    private static final long timeWindow = 1000 * 20;
    private static final long cleanTimeWindow = 1000 * 60;
    private static final TreeMap<Long, Integer> slidingLogMap = Maps.newTreeMap();

    public static synchronized boolean tryAcquire() {
        long now = System.currentTimeMillis();
        // 清理
        if (!slidingLogMap.isEmpty() && slidingLogMap.firstKey() - now > cleanTimeWindow) {
            Set<Long> cleanKeys = slidingLogMap.subMap(0L, now - timeWindow).keySet();
            for (Long cleanKey : cleanKeys) {
                slidingLogMap.remove(cleanKey);
            }
        }

        // 判断
        Set<Long> tcKeys = slidingLogMap.subMap(now - timeWindow, now).keySet();
        int sum = 0;
        for (Long tcKey : tcKeys) {
            sum += slidingLogMap.get(tcKey);
        }
        System.out.println(Strings.lenientFormat("key size: %s, sub size: %s", slidingLogMap.size(), tcKeys.size()));
        // 从0 计数，所以这里要加1
        if (sum + 1>trafficControl) {
            return true;
        }
        if (slidingLogMap.containsKey(now)) {
            slidingLogMap.put(now, slidingLogMap.get(now) + 1);
        } else {
            slidingLogMap.put(now, 1);
        }
        return false;
    }

    @Test
    public void testTC() {
        try {
            // 即便加上这行代码也不影响
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
