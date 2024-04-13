package cn.hefrankeleyn.hefrpc.demo.provider;

import cn.hefrankeleyn.hefrpc.core.conf.ProviderGrayConf;
import org.apache.curator.test.TestingServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Date 2024/4/9
 * @Author lifei
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProviderConfTest {

    @Autowired
    private ProviderGrayConf providerGrayConf;


    private static TestingServer testingServer;

    @BeforeClass
    public static void init() {
        try {
            testingServer = new TestingServer(2182);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void test01() {
        System.out.println(providerGrayConf.getMetas());
    }

    @AfterClass
    public static void stop() {
        try {
            testingServer.stop();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
