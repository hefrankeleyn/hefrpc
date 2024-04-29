package cn.hefrankeleyn.hefrpc.demo.trafficcontrol;

import cn.hefrankeleyn.hefrpc.core.governance.SlidingTimeWindow;
import com.google.common.base.Strings;
import org.junit.Test;

/**
 * @Date 2024/4/27
 * @Author lifei
 */
public class SlidingTimeWindowTCTest {

    private static final int trafficControl = 10;
    private static final SlidingTimeWindow slidingTimeWindow = new SlidingTimeWindow(20);

    public static synchronized boolean tryAcquire() {
        if (slidingTimeWindow.calcSum()>trafficControl) {
            return true;
        }
        slidingTimeWindow.record(System.currentTimeMillis());
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
