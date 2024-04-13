package cn.hefrankeleyn.hefrpc.core.cluster;

import cn.hefrankeleyn.hefrpc.core.api.Router;
import cn.hefrankeleyn.hefrpc.core.meta.InstanceMeta;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * @Date 2024/4/13
 * @Author lifei
 */
public class GrayRouter implements Router<InstanceMeta> {

    private static final Logger log = LoggerFactory.getLogger(GrayRouter.class);


    private int grayRatio;

    private final static Random RANDOM = new Random(System.currentTimeMillis());

    public GrayRouter(int grayRatio) {
        this.grayRatio = grayRatio;
    }

    @Override
    public List<InstanceMeta> route(List<InstanceMeta> providers) {
        if (Objects.isNull(providers) || providers.isEmpty()) {
            return providers;
        }
        List<InstanceMeta> grayProviders = Lists.newArrayList();
        List<InstanceMeta> nonmalProviders = Lists.newArrayList();
        for (InstanceMeta provider : providers) {
            if (Objects.nonNull(provider.getParameters())
                    && provider.getParameters().containsKey("gray")
                    && Boolean.parseBoolean(provider.getParameters().get("gray"))) {
                grayProviders.add(provider);
            } else {
                nonmalProviders.add(provider);
            }
        }

        log.debug("==> GrayRouter nonmalProviders/grayProviders grayRatio: {}, {}, {}",
                nonmalProviders.size(), grayProviders.size(), grayRatio);

        if (grayProviders.isEmpty() || nonmalProviders.isEmpty()) {
            return providers;
        }

        if (grayRatio<=0) {
            return nonmalProviders;
        } else if (grayRatio>=100) {
            return grayProviders;
        }

        if (RANDOM.nextInt(100) < grayRatio) {
            log.debug("==> GrayRouter grayNode: {}", grayProviders);
            return grayProviders;
        } else {
            log.debug("==> GrayRouter nonmalNode: {}", nonmalProviders);
            return nonmalProviders;
        }
    }

    public void setGrayRatio(int grayRatio) {
        this.grayRatio = grayRatio;
    }
}
