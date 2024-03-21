package cn.hefrankeleyn.hefrpc.core.api;

import java.util.List;

/**
 * @Date 2024/3/20
 * @Author lifei
 */
public interface RegistryCenter {

    // 启动zk客户端
    void start();
    // 停止zk客户端
    void stop();

    // provider端：注册服务, service 表示服务，instance 表示当前节点
    void register(String service, String instance);

    // provider端：取消注册
    void unregister(String service, String instance);

    // consumer端： 获取某个服务端所有事例
    List<String> findAll(String service);

    // consumer 端：订阅， 监听节点变化
    void subscribe();

    class StaticRegistryCenter implements RegistryCenter {

        private List<String> providers;

        public StaticRegistryCenter(List<String> providers) {
            this.providers = providers;
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void register(String service, String instance) {

        }

        @Override
        public void unregister(String service, String instance) {

        }

        @Override
        public List<String> findAll(String service) {
            return providers;
        }

        @Override
        public void subscribe() {

        }
    }
}
