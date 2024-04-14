package cn.hefrankeleyn.hefrpc.core.api;

import cn.hefrankeleyn.hefrpc.core.meta.InstanceMeta;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * @Date 2024/3/20
 * @Author lifei
 */
public class HefrpcContent {

    private List<Filter> filterList = Lists.newArrayList();
    private LoadBalance<InstanceMeta> loadBalance;
    private Router<InstanceMeta> router;

    private Map<String, String> parameters = Maps.newHashMap();

    public static ThreadLocal<Map<String, String>> contextParameters = new ThreadLocal<>() {
        @Override
        public Map<String, String> initialValue() {
            return Maps.newHashMap();
        }
    };

    public static void setContextParameter(String key, String value) {
        contextParameters.get().put(key, value);
    }

    public static String getContextParameter(String key) {
        return contextParameters.get().get(key);
    }

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

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "HefrpcContent{" +
                "filterList=" + filterList +
                ", loadBalance=" + loadBalance +
                ", router=" + router +
                ", parameters=" + parameters +
                '}';
    }
}
