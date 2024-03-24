package cn.hefrankeleyn.hefrpc.core.registry.zk;

import cn.hefrankeleyn.hefrpc.core.api.RegistryCenter;
import cn.hefrankeleyn.hefrpc.core.meta.InstanceMeta;
import cn.hefrankeleyn.hefrpc.core.meta.ServiceMeta;
import cn.hefrankeleyn.hefrpc.core.registry.ChangedListener;
import cn.hefrankeleyn.hefrpc.core.registry.Event;
import com.google.common.base.Strings;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Date 2024/3/21
 * @Author lifei
 */
public class ZkRegistryCenter implements RegistryCenter {

    private CuratorFramework client = null;
    private TreeCache treeCache=null;

    @Value("${hefrpc.zkservers}")
    private String zkServers;

    @Value("${hefrpc.zkroot}")
    private String zkRoot;
    @Override
    public void start() {
        // 间隔1秒，重试次数3次
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .retryPolicy(retryPolicy)
                .connectString(zkServers)
                .namespace(zkRoot)
                .build();
        client.start();
        System.out.println(Strings.lenientFormat("====>> zk client start to server [ %s/%s].", zkServers, zkRoot));
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
    public void register(ServiceMeta serviceMeta, InstanceMeta instance) {
        try {
            String servicePath = Strings.lenientFormat("/%s", serviceMeta.toPath());
            // 创建服务的持久化节点
            if (Objects.isNull(client.checkExists().forPath(servicePath))) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(servicePath, "service".getBytes());
            }
            // 创建实例的临时节点
            String instancePath = createInstancePath(servicePath, instance);
            if (Objects.isNull(client.checkExists().forPath(instancePath))) {
                client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath, "provider".getBytes());
            }
            System.out.println("====>> register to zk : " + instancePath);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unregister(ServiceMeta serviceMeta, InstanceMeta instance) {
        try {
            String servicePath = Strings.lenientFormat("/%s", serviceMeta.toPath());
            // 如果持久化节点不存在，直接返回
            if (Objects.isNull(client.checkExists().forPath(servicePath))) {
                return;
            }
            // 删除临时节点
            String instancePath = createInstancePath(servicePath, instance);
            client.delete().quietly().forPath(instancePath);
            System.out.println("====>> unregister from zk : " + instancePath);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String createInstancePath(String servicePath, InstanceMeta instance) {
        return Strings.lenientFormat("%s/%s", servicePath, instance.toPath());
    }

    @Override
    public List<InstanceMeta> findAll(ServiceMeta serviceMeta) {
        try {
            String servicePath = Strings.lenientFormat("/%s", serviceMeta.toPath());
            List<String> nodes = client.getChildren().forPath(servicePath);
            List<InstanceMeta> instanceList = nodes.stream().map(node -> new InstanceMeta("http", node.split("_")[0], Integer.parseInt(node.split("_")[1])))
                    .collect(Collectors.toList());
            System.out.println("===> findAll from zk : " + servicePath);
            instanceList.forEach(System.out::println);
            return instanceList;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void subscribe(ServiceMeta serviceMeta, ChangedListener changedListener) {
        try {
            String servicePath = Strings.lenientFormat("/%s", serviceMeta.toPath());
            treeCache = TreeCache.newBuilder(client, servicePath)
                    .setCacheData(true) // 打开缓存，避免频繁交互
                    .setMaxDepth(2) // 设置最大的深度
                    .build();
            treeCache.getListenable().addListener((cf, event) -> {
                // 节点变动，会执行
                System.out.println("===> zk subscribe event: " + event);
                List<InstanceMeta> instanceMetaList = findAll(serviceMeta);
                changedListener.fire(new Event(instanceMetaList));
            });
            treeCache.start();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
