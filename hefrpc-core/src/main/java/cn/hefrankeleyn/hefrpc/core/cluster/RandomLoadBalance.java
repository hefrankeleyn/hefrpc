package cn.hefrankeleyn.hefrpc.core.cluster;

import cn.hefrankeleyn.hefrpc.core.api.LoadBalance;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * 随机选择
 * @Date 2024/3/20
 * @Author lifei
 */
public class RandomLoadBalance<T> implements LoadBalance<T> {

    private final Random random = new Random(System.currentTimeMillis());
    @Override
    public T choose(List<T> urls) {
        if (Objects.isNull(urls) || urls.size()==0) {
            return null;
        }
        if (urls.size()==1) {
            return urls.get(0);
        }
        return urls.get(random.nextInt(urls.size()));
    }
}
