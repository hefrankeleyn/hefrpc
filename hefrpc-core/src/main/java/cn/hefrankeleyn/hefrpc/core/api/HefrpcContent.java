package cn.hefrankeleyn.hefrpc.core.api;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @Date 2024/3/20
 * @Author lifei
 */
public class HefrpcContent<T> {

    private List<Filter> filterList = Lists.newArrayList();
    private LoadBalance<T> loadBalance;
    private Router<T> router;

    public HefrpcContent(){}

    public HefrpcContent(LoadBalance<T> loadBalance, Router<T> router) {
        this.loadBalance = loadBalance;
        this.router = router;
    }

    public List<Filter> getFilterList() {
        return filterList;
    }

    public void setFilterList(List<Filter> filterList) {
        this.filterList = filterList;
    }

    public LoadBalance<T> getLoadBalance() {
        return loadBalance;
    }

    public void setLoadBalance(LoadBalance<T> loadBalance) {
        this.loadBalance = loadBalance;
    }

    public Router<T> getRouter() {
        return router;
    }

    public void setRouter(Router<T> router) {
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
