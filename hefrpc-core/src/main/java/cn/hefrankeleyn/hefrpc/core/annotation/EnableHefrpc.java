package cn.hefrankeleyn.hefrpc.core.annotation;

import cn.hefrankeleyn.hefrpc.core.conf.ConsumerConf;
import cn.hefrankeleyn.hefrpc.core.conf.ProviderConf;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Import({ProviderConf.class, ConsumerConf.class})
public @interface EnableHefrpc {
}
