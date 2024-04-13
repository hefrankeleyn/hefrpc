package cn.hefrankeleyn.hefrpc.demo.provider;

import org.apache.curator.test.TestingServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HefrpcDemoProviderApplicationTests {

	private static TestingServer testingServer;

	@BeforeAll
	public static void init() {
		try {
			testingServer = new TestingServer(2182);
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@AfterAll
	public static void stop() {
		try {
			testingServer.stop();
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	@Test
	void contextLoads() {
		System.out.println("aaa");
	}

}
