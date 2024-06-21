package cn.hefrankeleyn.hefrpc.demo.provider;

import cn.hefrankeleyn.hefrpc.core.annotation.EnableHefrpc;
import cn.hefrankeleyn.hefrpc.core.api.RpcRequest;
import cn.hefrankeleyn.hefrpc.core.api.RpcResponse;
import cn.hefrankeleyn.hefrpc.core.conf.ProviderBusConf;
import cn.hefrankeleyn.hefrpc.core.conf.ProviderConf;
import cn.hefrankeleyn.hefrpc.core.provider.ProviderInvoker;
import cn.hefrankeleyn.hefrpc.demo.api.UserService;
import io.github.hefrankeleyn.hefconfig.client.annotation.EnableHefConfigAnnotation;
import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.*;


@SpringBootApplication
@RestController
@EnableHefConfigAnnotation
@Import(ProviderConf.class)
public class HefrpcDemoProviderApplication {

	@Resource
	private UserService userService;

	@Resource
	private ProviderInvoker providerInvoker;

	public static void main(String[] args) {
		SpringApplication.run(HefrpcDemoProviderApplication.class, args);
	}



	@RequestMapping(value = "/updateTimeoutPorts", method = RequestMethod.GET)
	public RpcResponse<String> updateTimeoutPorts(@RequestParam("timeoutPorts") String timeoutPorts) {
		this.userService.updateTimeoutPorts(timeoutPorts);
		RpcResponse<String> result = new RpcResponse<>();
		result.setStatus(true);
		result.setData("success");
		return result;
	}

	@Resource
	private ProviderBusConf providerBusConf;

	@RequestMapping(value = "/findProviderBusConf")
	public String findProviderBusConf() {
		return providerBusConf.getMetas().toString();
	}

	/**
	 * 模拟远程调用
	 * @return
	 */
	@Bean
	public ApplicationRunner providerRun() {
		return x -> {
			System.out.println("run 方法运行了");
			RpcRequest request = new RpcRequest();
			request.setService("cn.hefrankeleyn.hefrpc.demo.api.UserService");
			request.setMethodSign("findById#int");
			request.setArgs(new Object[]{100});
//			for (int i = 0; i < 100; i++) {
//				try {
//					int times = i + 1;
//					int loopTimes = times%30;
//					Thread.sleep(1000);
//					RpcResponse<Object> response = providerInvoker.invoke(request);
//
//					if (response.isStatus()) {
//						System.out.println(Strings.lenientFormat("times: %s, loopTimes: %s, =====> %s",times, loopTimes, response.getData()));
//					}else {
//						System.out.println(Strings.lenientFormat("times: %s, loopTimes: %s, =====> %s",times, loopTimes, response.getEx().getMessage()));
//					}
//				}catch (Exception e) {
//					throw new RuntimeException(e);
//				}
//			}
		};
	}
}
