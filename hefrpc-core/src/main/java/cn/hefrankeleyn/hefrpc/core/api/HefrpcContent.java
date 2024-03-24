package cn.hefrankeleyn.hefrpc.core.api;

import cn.hefrankeleyn.hefrpc.core.meta.InstanceMeta;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @Date 2024/3/20
 * @Author lifei
 */
public class HefrpcContent {

    private List<Filter> filterList = Lists.newArrayList();
    private LoadBalance<InstanceMeta> loadBalance;
    private Router<InstanceMeta> router;

    public HefrpcContent(){}

    public HefrpcContent(LoadBalance<InstanceMeta> loadBalance, Router<InstanceMeta> router) {
        this.loadBalance = loadBalance;
        this.router = router;
    }

    public List<Filter> getFilterList() {
        return filterList;
    }

    public void setFilterList(List<Filter> filterList) {
        this.filterList = filterList;
    }

    public LoadBalance<InstanceMeta> getLoadBalance() {
        return loadBalance;
    }

    public void setLoadBalance(LoadBalance<InstanceMeta> loadBalance) {
        this.loadBalance = loadBalance;
    }

    public Router<InstanceMeta> getRouter() {
        return router;
    }

    public void setRouter(Router<InstanceMeta> router) {
        this.router = router;
    }

    @Override
    public String toString() {
        return "HefrpcContent{" +
                "filterList=" + filterList +
                ", loadBalance=" + loadBalance +
                ", router=" + router +
                '}';
    }
}
