package cn.hefrankeleyn.hefrpc.demo.trafficcontrol;

import com.google.common.base.Strings;
import com.google.common.util.concurrent.RateLimiter;
import org.junit.Test;

/**
 * @Date 2024/4/29
 * @Author lifei
 */
public class TokenBucketTCTest {

    @Test
    public void testTC() {
        // 使用Guava，每秒放10个令牌
        RateLimiter rateLimiter = RateLimiter.create(10);
        try {
            // 正常每30s限流15次
            for (int j = 0; j < 5; j++) {
                Thread.sleep(1000*1);
                for (int i = 0; i < 20; i++) {
                    if (rateLimiter.tryAcquire()) {
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
