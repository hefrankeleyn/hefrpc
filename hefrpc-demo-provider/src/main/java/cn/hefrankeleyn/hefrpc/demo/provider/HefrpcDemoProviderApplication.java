package cn.hefrankeleyn.hefrpc.demo.provider;

import cn.hefrankeleyn.hefrpc.core.api.RpcRequest;
import cn.hefrankeleyn.hefrpc.core.api.RpcResponse;
import cn.hefrankeleyn.hefrpc.core.conf.ProviderConf;
import cn.hefrankeleyn.hefrpc.core.provider.ProviderBootstrap;
import cn.hefrankeleyn.hefrpc.core.provider.ProviderInvoker;
import cn.hefrankeleyn.hefrpc.demo.api.UserService;
import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.*;


@SpringBootApplication
@RestController
@Import(ProviderConf.class)
public class HefrpcDemoProviderApplication {

	@Resource
	private UserService userService;

	@Resource
	private ProviderInvoker providerInvoker;

	public static void main(String[] args) {
		SpringApplication.run(HefrpcDemoProviderApplication.class, args);
	}

	// 使用HTTP + JSON实现序列化
	@RequestMapping(value = "/")
	public RpcResponse<Object> invoke(@RequestBody RpcRequest request) {
		return providerInvoker.invoke(request);
	}

	@RequestMapping(value = "/updateTimeoutPorts", method = RequestMethod.GET)
	public RpcResponse<String> updateTimeoutPorts(@RequestParam("timeoutPorts") String timeoutPorts) {
		this.userService.updateTimeoutPorts(timeoutPorts);
		RpcResponse<String> result = new RpcResponse<>();
		result.setStatus(true);
		result.setData("success");
		return result;
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
			RpcResponse<Object> response = providerInvoker.invoke(request);
			System.out.println(response.getData());
		};
	}
}
