package cn.hefrankeleyn.hefrpc.core.registry;

import cn.hefrankeleyn.hefrpc.core.api.RegistryCenter;
import com.google.common.base.Strings;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.List;
import java.util.Objects;

/**
 * @Date 2024/3/21
 * @Author lifei
 */
public class ZkRegistryCenter implements RegistryCenter {

    private CuratorFramework client = null;
    private TreeCache treeCache=null;
    @Override
    public void start() {
        // 间隔1秒，重试次数3次
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .retryPolicy(retryPolicy)
                .connectString("localhost:2181")
                .namespace("hefrpc")
                .build();
        client.start();
        System.out.println("====>> zk client start.");
    }

    @Override
    public void stop() {
        System.out.println("====>> stop zk.");
        if (Objects.nonNull(treeCache)) {
            treeCache.close();
        }
        client.close();
    }

    @Override
    public void register(String service, String instance) {
        try {
            String servicePath = Strings.lenientFormat("/%s", service);
            // 创建服务的持久化节点
            if (Objects.isNull(client.checkExists().forPath(servicePath))) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(servicePath, "service".getBytes());
            }
            // 创建实例的临时节点
            String instancePath = Strings.lenientFormat("%s/%s", servicePath, instance);
            if (Objects.isNull(client.checkExists().forPath(instancePath))) {
                client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath, "provider".getBytes());
            }
            System.out.println("====>> register to zk : " + instancePath);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unregister(String service, String instance) {
        try {
            String servicePath = Strings.lenientFormat("/%s", service);
            // 如果持久化节点不存在，直接返回
            if (Objects.isNull(client.checkExists().forPath(servicePath))) {
                return;
            }
            // 删除临时节点
            String instancePath = Strings.lenientFormat("%s/%s", servicePath, instance);
            client.delete().quietly().forPath(instancePath);
            System.out.println("====>> unregister from zk : " + instancePath);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> findAll(String service) {
        try {
            String servicePath = Strings.lenientFormat("/%s", service);
            List<String> nodes = client.getChildren().forPath(servicePath);
            System.out.println("===> findAll from zk : " + servicePath);
            nodes.forEach(System.out::println);
            return nodes;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void subscribe(String service, ChangedListener changedListener) {
        try {
            String servicePath = Strings.lenientFormat("/%s", service);
            treeCache = TreeCache.newBuilder(client, servicePath)
                    .setCacheData(true) // 打开缓存，避免频繁交互
                    .setMaxDepth(2) // 设置最大的深度
                    .build();
            treeCache.getListenable().addListener((cf, event) -> {
                // 节点变动，会执行
                System.out.println("===> zk subscribe event: " + event);
                List<String> nodes = findAll(service);
                changedListener.fire(new Event(nodes));
            });
            treeCache.start();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
