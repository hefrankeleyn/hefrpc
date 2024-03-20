package cn.hefrankeleyn.hefrpc.core.cluster;

import cn.hefrankeleyn.hefrpc.core.api.LoadBalance;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Date 2024/3/20
 * @Author lifei
 */
public class RoundRibonLoadBalance<T> implements LoadBalance<T> {

    private AtomicInteger atomicInteger = new AtomicInteger(0);
    @Override
    public T choose(List<T> urls) {
        if (Objects.isNull(urls) || urls.size()==0) {
            return null;
        }
        if (urls.size()==0) return urls.get(0);
        return urls.get((atomicInteger.getAndIncrement()&0x7fffffff)%urls.size());
    }
}
