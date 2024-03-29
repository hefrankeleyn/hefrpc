package cn.hefrankeleyn.hefrpc.demo.provider;

import cn.hefrankeleyn.hefrpc.core.api.RpcRequest;
import cn.hefrankeleyn.hefrpc.core.api.RpcResponse;
import cn.hefrankeleyn.hefrpc.core.conf.ProviderConf;
import cn.hefrankeleyn.hefrpc.core.provider.ProviderBootstrap;
import cn.hefrankeleyn.hefrpc.core.provider.ProviderInvoker;
import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@RestController
@Import(ProviderConf.class)
public class HefrpcDemoProviderApplication {

	@Resource
	private ProviderInvoker providerInvoker;

	public static void main(String[] args) {
		SpringApplication.run(HefrpcDemoProviderApplication.class, args);
	}

	// 使用HTTP + JSON实现序列化
	@RequestMapping(value = "/")
	public RpcResponse invoke(@RequestBody RpcRequest request) {
		return providerInvoker.invoke(request);
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
			RpcResponse response = providerInvoker.invoke(request);
			System.out.println(response.getData());
		};
	}
}
