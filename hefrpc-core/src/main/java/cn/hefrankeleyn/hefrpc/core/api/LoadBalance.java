package cn.hefrankeleyn.hefrpc.core.api;

import java.util.List;
import java.util.Objects;

public interface LoadBalance<T> {

    /**
     * 从多个服务提供者中选择一个
     * @param urls
     * @return
     */
    T choose(List<T> urls);

    LoadBalance DEFAULT = (urls)->(Objects.isNull(urls) || urls.size()==0)? null : urls.get(0);

}
