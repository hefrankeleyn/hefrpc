package cn.hefrankeleyn.hefrpc.core.api;

import cn.hefrankeleyn.hefrpc.core.meta.InstanceMeta;
import cn.hefrankeleyn.hefrpc.core.meta.ServiceMeta;
import cn.hefrankeleyn.hefrpc.core.registry.ChangedListener;

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
    void register(ServiceMeta serviceMeta, InstanceMeta instance);

    // provider端：取消注册
    void unregister(ServiceMeta serviceMeta, InstanceMeta instance);

    // consumer端： 获取某个服务端所有事例
    List<InstanceMeta> findAll(ServiceMeta serviceMeta);

    // consumer 端：订阅， 监听节点变化
    void subscribe(ServiceMeta service, ChangedListener changedListener);

    class StaticRegistryCenter implements RegistryCenter {

        private List<InstanceMeta> providers;

        public StaticRegistryCenter(List<InstanceMeta> providers) {
            this.providers = providers;
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void register(ServiceMeta serviceMeta, InstanceMeta instance) {

        }

        @Override
        public void unregister(ServiceMeta serviceMeta, InstanceMeta instance) {

        }

        @Override
        public List<InstanceMeta> findAll(ServiceMeta serviceMeta) {
            return providers;
        }

        @Override
        public void subscribe(ServiceMeta serviceMeta, ChangedListener changedListener) {

        }
    }
}
