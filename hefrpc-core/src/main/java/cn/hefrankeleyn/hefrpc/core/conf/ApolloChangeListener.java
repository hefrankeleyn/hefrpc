package cn.hefrankeleyn.hefrpc.core.conf;

import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import org.springframework.beans.BeansException;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Date 2024/5/9
 * @Author lifei
 */
public class ApolloChangeListener implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @ApolloConfigChangeListener(value = "${apollo.bootstrap.namespaces}")
    public void refresh(ConfigChangeEvent configChangeEvent) {
//        applicationContext.getBean(ConfigurationPropertiesRebinder.class)
        applicationContext.publishEvent(new EnvironmentChangeEvent(configChangeEvent.changedKeys()));
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
